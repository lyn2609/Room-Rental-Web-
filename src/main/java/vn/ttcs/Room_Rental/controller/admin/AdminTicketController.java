package vn.ttcs.Room_Rental.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.ttcs.Room_Rental.domain.dto.TicketResponse;
import vn.ttcs.Room_Rental.domain.dto.UpdateTicketStatusRequest;
import vn.ttcs.Room_Rental.service.TicketService;

@RestController
@RequestMapping("/api/admin/tickets")
public class AdminTicketController {
    private final TicketService ticketService;
    public AdminTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }
    @GetMapping
    public ResponseEntity<Page<TicketResponse>> getAllTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ticketService.getAllTickets(status, priority, page, size));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {

        return ResponseEntity.ok(ticketService.updateTicketStatus(id, request));
    }
}
