package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.dto.NewsDTO;
import com.example.GoldenNest.model.entity.News;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.service.NewsMediaService;
import com.example.GoldenNest.service.NewsService;
import com.example.GoldenNest.util.annotation.CheckLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.GoldenNest.service.impl.AuthServiceImpl.logger;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final NewsMediaService newsMediaService;

    public NewsController(NewsMediaService newsMediaService,
                          NewsService newsService) {
        this.newsMediaService = newsMediaService;
        this.newsService = newsService;
    }

    @GetMapping
    public Page<News> getAllNews(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsService.getAllNews(pageable);
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

    @CheckLogin
    @PostMapping
    public ResponseEntity<News> createProduct(@RequestBody NewsDTO newsDTO) {
        News newsNews = newsService.createNews(newsDTO);
        return new ResponseEntity<>(newsNews, HttpStatus.CREATED);
    }

}
