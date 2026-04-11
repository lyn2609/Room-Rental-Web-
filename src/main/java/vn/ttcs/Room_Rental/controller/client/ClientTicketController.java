package vn.ttcs.Room_Rental.controller.client;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.ttcs.Room_Rental.domain.dto.TicketCreateRequest;
import vn.ttcs.Room_Rental.domain.dto.TicketResponse;
import vn.ttcs.Room_Rental.repository.UserRepository;
import vn.ttcs.Room_Rental.service.TicketService;

@RestController
@RequestMapping("/api/client/tickets")
public class ClientTicketController {
    private final TicketService ticketService;
    private final UserRepository userRepository;

    public ClientTicketController(TicketService ticketService, UserRepository userRepository) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }
    
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getMyTickets(
            Authentication authentication) {
        Integer userId = resolveCurrentUserId(authentication);
        return ResponseEntity.ok(ticketService.getMyTicketsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            Authentication authentication,
            @Valid @RequestBody TicketCreateRequest request) {
        Integer userId = resolveCurrentUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.createTicket(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketDetail(
            Authentication authentication,
            @PathVariable Integer id) {
        Integer userId = resolveCurrentUserId(authentication);
        return ResponseEntity.ok(ticketService.getTicketDetail(userId, id));
    }

    private Integer resolveCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Bạn chưa đăng nhập");
        }

        String phone = authentication.getName();
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phiên đăng nhập không hợp lệ");
        }

        return userRepository.findByPhone(phone)
                .map(user -> user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng đăng nhập"));
    }
    
}
