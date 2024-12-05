package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.entity.Users;
import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.MinioService;
import com.example.GoldenNest.service.AvatarService;
import com.example.GoldenNest.util.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;

@Service
public class AvatarServiceImpl implements AvatarService {

    private final MinioService minioService;
    private final UsersRepository usersRepository;
    private final Logger logger = LoggerFactory.getLogger(AvatarServiceImpl.class);

    public AvatarServiceImpl(MinioService minioService, UsersRepository usersRepository) {
        this.minioService = minioService;
        this.usersRepository = usersRepository;
    }

    @Override
    public void uploadAvatar(MultipartFile file) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getName())) {
            throw new NotFoundException("Người dùng chưa đăng nhập.");
        }

        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser.getId();

        // Define the bucket as "avatar"
        String bucketName = "avatar";

        try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
            String objectName = userId + "/" + file.getOriginalFilename();
            String contentType = minioService.getContentType(objectName);
            minioService.uploadFile(bucketName, objectName, inputStream, inputStream.available(), contentType);

            String avatarUrl = bucketName + "/" + objectName;
            currentUser.setAvatar(avatarUrl);
            usersRepository.save(currentUser);
            logger.info("Tệp {} đã được tải lên thành công cho người dùng {}", file.getOriginalFilename(), currentUsername);
        }
    }

    @Override
    public void deleteAvatar(String objectName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser.getId();

        // Define the bucket as "avatar"
        String bucketName = "avatar";

        try {
            String filepath = userId + "/" + objectName;
            minioService.deleteFile(bucketName, filepath);
            currentUser.setAvatar(null);
            usersRepository.save(currentUser);
            logger.info("Avatar deleted successfully for user: {}", currentUsername);
        } catch (Exception e) {
            logger.error("Error deleting avatar for user {}: {}", currentUsername, e.getMessage());
            throw new NotFoundException("Không tìm thấy tệp đính kèm từ MinIO: " + e.getMessage());
        }
    }
}
