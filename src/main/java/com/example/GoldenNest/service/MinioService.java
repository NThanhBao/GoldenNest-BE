package com.example.GoldenNest.service;

import io.minio.*;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final Logger logger = LoggerFactory.getLogger(MinioService.class);

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * Kiểm tra và tạo bucket nếu chưa tồn tại.
     * @param bucketName Tên bucket cần kiểm tra và tạo.
     */
    public void ensureBucketExists(String bucketName) {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                logger.info("Bucket '{}' created successfully.", bucketName);
            } else {
                logger.info("Bucket '{}' already exists.", bucketName);
            }
        } catch (Exception e) {
            logger.error("Error ensuring bucket existence: {}", e.getMessage());
            throw new RuntimeException("Failed to ensure bucket existence", e);
        }
    }

    /**
     * Tải tệp lên MinIO cho một bucket cụ thể.
     *
     * @param bucketName  Tên bucket nơi sẽ lưu trữ tệp
     * @param objectName  Tên tệp trên MinIO
     * @param inputStream Luồng dữ liệu tệp
     * @param size        Kích thước tệp
     * @param contentType Loại MIME của tệp
     */
    public void uploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType) {
        try {
            ensureBucketExists(bucketName);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            logger.info("File '{}' uploaded successfully to bucket '{}'.", objectName, bucketName);
        } catch (Exception e) {
            logger.error("Error uploading file '{}': {}", objectName, e.getMessage());
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    /**
     * Xóa tệp từ MinIO trong một bucket cụ thể.
     *
     * @param bucketName Tên bucket chứa tệp
     * @param objectName Tên tệp trên MinIO
     */
    public void deleteFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            logger.info("File '{}' deleted successfully from bucket '{}'.", objectName, bucketName);
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            logger.error("Error deleting file '{}': {}", objectName, e.getMessage());
            throw new RuntimeException("Failed to delete file", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lấy MIME type dựa trên phần mở rộng tệp.
     *
     * @param fileName Tên tệp
     * @return MIME type
     */
    public String getContentType(String fileName) {
        String fileExtension = getFileExtension(fileName).toLowerCase();
        return switch (fileExtension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            default -> "application/octet-stream";
        };
    }

    /**
     * Lấy phần mở rộng của tệp.
     *
     * @param fileName Tên tệp
     * @return Phần mở rộng
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }
}
