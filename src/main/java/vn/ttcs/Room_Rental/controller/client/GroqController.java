package vn.ttcs.Room_Rental.controller.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ttcs.Room_Rental.service.GroqService;
import vn.ttcs.Room_Rental.domain.dto.GroqChatRequest;
import vn.ttcs.Room_Rental.domain.dto.GroqChatResponse;

@RestController
@RequestMapping("/api/ai")
public class GroqController {

    private final GroqService groqService;

    public GroqController(GroqService groqService) {
        this.groqService = groqService;
    }

    @PostMapping("/chat")
    public ResponseEntity<GroqChatResponse> chat(@RequestBody GroqChatRequest request) {
        GroqChatResponse response = groqService.chat(request);
        return ResponseEntity.ok(response);
    }
}