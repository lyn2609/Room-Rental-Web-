package vn.ttcs.Room_Rental.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import vn.ttcs.Room_Rental.config.VNPayConfig;
import vn.ttcs.Room_Rental.domain.Contract;
import vn.ttcs.Room_Rental.domain.Invoice;
import vn.ttcs.Room_Rental.domain.InvoiceDetail;
import vn.ttcs.Room_Rental.domain.dto.GenerateInvoiceRequest;
import vn.ttcs.Room_Rental.domain.dto.InvoiceDashboardResponse;
import vn.ttcs.Room_Rental.domain.dto.InvoiceDetailResponse;
import vn.ttcs.Room_Rental.domain.dto.InvoiceResponse;
import vn.ttcs.Room_Rental.domain.dto.UpdateInvoiceRequest;
import vn.ttcs.Room_Rental.repository.ContractRepository;
import vn.ttcs.Room_Rental.repository.InvoiceDetailRepository;
import vn.ttcs.Room_Rental.repository.InvoiceRepository;
import vn.ttcs.Room_Rental.repository.ServiceRepository;

@Service
public class InvoiceService {
    private static final int VNPAY_AMOUNT_MULTIPLIER = 100;

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final ContractRepository contractRepository;
    private final ServiceRepository serviceRepository;

    @Value("${vnpay.tmnCode}")
    private String vnpTmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnpHashSecret;

    @Value("${vnpay.url}")
    private String vnpUrl;

    @Value("${vnpay.returnUrl}")
    private String vnpReturnUrl;

    @Value("${service.type.electric:ELECTRIC}")
    private String electricType;

    @Value("${service.type.water:WATER}")
    private String waterType;

    @Value("${service.unit.electric:kWh}")
    private String electricUnit;

