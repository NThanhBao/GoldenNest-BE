package com.example.GoldenNest.controller;

import com.example.GoldenNest.service.NewsMediaService;
import com.example.GoldenNest.util.annotation.CheckLogin;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.example.GoldenNest.service.impl.AuthServiceImpl.logger;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsMediaService newsMediaService;

    public NewsController(NewsMediaService newsMediaService) {
        this.newsMediaService = newsMediaService;
    }

    @CheckLogin
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadMedia(@RequestParam("filePath") MultipartFile filePath) {
        if (filePath.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            String mediaId = newsMediaService.uploadMedia(filePath);
            return ResponseEntity.ok(mediaId);
        } catch (Exception e) {
            logger.error("Error uploading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

}
