package me.artemiyulyanov.uptodate.services;

import jakarta.annotation.PostConstruct;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.ArticleTopic;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    @Lazy
    private UserService userService;

    @PostConstruct
    @Lazy
    public void init() {
        if (articleRepository.count() > 0) return;

        User author = userService.findByUsername("Artemiy").get();
        Article article = Article.builder()
                .author(author)
                .content("The content of the article")
                .heading("The heading of the article")
                .createdAt(LocalDateTime.now())
                .build();

        articleRepository.save(article);
    }

    public List<Article> getAllArticles(Sort sort) {
        return articleRepository.findAll(sort);
    }

    public Page<Article> getAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    public Optional<Article> getArticle(Long id) {
        return articleRepository.findById(id);
    }
}