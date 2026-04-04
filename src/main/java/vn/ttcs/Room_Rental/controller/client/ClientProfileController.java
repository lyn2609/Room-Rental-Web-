package vn.ttcs.Room_Rental.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.ttcs.Room_Rental.domain.User;
import vn.ttcs.Room_Rental.domain.dto.BankAccountRequest;
import vn.ttcs.Room_Rental.domain.dto.ChangePasswordRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.ProfileResponse;
import vn.ttcs.Room_Rental.domain.dto.ProfileUpdateRequest;
import vn.ttcs.Room_Rental.domain.dto.VehicleRequest;
import vn.ttcs.Room_Rental.domain.dto.VehicleResponse;
import vn.ttcs.Room_Rental.repository.UserRepository;
import vn.ttcs.Room_Rental.service.ClientProfileService;

@RestController
@RequestMapping("/api/client/profile")
public class ClientProfileController {

    private final ClientProfileService profileService;
    private final UserRepository userRepository;

    public ClientProfileController(ClientProfileService profileService,
                                   UserRepository userRepository) {
        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    private Integer currentUserId(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByPhone(username)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        return user.getId();
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getProfile(currentUserId(authentication)));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            Authentication authentication,
            @RequestBody ProfileUpdateRequest req) {
        return ResponseEntity.ok(profileService.updateProfile(currentUserId(authentication), req));
    }

    @PutMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> updateAvatar(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        String url = profileService.updateAvatar(currentUserId(authentication), file);
        return ResponseEntity.ok(Map.of("avatarUrl", url));
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequestDTO req) {
        profileService.changePassword(currentUserId(authentication), req);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
    }

    @PutMapping("/bank-account")
    public ResponseEntity<Map<String, String>> updateBankAccount(
            Authentication authentication,
            @RequestBody BankAccountRequest req) {
        profileService.updateBankAccount(currentUserId(authentication), req);
        return ResponseEntity.ok(Map.of("message", "Cập nhật tài khoản ngân hàng thành công"));
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleResponse>> getVehicles(Authentication authentication) {
        return ResponseEntity.ok(profileService.getVehicles(currentUserId(authentication)));
    }

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleResponse> addVehicle(
            Authentication authentication,
            @RequestBody VehicleRequest req) {
        VehicleResponse res = profileService.addVehicle(currentUserId(authentication), req);
        return ResponseEntity.status(201).body(res);
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Map<String, String>> deleteVehicle(
            Authentication authentication,
            @PathVariable Integer id) {
        profileService.deleteVehicle(currentUserId(authentication), id);
        return ResponseEntity.ok(Map.of("message", "Xóa phương tiện thành công"));
    }
}