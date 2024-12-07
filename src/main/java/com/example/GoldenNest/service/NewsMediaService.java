package com.example.GoldenNest.service;

import org.springframework.web.multipart.MultipartFile;

public interface NewsMediaService {

    String uploadMedia(MultipartFile filePath) throws Exception;

}