    @Value("${service.unit.water:m3}")
    private String waterUnit;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          InvoiceDetailRepository invoiceDetailRepository,
                          ContractRepository contractRepository,
                          ServiceRepository serviceRepository) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
        this.contractRepository = contractRepository;
        this.serviceRepository = serviceRepository;
    }

    // ==================== CLIENT ====================

    public List<InvoiceResponse> getMyInvoices(Integer userId) {
        List<Invoice> invoices = invoiceRepository.findByUserId(userId);
        return invoices.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public InvoiceResponse getMyInvoiceDetail(Integer invoiceId, Integer userId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));

        Integer ownerUserId = invoice.getContract().getUser().getId();
        if (!ownerUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền xem hóa đơn này");
        }

        return toResponseWithDetails(invoice);
    }

    public String createVNPayPaymentUrl(Integer invoiceId, Integer userId, String clientIp, String bankCode) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));

        Integer ownerUserId = invoice.getContract().getUser().getId();
        if (!ownerUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền truy cập hóa đơn này");
        }

        if ("PAID".equals(invoice.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hóa đơn này đã được thanh toán");
        }

        String orderInfo = "Thanh toan hoa don phong " + getRoomDisplayName(invoice.getContract())
                + " thang " + invoice.getMonth();

        return buildVNPayUrl(invoice, orderInfo, clientIp, bankCode);
    }

    // ==================== ADMIN ====================

    public List<InvoiceResponse> getAllInvoices(String month, Integer contractId) {
        List<Invoice> invoices;
        if (month != null && contractId != null) {
            invoices = invoiceRepository.findByMonthAndContract_Id(month, contractId);
        } else if (month != null) {
            invoices = invoiceRepository.findByMonth(month);
        } else if (contractId != null) {
            invoices = invoiceRepository.findByContract_Id(contractId);
        } else {
            invoices = invoiceRepository.findAll();
        }

        List<Integer> invoiceIds = invoices.stream().map(Invoice::getId).collect(Collectors.toList());
        final Map<Integer, List<InvoiceDetail>> detailsByInvoiceId;
        if (invoiceIds.isEmpty()) {
            detailsByInvoiceId = new HashMap<>();
        } else {
            List<InvoiceDetail> details = invoiceDetailRepository.findByInvoice_IdIn(invoiceIds);
            detailsByInvoiceId = details.stream().collect(Collectors.groupingBy(d -> d.getInvoice().getId()));
        }

        return invoices.stream()
                .map(i -> toResponseWithDetails(i, detailsByInvoiceId.getOrDefault(i.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<InvoiceResponse> generateInvoices(GenerateInvoiceRequest request) {
        List<InvoiceResponse> results = new ArrayList<>();

        for (GenerateInvoiceRequest.RoomMeterReading reading : request.getReadings()) {
            Contract contract = contractRepository.findById(reading.getContractId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Không tìm thấy hợp đồng: " + reading.getContractId()));

            validateContractForInvoiceGeneration(contract, request.getMonth());

            if (invoiceRepository.findByContract_IdAndMonth(contract.getId(), request.getMonth()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Hóa đơn tháng " + request.getMonth()
                        + " của phòng " + getRoomDisplayName(contract) + " đã được phát hành");
            }

            Invoice invoice = new Invoice();
            invoice.setContract(contract);
            invoice.setMonth(request.getMonth());
            invoice.setDueDate(request.getDueDate());
            invoice.setStatus("UNPAID");
            invoice.setPaymentMethod("UNPAID");

            List<vn.ttcs.Room_Rental.domain.Service> services =
                    serviceRepository.findByRoom_Id(contract.getRoom().getId());
            List<InvoiceDetail> details = buildInvoiceDetails(
                    invoice,
                    contract,
                    request.getMonth(),
                    reading.getElectricOldIndex(),
                    reading.getElectricNewIndex(),
                    reading.getWaterOldIndex(),
                    reading.getWaterNewIndex(),
                    services
            );

            invoice.setTotalAmount(calculateTotalAmount(details));
            Invoice savedInvoice;
            try {
                savedInvoice = invoiceRepository.save(invoice);
            } catch (DataIntegrityViolationException ex) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Hóa đơn tháng " + request.getMonth()
                        + " của phòng " + getRoomDisplayName(contract) + " đã được phát hành");
            }

            for (InvoiceDetail d : details) {
                d.setInvoice(savedInvoice);
            }
            invoiceDetailRepository.saveAll(details);
            savedInvoice.setInvoiceDetails(details);

            results.add(toResponseWithDetails(savedInvoice));
        }

        return results;
    }

        @Transactional
        public InvoiceResponse updateInvoiceByContractAndMonth(Integer contractId, String month, UpdateInvoiceRequest request) {
        YearMonth billingMonth = parseBillingMonthOrThrow(month);

        Invoice invoice = invoiceRepository.findByContract_IdAndMonth(contractId, billingMonth.toString())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Không tìm thấy hóa đơn tháng " + billingMonth + " của hợp đồng " + contractId));

        if (isPaid(invoice)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Hóa đơn đã thanh toán, không thể chỉnh sửa");
        }

        Contract contract = invoice.getContract();
        validateContractForInvoiceGeneration(contract, billingMonth.toString());

        List<vn.ttcs.Room_Rental.domain.Service> services =
            serviceRepository.findByRoom_Id(contract.getRoom().getId());

        List<InvoiceDetail> details = buildInvoiceDetails(
            invoice,
            contract,
            billingMonth.toString(),
            request.getElectricOldIndex(),
            request.getElectricNewIndex(),
            request.getWaterOldIndex(),
            request.getWaterNewIndex(),
            services
        );

        invoice.setDueDate(request.getDueDate());
        invoice.setTotalAmount(calculateTotalAmount(details));
        invoice.setPaymentMethod("UNPAID");
        invoice.setStatus(request.getDueDate().isBefore(LocalDate.now()) ? "OVERDUE" : "UNPAID");
        invoice = invoiceRepository.save(invoice);

        invoiceDetailRepository.deleteByInvoice_Id(invoice.getId());
        invoiceDetailRepository.saveAll(details);
        invoice.setInvoiceDetails(details);

        return toResponseWithDetails(invoice, details);
        }

    @Transactional
    public InvoiceResponse confirmCashPayment(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));

        if ("PAID".equals(invoice.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hóa đơn đã được thanh toán trước đó");
        }

        invoice.setStatus("PAID");
        invoice.setPaymentMethod("CASH");
        return toResponse(invoiceRepository.save(invoice));
    }

    public InvoiceDashboardResponse getDashboard(String month) {
        // --- [FIX 4] Cập nhật trạng thái OVERDUE xuống DB trước khi tạo dashboard ---
        syncOverdueStatuses();

        List<Invoice> invoices = month != null
                ? invoiceRepository.findByMonthOrderByCreatedAtDesc(month)
                : invoiceRepository.findAll();

        double totalRevenue = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        double collectedAmount = invoices.stream()
                .filter(this::isPaid)
                .mapToDouble(Invoice::getTotalAmount).sum();
        double pendingAmount = totalRevenue - collectedAmount;
        double collectionRate = totalRevenue > 0 ? (collectedAmount / totalRevenue) * 100 : 0;

        long paid = invoices.stream().filter(this::isPaid).count();
        long overdue = invoices.stream().filter(this::isOverdue).count();
        long unpaid = invoices.stream().filter(i -> !isPaid(i) && !isOverdue(i)).count();

        // Đồng bộ theo bộ lọc month để tránh lệch dữ liệu debtRooms với dashboard summary
        List<Invoice> debtInvoices = month != null
            ? invoiceRepository.findAllDebtInvoicesByMonth(month)
            : invoiceRepository.findAllDebtInvoices();
        List<InvoiceDashboardResponse.DebtRoomInfo> debtRooms = debtInvoices.stream()
                .map(i -> {
                    InvoiceDashboardResponse.DebtRoomInfo debt = new InvoiceDashboardResponse.DebtRoomInfo();
                    debt.setRoomNumber(getRoomDisplayName(i.getContract()));
                    debt.setTenantName(i.getContract().getUser().getFullName());
                    debt.setAmount(i.getTotalAmount());
                    debt.setMonth(i.getMonth());
                    debt.setStatus(i.getStatus()); // lấy status thật từ DB (UNPAID / OVERDUE)
                    return debt;
                }).collect(Collectors.toList());

        InvoiceDashboardResponse dashboard = new InvoiceDashboardResponse();
        dashboard.setTotalRevenue(totalRevenue);
        dashboard.setCollectedAmount(collectedAmount);
        dashboard.setPendingAmount(pendingAmount);
        dashboard.setCollectionRate(Math.round(collectionRate * 100.0) / 100.0);
        dashboard.setTotalInvoices(invoices.size());
        dashboard.setPaidInvoices((int) paid);
        dashboard.setUnpaidInvoices((int) unpaid);
        dashboard.setOverdueInvoices((int) overdue);
        dashboard.setDebtRooms(debtRooms);

        return dashboard;
    }

    public String createVNPayPaymentUrlForAdmin(Integer invoiceId, String clientIp, String bankCode) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));

        if ("PAID".equals(invoice.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hóa đơn này đã được thanh toán");
        }

        String orderInfo = "Thanh toan hoa don phong " + getRoomDisplayName(invoice.getContract())
                + " thang " + invoice.getMonth();

        return buildVNPayUrl(invoice, orderInfo, clientIp, bankCode);
    }

    @Transactional
    public InvoiceResponse confirmVNPayReturn(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("Thiếu tham số trả về từ VNPAY");
        }

        String secureHash = params.get("vnp_SecureHash");
        if (secureHash == null || secureHash.isBlank()) {
            throw new IllegalArgumentException("Thiếu chữ ký bảo mật VNPAY");
        }

        if (!isValidVNPaySignature(params, secureHash)) {
            throw new IllegalArgumentException("Chữ ký VNPAY không hợp lệ");
        }

        String txnRef = params.get("vnp_TxnRef");
        if (txnRef == null || txnRef.isBlank()) {
            throw new IllegalArgumentException("Thiếu mã giao dịch hóa đơn");
        }

        Integer invoiceId;
        try {
            invoiceId = Integer.valueOf(txnRef);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Mã hóa đơn không hợp lệ: " + txnRef);
        }

        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));

        String responseCode = params.getOrDefault("vnp_ResponseCode", "");
        String transactionStatus = params.getOrDefault("vnp_TransactionStatus", "");
        boolean paymentSuccess = "00".equals(responseCode) && "00".equals(transactionStatus);

        if (!paymentSuccess) {
            throw new IllegalArgumentException("Thanh toán VNPAY không thành công (code: " + responseCode + ")");
        }

        long returnedAmount = extractVNPayAmountOrThrow(params);
        long expectedAmount = toVnpAmount(invoice.getTotalAmount());
        if (returnedAmount != expectedAmount) {
            throw new IllegalArgumentException("Số tiền thanh toán không khớp với hóa đơn");
        }

        if (!"PAID".equals(invoice.getStatus())) {
            invoice.setStatus("PAID");
            invoice.setPaymentMethod("TRANSFER");
            invoice = invoiceRepository.save(invoice);
        }

        return toResponse(invoice);
    }

    // ==================== PRIVATE HELPERS ====================

    private String buildVNPayUrl(Invoice invoice, String orderInfo, String clientIp, String bankCode) {
        try {
            Map<String, String> vnpParams = new TreeMap<>();

            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", vnpTmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(toVnpAmount(invoice.getTotalAmount())));
            vnpParams.put("vnp_CurrCode", "VND");
            String normalizedBankCode = normalizeBankCode(bankCode);
            if (normalizedBankCode != null) {
                vnpParams.put("vnp_BankCode", normalizedBankCode);
            }
            vnpParams.put("vnp_TxnRef", String.valueOf(invoice.getId()));
            vnpParams.put("vnp_OrderInfo", orderInfo);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
            vnpParams.put("vnp_IpAddr", clientIp != null ? clientIp : "127.0.0.1");

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

            String createDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_CreateDate", createDate);

            cld.add(Calendar.MINUTE, 15);
            String expireDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_ExpireDate", expireDate);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator<Map.Entry<String, String>> itr = vnpParams.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<String, String> entry = itr.next();
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                         .append('=')
                         .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    if (itr.hasNext()) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }

            String secureHash = VNPayConfig.hmacSHA512(vnpHashSecret, hashData.toString());
            query.append("&vnp_SecureHash=").append(secureHash);

            return vnpUrl + "?" + query;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể tạo link thanh toán VNPAY", e);
        }
    }

    private String normalizeBankCode(String bankCode) {
        if (bankCode == null || bankCode.isBlank()) {
            return null;
        }

        String normalized = bankCode.trim().toUpperCase();

        if ("ALL".equals(normalized)) {
            return null;
        }

        if ("QR".equals(normalized)) {
            return "VNPAYQR";
        }

        if ("BANK".equals(normalized)) {
            return "VNBANK";
        }

        if ("VNPAYQR".equals(normalized) || "VNBANK".equals(normalized)) {
            return normalized;
        }

        throw new IllegalArgumentException("bankCode không hợp lệ. Dùng: ALL, QR, BANK, VNPAYQR hoặc VNBANK");
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        InvoiceResponse res = new InvoiceResponse();
        res.setId(invoice.getId());
        res.setMonth(invoice.getMonth());
        res.setRoomNumber(getRoomDisplayName(invoice.getContract()));
        res.setTenantName(invoice.getContract().getUser().getFullName());
        res.setTotalAmount(invoice.getTotalAmount());
        res.setDueDate(invoice.getDueDate());
        res.setStatus(resolveDisplayStatus(invoice));
        res.setPaymentMethod(invoice.getPaymentMethod());
        res.setCreatedAt(invoice.getCreatedAt());
        return res;
    }

    private InvoiceResponse toResponseWithDetails(Invoice invoice) {
        List<InvoiceDetail> details = invoiceDetailRepository.findByInvoice_Id(invoice.getId());
        return toResponseWithDetails(invoice, details);
    }

    private InvoiceResponse toResponseWithDetails(Invoice invoice, List<InvoiceDetail> details) {
        InvoiceResponse res = toResponse(invoice);

        List<InvoiceDetailResponse> detailResponses = details.stream().map(d -> {
            InvoiceDetailResponse dr = new InvoiceDetailResponse();
            dr.setId(d.getId());
            // --- [FIX 1] Ư u tiên lấy description, fallback sang tên dịch vụ nếu có ---
            String label = d.getDescription() != null ? d.getDescription()
                    : (d.getService() != null ? d.getService().getName() : "Dịch vụ");
            dr.setDescription(label);
            dr.setServiceName(d.getService() != null ? d.getService().getName() : null);
            dr.setServiceUnit(d.getService() != null ? d.getService().getUnit() : "tháng");
            dr.setUnitPrice(getDetailUnitPrice(d));
            dr.setOldIndex(d.getOldIndex());
            dr.setNewIndex(d.getNewIndex());
            dr.setQuantity(Math.max(d.getNewIndex() - d.getOldIndex(), 0));
            dr.setSubtotal(d.getSubtotal());
            return dr;
        }).collect(Collectors.toList());

        res.setDetails(detailResponses);
        return res;
    }

    private String getRoomDisplayName(Contract contract) {
        return contract.getRoom().getName();
    }

    private boolean isServiceNameMatch(String serviceName, String configuredType) {
        return serviceName != null && configuredType != null && serviceName.equalsIgnoreCase(configuredType);
    }

    private boolean isServiceUnitMatch(String serviceUnit, String configuredUnit) {
        return serviceUnit != null && configuredUnit != null && serviceUnit.equalsIgnoreCase(configuredUnit);
    }

    private List<InvoiceDetail> buildInvoiceDetails(
            Invoice invoice,
            Contract contract,
            String month,
            Integer electricOldIndex,
            Integer electricNewIndex,
            Integer waterOldIndex,
            Integer waterNewIndex,
            List<vn.ttcs.Room_Rental.domain.Service> services) {
        List<InvoiceDetail> details = new ArrayList<>();

        Double roomPriceValue = contract.getRoom().getPrice();
        double roomPrice = roomPriceValue != null ? roomPriceValue : 0.0;
        InvoiceDetail rentDetail = new InvoiceDetail();
        rentDetail.setInvoice(invoice);
        rentDetail.setService(null);
        rentDetail.setDescription("Tiền phòng");
        rentDetail.setUnitPrice(roomPrice);
        rentDetail.setOldIndex(0);
        rentDetail.setNewIndex(1);
        rentDetail.setSubtotal(roomPrice);
        details.add(rentDetail);

        for (vn.ttcs.Room_Rental.domain.Service service : services) {
            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoice(invoice);
            detail.setService(service);
            detail.setDescription(service.getName());
            double unitPrice = getServicePrice(service);
            detail.setUnitPrice(unitPrice);

            double subtotal;
            MeterType meterType = detectMeterType(service);

            if (meterType == MeterType.ELECTRIC) {
                int oldIndex = safeMeterIndex(electricOldIndex);
                int newIndex = safeMeterIndex(electricNewIndex);
                int usage = calculateUsageOrThrow(oldIndex, newIndex,
                        "điện", getRoomDisplayName(contract), month);
                detail.setOldIndex(oldIndex);
                detail.setNewIndex(newIndex);
                subtotal = usage * unitPrice;
            } else if (meterType == MeterType.WATER) {
                int oldIndex = safeMeterIndex(waterOldIndex);
                int newIndex = safeMeterIndex(waterNewIndex);
                int usage = calculateUsageOrThrow(oldIndex, newIndex,
                        "nước", getRoomDisplayName(contract), month);
                detail.setOldIndex(oldIndex);
                detail.setNewIndex(newIndex);
                subtotal = usage * unitPrice;
            } else {
                detail.setOldIndex(0);
                detail.setNewIndex(1);
                subtotal = unitPrice;
            }

            detail.setSubtotal(subtotal);
            details.add(detail);
        }

        return details;
    }

    private double calculateTotalAmount(List<InvoiceDetail> details) {
        return details.stream()
                .map(InvoiceDetail::getSubtotal)
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private void validateContractForInvoiceGeneration(Contract contract, String month) {
        if (contract == null) {
            throw new IllegalArgumentException("Hợp đồng không hợp lệ");
        }

        if (!"ACTIVE".equalsIgnoreCase(contract.getStatus())) {
            throw new IllegalArgumentException("Chỉ được phát hành hóa đơn cho hợp đồng đang ACTIVE");
        }

        YearMonth billingMonth = parseBillingMonthOrThrow(month);
        LocalDate startDate = contract.getStartDate();
        LocalDate endDate = contract.getEndDate();

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Hợp đồng thiếu thời gian hiệu lực");
        }

        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        if (billingMonth.isBefore(startMonth) || billingMonth.isAfter(endMonth)) {
            throw new IllegalArgumentException("Tháng hóa đơn " + month + " nằm ngoài thời gian hiệu lực hợp đồng");
        }
    }

    private YearMonth parseBillingMonthOrThrow(String month) {
        if (month == null || month.isBlank()) {
            throw new IllegalArgumentException("Thiếu tháng hóa đơn");
        }

        try {
            return YearMonth.parse(month.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Định dạng tháng không hợp lệ. Yêu cầu: yyyy-MM");
        }
    }

    private MeterType detectMeterType(vn.ttcs.Room_Rental.domain.Service service) {
        if (service == null) {
            return MeterType.OTHER;
        }

        // --- [FIX 5] Ư u tiên dùng flag isMetered để quyết định loại dịch vụ có đồng hồ không ---
        // Sau đó phân biệt điện và nước bằng name/unit để tính chỉ số đúng
        boolean metered = Boolean.TRUE.equals(service.getIsMetered());

        if (metered || isServiceUnitMatch(service.getUnit(), electricUnit)
                || isServiceNameMatch(service.getName(), electricType)) {
            // Nhưng nếu metered mà tên/đơn vị khớp nước thì ưu tiên nước
            if (isServiceUnitMatch(service.getUnit(), waterUnit)
                    || isServiceNameMatch(service.getName(), waterType)) {
                return MeterType.WATER;
            }
            return MeterType.ELECTRIC;
        }

        if (isServiceUnitMatch(service.getUnit(), waterUnit)
                || isServiceNameMatch(service.getName(), waterType)) {
            return MeterType.WATER;
        }

        return MeterType.OTHER;
    }

    private int calculateUsageOrThrow(int oldIndex, int newIndex,
                                      String meterName, String roomName, String month) {
        if (newIndex < oldIndex) {
            throw new IllegalArgumentException("Chỉ số " + meterName + " không hợp lệ cho phòng "
                    + roomName + " tháng " + month + ": chỉ số mới phải lớn hơn hoặc bằng chỉ số cũ");
        }
        return newIndex - oldIndex;
    }

    private int safeMeterIndex(Integer value) {
        return value == null ? 0 : value;
    }

    private double getServicePrice(vn.ttcs.Room_Rental.domain.Service service) {
        Double defaultPrice = service.getDefaultPrice();
        return defaultPrice != null ? defaultPrice : 0.0;
    }

    private double getDetailUnitPrice(InvoiceDetail detail) {
        Double unitPrice = detail.getUnitPrice();
        if (unitPrice != null) {
            return unitPrice;
        }
        return getServicePrice(detail.getService());
    }

    private boolean isPaid(Invoice invoice) {
        return invoice != null && "PAID".equals(invoice.getStatus());
    }

    private boolean isOverdue(Invoice invoice) {
        return invoice != null
                && !isPaid(invoice)
                && invoice.getDueDate() != null
                && invoice.getDueDate().isBefore(LocalDate.now());
    }

    /**
     * [FIX 4] Cập nhật trạng thái OVERDUE xuống DB cho các hóa đơn đã quá hạn.
     * Được gọi mỗi khi Admin vào Dashboard để dữ liệu luôn đồng bộ.
     */
    @Transactional
    public void syncOverdueStatuses() {
        List<Invoice> unpaidInvoices = invoiceRepository.findByStatusNot("PAID").stream()
                .filter(i -> !isPaid(i))
                .collect(Collectors.toList());

        List<Invoice> toUpdate = new ArrayList<>();
        for (Invoice invoice : unpaidInvoices) {
            boolean shouldBeOverdue = isOverdue(invoice);
            if (shouldBeOverdue && !"OVERDUE".equals(invoice.getStatus())) {
                invoice.setStatus("OVERDUE");
                toUpdate.add(invoice);
            } else if (!shouldBeOverdue && "OVERDUE".equals(invoice.getStatus())) {
                // Trường hợp hiếm: hóa đơn được gia hạn (sửa dueDate) -> reset về UNPAID
                invoice.setStatus("UNPAID");
                toUpdate.add(invoice);
            }
        }

        if (!toUpdate.isEmpty()) {
            invoiceRepository.saveAll(toUpdate);
        }
    }

    private long extractVNPayAmountOrThrow(Map<String, String> params) {
        String amountRaw = params.get("vnp_Amount");
        if (amountRaw == null || amountRaw.isBlank()) {
            throw new IllegalArgumentException("Thiếu số tiền thanh toán từ VNPAY");
        }

        try {
            return Long.parseLong(amountRaw);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số tiền thanh toán từ VNPAY không hợp lệ");
        }
    }

    private long toVnpAmount(Double totalAmount) {
        if (totalAmount == null) {
            throw new IllegalArgumentException("Tổng tiền hóa đơn không hợp lệ");
        }

        BigDecimal amount = BigDecimal.valueOf(totalAmount)
                .multiply(BigDecimal.valueOf(VNPAY_AMOUNT_MULTIPLIER))
                .setScale(0, RoundingMode.HALF_UP);

        return amount.longValueExact();
    }

    private String resolveDisplayStatus(Invoice invoice) {
        if (isPaid(invoice)) {
            return "PAID";
        }
        if (isOverdue(invoice)) {
            return "OVERDUE";
        }
        return "UNPAID";
    }

    private boolean isValidVNPaySignature(Map<String, String> params, String receivedSecureHash) {
        try {
            Map<String, String> sorted = new TreeMap<>(params);
            sorted.remove("vnp_SecureHash");
            sorted.remove("vnp_SecureHashType");

            StringBuilder hashData = new StringBuilder();
            boolean first = true;

            for (Map.Entry<String, String> entry : sorted.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                if (fieldName == null || !fieldName.startsWith("vnp_")) {
                    continue;
                }

                if (fieldValue != null && !fieldValue.isEmpty()) {
                    if (!first) {
                        hashData.append('&');
                    }

                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    first = false;
                }
            }

            String expectedSecureHash = VNPayConfig.hmacSHA512(vnpHashSecret, hashData.toString());
            return expectedSecureHash != null && expectedSecureHash.equalsIgnoreCase(receivedSecureHash);
        } catch (Exception e) {
            return false;
        }
    }

    private enum MeterType {
        ELECTRIC,
        WATER,
        OTHER
    }
}