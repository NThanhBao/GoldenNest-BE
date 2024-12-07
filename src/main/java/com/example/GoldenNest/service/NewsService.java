package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.NewsDTO;
import com.example.GoldenNest.model.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsService {

    Page<News> getAllNews(Pageable pageable);

    News createNews(NewsDTO newsDTO);

}
