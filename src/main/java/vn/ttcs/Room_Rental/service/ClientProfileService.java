package vn.ttcs.Room_Rental.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.ttcs.Room_Rental.domain.User;
import vn.ttcs.Room_Rental.domain.Vehicle;
import vn.ttcs.Room_Rental.domain.dto.BankAccountRequest;
import vn.ttcs.Room_Rental.domain.dto.ChangePasswordRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.ProfileResponse;
import vn.ttcs.Room_Rental.domain.dto.ProfileUpdateRequest;
import vn.ttcs.Room_Rental.domain.dto.VehicleRequest;
import vn.ttcs.Room_Rental.domain.dto.VehicleResponse;
import vn.ttcs.Room_Rental.repository.UserRepository;
import vn.ttcs.Room_Rental.repository.VehicleRepository;

@Service
public class ClientProfileService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String AVATAR_DIR = "uploads/avatars/";

    public ClientProfileService(UserRepository userRepository,
                                VehicleRepository vehicleRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ProfileResponse getProfile(Integer userId) {
        User user = findUser(userId);
        return toProfileResponse(user);
    }

    public ProfileResponse updateProfile(Integer userId, ProfileUpdateRequest req) {
        User user = findUser(userId);
        user.setFullName(req.getFullName());
        user.setCccd(req.getCccd());
        user.setDob(req.getDob());
        user.setGender(req.getGender());
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());
        userRepository.save(user);
        return toProfileResponse(user);
    }

    public String updateAvatar(Integer userId, MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null ||
            (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Chỉ chấp nhận file JPG hoặc PNG");
        }

        String extension = contentType.equals("image/jpeg") ? ".jpg" : ".png";
        String filename = "avatar_" + userId + "_" + UUID.randomUUID() + extension;

        try {
            Path dir = Paths.get(AVATAR_DIR);
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(filename),
                       StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu ảnh đại diện", e);
        }

        String avatarUrl = "/" + AVATAR_DIR + filename;
        User user = findUser(userId);
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }

    public void changePassword(Integer userId, ChangePasswordRequestDTO req) {
        User user = findUser(userId);

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác");
        }

        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    public void updateBankAccount(Integer userId, BankAccountRequest req) {
        User user = findUser(userId);
        user.setBankAccountNumber(req.getBankAccountNumber());
        user.setBankAccountName(req.getBankAccountName());
        user.setBankName(req.getBankName());
        userRepository.save(user);
    }

    public List<VehicleResponse> getVehicles(Integer userId) {
        return vehicleRepository.findByUserId(userId)
                .stream()
                .map(this::toVehicleResponse)
                .collect(Collectors.toList());
    }

    public VehicleResponse addVehicle(Integer userId, VehicleRequest req) {
        User user = findUser(userId);

        Vehicle vehicle = new Vehicle();
        vehicle.setUser(user);
        vehicle.setLicensePlate(req.getLicensePlate());
        vehicle.setVehicleType(req.getVehicleType());
        vehicle.setBrand(req.getBrand());
        vehicle.setModel(req.getModel());
        vehicle.setNote(req.getNote());

        return toVehicleResponse(vehicleRepository.save(vehicle));
    }

    public void deleteVehicle(Integer userId, Integer vehicleId) {
        Vehicle vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy phương tiện hoặc bạn không có quyền xóa"));
        vehicleRepository.delete(vehicle);
    }


    private User findUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
    }

    private ProfileResponse toProfileResponse(User user) {
        ProfileResponse res = new ProfileResponse();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setFullName(user.getFullName());
        res.setCccd(user.getCccd());
        res.setDob(user.getDob());
        res.setGender(user.getGender());
        res.setPhone(user.getPhone());
        res.setEmail(user.getEmail());
        res.setAddress(user.getAddress());
        res.setAvatarUrl(user.getAvatarUrl());
        res.setBankAccountNumber(user.getBankAccountNumber());
        res.setBankAccountName(user.getBankAccountName());
        res.setBankName(user.getBankName());
        res.setStatus(user.getStatus());
        res.setCreatedAt(user.getCreatedAt());
        return res;
    }

    private VehicleResponse toVehicleResponse(Vehicle v) {
        VehicleResponse res = new VehicleResponse();
        res.setId(v.getId());
        res.setLicensePlate(v.getLicensePlate());
        res.setVehicleType(v.getVehicleType());
        res.setBrand(v.getBrand());
        res.setModel(v.getModel());
        res.setNote(v.getNote());
        res.setStatus(v.getStatus());
        res.setCreatedAt(v.getCreatedAt());
        return res;
    }
}