package vn.ttcs.Room_Rental.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class UploadService {
    // Tự động trỏ vào folder "uploads" có sẵn trong cấu hình WebMvcConfig của ông
    private final Path root = Paths.get("uploads");

    public String saveFile(MultipartFile file) {
        try {
            // 1. Kiểm tra nếu thư mục "uploads" chưa có thì tự động tạo
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            // 2. Đổi tên file bằng cách thêm timestamp để tránh trùng lặp đè file cũ
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;

            // 3. Xác định đường dẫn đích của file trong folder uploads
            Path destination = this.root.resolve(uniqueFilename);

            // 4. Copy file vật lý từ request vào thư mục uploads
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // 5. Trả về Link URL dạng /uploads/ để khớp với WebMvcConfig công khai ảnh
            return "http://localhost:8080/uploads/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file ảnh! Lỗi: " + e.getMessage());
        }
    }
}