package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.dto.NewsDTO;
import com.example.GoldenNest.model.entity.News;
import com.example.GoldenNest.model.entity.NewsMedia;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.model.entity.Users;
import com.example.GoldenNest.repositories.NewsMediaRepository;
import com.example.GoldenNest.repositories.NewsRepository;
import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.NewsService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NewsServiceImpl implements NewsService {

    private final UsersRepository usersRepository;
    private final NewsRepository newsRepository;
    private final NewsMediaRepository newsMediaRepository;
    private final Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);


    public NewsServiceImpl(UsersRepository usersRepository, NewsRepository newsRepository, NewsMediaRepository newsMediaRepository) {
        this.usersRepository = usersRepository;
        this.newsRepository = newsRepository;
        this.newsMediaRepository = newsMediaRepository;
    }

    @Override
    public Page<News> getAllNews(Pageable pageable) {
        Page<News> news = newsRepository.findAll(pageable);
        if (news.isEmpty()) {
            logger.warn("No products found.");
        }
        return news;
    }


    @Override
    public News createNews(NewsDTO newsDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser != null ? currentUser.getId() : null;
        logger.info("Current user ID: {}", userId);

        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }

        News news = new News();
        news.setId(UUID.randomUUID().toString());
        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setSource(newsDTO.getSource());
        news.setUserId(currentUser);


        List<NewsMedia> medias = new ArrayList<>();
        for (String mediaId : newsDTO.getMediasId()) {
            Optional<NewsMedia> mediaOptional = newsMediaRepository.findById(mediaId);
            if (mediaOptional.isPresent()) {
                NewsMedia media = mediaOptional.get();

                media.setNews(news);
                medias.add(media);
            }
        }
        news.setMedias(medias);

        News createdNews = newsRepository.save(news);
        logger.info("New News created with ID: {}", createdNews.getId());

        return createdNews;
    }
}
