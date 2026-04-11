package vn.ttcs.Room_Rental.service;

import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ttcs.Room_Rental.domain.Contract;
import vn.ttcs.Room_Rental.domain.Ticket;
import vn.ttcs.Room_Rental.domain.dto.TicketCreateRequest;
import vn.ttcs.Room_Rental.domain.dto.TicketResponse;
import vn.ttcs.Room_Rental.domain.dto.UpdateTicketStatusRequest;
import vn.ttcs.Room_Rental.repository.ContractRepository;
import vn.ttcs.Room_Rental.repository.TicketRepository;
import org.springframework.data.domain.Page;


@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ContractRepository contractRepository;

    public TicketService(TicketRepository ticketRepository, ContractRepository contractRepository) {
        this.ticketRepository = ticketRepository;
        this.contractRepository = contractRepository;
    }

    public List<TicketResponse> getMyTickets(Integer contractId) {
        return ticketRepository.findByContractIdOrderByCreatedAtDesc(contractId)
                .stream()
                .map(TicketResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getMyTicketsByUserId(Integer userId) {
        return contractRepository.findByUserId(userId)
                .stream()
                .flatMap(contract -> ticketRepository.findByContractIdOrderByCreatedAtDesc(contract.getId()).stream())
                .sorted(Comparator.comparing(Ticket::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(TicketResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketResponse createTicket(Integer userId, TicketCreateRequest request) {
        Contract contract = contractRepository.findByUserId(userId)
                .stream()
                .filter(c -> "ACTIVE".equalsIgnoreCase(c.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Bạn chưa có hợp đồng đang hoạt động"));

        Ticket ticket = new Ticket();
        ticket.setContract(contract);
        ticket.setTitle(request.getTitle());
        ticket.setCategory(request.getCategory());
        ticket.setPriority(request.getPriority());
        ticket.setDescription(request.getDescription());
        // status mặc định = "PENDING" đã set trong entity

        return TicketResponse.fromEntity(ticketRepository.save(ticket));
    }

    public TicketResponse getTicketDetail(Integer userId, Integer ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket không tồn tại"));

        if (ticket.getContract() == null
                || ticket.getContract().getUser() == null
                || !ticket.getContract().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Ticket không tồn tại hoặc bạn không có quyền xem");
        }
        return TicketResponse.fromEntity(ticket);
    }

    public Page<TicketResponse> getAllTickets(
            String status, String priority, int page, int size) {

        Page<Ticket> ticketPage = ticketRepository.findAllWithFilters(
                status, priority, PageRequest.of(page, size));

        return ticketPage.map(TicketResponse::fromEntity);
    }

    @Transactional
    public TicketResponse updateTicketStatus(Integer ticketId, UpdateTicketStatusRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket không tồn tại"));

        ticket.setStatus(request.getStatus());
        return TicketResponse.fromEntity(ticketRepository.save(ticket));
    }
}
