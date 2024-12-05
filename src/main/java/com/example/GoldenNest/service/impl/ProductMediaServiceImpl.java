package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.model.entity.ProductMedia;
import com.example.GoldenNest.model.entity.Users;
import com.example.GoldenNest.repositories.ProductMediaRepository;
import com.example.GoldenNest.repositories.ProductRepository;
import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.MinioService;
import com.example.GoldenNest.service.ProductMediaService;
import com.example.GoldenNest.util.exception.NotFoundException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
public class ProductMediaServiceImpl implements ProductMediaService {

    private final MinioClient minioClient;
    private final MinioService minioService;
    private final UsersRepository usersRepository;
    private final ProductMediaRepository productMediaRepository;
    private final ProductRepository productRepository;
    private final Logger logger = LoggerFactory.getLogger(ProductMediaServiceImpl.class);
    String bucketName = "products";

    public ProductMediaServiceImpl(MinioClient minioClient, MinioService minioService, UsersRepository usersRepository,
                                   ProductMediaRepository productMediaRepository, ProductRepository productRepository) {
        this.minioClient = minioClient;
        this.minioService = minioService;
        this.usersRepository = usersRepository;
        this.productMediaRepository = productMediaRepository;
        this.productRepository = productRepository;
    }

    @Override
    public String uploadMedia(MultipartFile filePath) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser.getId();

        try {
            // Kiểm tra bucketName
            checkBucketName(minioClient);

            try (InputStream inputStream = new BufferedInputStream(filePath.getInputStream())) {
                String originalFileName = filePath.getOriginalFilename();
                assert originalFileName != null;
                String fileExtension = getFileExtension(originalFileName).toLowerCase();
                String objectType = (fileExtension.equals("mp4") || fileExtension.equals("avi") || fileExtension.equals("mov") || fileExtension.equals("wmv")) ? "videos" : "images";
                String objectName = userId + "/" + objectType + "/" + originalFileName;

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, inputStream.available(), -1)
                                .contentType(getContentType(originalFileName))
                                .build()
                );

                ProductMedia medias = new ProductMedia();
                String mediaId = UUID.randomUUID().toString();
                medias.setId(mediaId);
                medias.setBase_name(filePath.getOriginalFilename());
                String setBaseName = bucketName + "/" + objectName;
                medias.setPublic_url(setBaseName);

                productMediaRepository.save(medias);

                logger.info("File {} uploaded successfully to MinIO for user {}", originalFileName, currentUsername);

                // Trả về ID của media
                logger.info("mediaId{}", mediaId);
                return mediaId;
            }
        } catch (Exception e) {
            logger.error("Error uploading file to MinIO", e);
            throw new Exception("Error uploading file to MinIO", e);
        }
    }

    private String getContentType(String fileName) {
        String fileExtension = getFileExtension(fileName).toLowerCase();
        return switch (fileExtension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "mp4" -> "video/mp4";
            case "avi" -> "video/x-msvideo";
            case "mov" -> "video/quicktime";
            case "wmv" -> "video/x-ms-wmv";
            default -> "application/octet-stream";
        };
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }

    public void checkBucketName(MinioClient minioClient) throws Exception {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                .bucket(bucketName)
                .build();

        if (minioClient.bucketExists(bucketExistsArgs)) {
            logger.info("{} exists.", bucketName);
        } else {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build();

            minioClient.makeBucket(makeBucketArgs);
            logger.info("{} created.", bucketName);
        }
    }
}
