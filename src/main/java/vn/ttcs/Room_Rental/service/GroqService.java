package vn.ttcs.Room_Rental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.ttcs.Room_Rental.domain.dto.GroqChatRequest;
import vn.ttcs.Room_Rental.domain.dto.GroqChatResponse;

import java.util.*;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String groqApiUrl;

    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String model;

    private final RestTemplate restTemplate;

    public GroqService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String SYSTEM_PROMPT = """
        Bạn là trợ lý AI thông minh hỗ trợ hệ thống quản lý nhà trọ "Room Rental".
        Hệ thống có hai loại người dùng:
        - Admin (chủ nhà): quản lý phòng, hợp đồng, hóa đơn, duyệt yêu cầu.
        - Client (khách thuê): xem hóa đơn, đăng ký xe, gửi ticket sự cố, thanh toán qua VNPay.

        Các module chính:
        - Quản lý phòng & hợp đồng (Rooms & Contracts)
        - Hóa đơn & thanh toán (Invoices, tích hợp VNPay)
        - Dịch vụ tiện ích (điện, nước, internet, rác)
        - Phương tiện (Vehicles)
        - Hỗ trợ sự cố (Tickets)
        - Xác thực bảo mật (JWT + OTP qua Email)

        Hãy trả lời bằng tiếng Việt, ngắn gọn, thực tế và hữu ích.
        Khi tư vấn quy trình, hãy đưa ra các bước cụ thể.
        Nếu liên quan đến pháp lý thuê nhà tại Việt Nam, hãy đề cập các điều khoản phù hợp.
        """;

    public GroqChatResponse chat(GroqChatRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        // Build messages array: system + conversation history
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));

        for (GroqChatRequest.Message msg : request.getMessages()) {
            messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("max_tokens", 800);
        body.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            groqApiUrl, HttpMethod.POST, entity, Map.class
        );

        Map responseBody = response.getBody();
        List<Map> choices = (List<Map>) responseBody.get("choices");
        Map message = (Map) choices.get(0).get("message");
        String reply = (String) message.get("content");

        return new GroqChatResponse(reply);
    }
}