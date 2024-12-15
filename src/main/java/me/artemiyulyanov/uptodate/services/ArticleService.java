package me.artemiyulyanov.uptodate.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import me.artemiyulyanov.uptodate.minio.MinioService;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.ArticleTopic;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.models.text.ArticleTextFragment;
import me.artemiyulyanov.uptodate.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private ArticleTopicService articleTopicService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    @Lazy
    public void init() {
        if (articleRepository.count() > 0) return;

        ArticleTopic topic1 = articleTopicService.findByName("Cultural Travel").get();
        ArticleTopic topic2 = articleTopicService.findByName("Luxury Travel").get();

        User author = userService.findByUsername("Artemiy").get();
        for (int i = 1; i <= 50; i++) {
            Article article = Article.builder()
                    .author(author)
                    .heading("The heading of the article #" + i)
                    .content("The content of the article #" + i)
                    .topics(Set.of(topic1, topic2))
                    .createdAt(LocalDateTime.now())
                    .build();
            articleRepository.save(article);
        }
    }

    public List<Article> findAllArticles(Sort sort) {
        return articleRepository.findAll(sort);
    }

    public Page<Article> findAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

    public List<Article> findByAuthor(User author) {
        return articleRepository.findByAuthor(author);
    }

    public void deleteById(Long id) {
        articleRepository.deleteById(id);
    }

    public void delete(Article article) {
        minioService.deleteArticleResources(article);
        articleRepository.delete(article);
    }

    public void save(Article article) {
        articleRepository.save(article);
    }

    public void loadResources(Article article, List<MultipartFile> resources) {
        if (resources != null) {
            minioService.deleteArticleResources(article);
            minioService.saveArticleResources(article, resources);
        }
    }

    @Deprecated
    public void update(Article article, List<MultipartFile> resources) {
        if (resources != null) {
            minioService.saveArticleResources(article, resources);
        }

        articleRepository.save(article);
    }

    public List<ArticleTextFragment> getArticleTextFragments(Article article) {
        try {
            return objectMapper.readValue(article.getContent(), objectMapper.getTypeFactory().constructCollectionType(List.class, ArticleTextFragment.class));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}