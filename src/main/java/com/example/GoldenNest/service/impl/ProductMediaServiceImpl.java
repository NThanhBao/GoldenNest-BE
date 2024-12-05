package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.entity.ProductMedia;
import com.example.GoldenNest.model.entity.Users;
import com.example.GoldenNest.repositories.ProductMediaRepository;
import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.MinioService;
import com.example.GoldenNest.service.ProductMediaService;
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

    private final MinioService minioService;
    private final UsersRepository usersRepository;
    private final ProductMediaRepository productMediaRepository;
    private final Logger logger = LoggerFactory.getLogger(ProductMediaServiceImpl.class);


    public ProductMediaServiceImpl(MinioService minioService, UsersRepository usersRepository,
                                   ProductMediaRepository productMediaRepository) {
        this.minioService = minioService;
        this.usersRepository = usersRepository;
        this.productMediaRepository = productMediaRepository;
    }

    @Override
    public String uploadMedia(MultipartFile filePath) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser.getId();

        String bucketName = "products";

        try {
            minioService.ensureBucketExists(bucketName);

            try (InputStream inputStream = new BufferedInputStream(filePath.getInputStream())) {
                String originalFileName = filePath.getOriginalFilename();
                assert originalFileName != null;
                String fileExtension = getFileExtension(originalFileName).toLowerCase();
                String objectType = (fileExtension.equals("mp4") || fileExtension.equals("avi") || fileExtension.equals("mov") || fileExtension.equals("wmv")) ? "videos" : "images";
                String objectName = userId + "/" + objectType + "/" + originalFileName;

                String contentType = minioService.getContentType(originalFileName);
                minioService.uploadFile(bucketName, objectName, inputStream, inputStream.available(), contentType);

                ProductMedia medias = new ProductMedia();
                String mediaId = UUID.randomUUID().toString();
                medias.setId(mediaId);
                medias.setBase_name(filePath.getOriginalFilename());
                String setBaseName = bucketName + "/" + objectName;
                medias.setPublic_url(setBaseName);

                productMediaRepository.save(medias);

                logger.info("File {} uploaded successfully to MinIO for user {}", originalFileName, currentUsername);

                return mediaId;
            }
        } catch (Exception e) {
            logger.error("Error uploading file to MinIO", e);
            throw new Exception("Error uploading file to MinIO", e);
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }
}
