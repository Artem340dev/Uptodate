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
import java.util.OptionalInt;
import java.util.Set;

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

    @PostConstruct
    @Lazy
    public void init() {
        if (articleRepository.count() > 0) return;

        ArticleTopic topic1 = articleTopicService.findByName("Cultural Travel").get();
        ArticleTopic topic2 = articleTopicService.findByName("Luxury Travel").get();

        User author = userService.findByUsername("Artemiy").get();
        Article article = Article.builder()
                .author(author)
                .heading("The heading of the article")
                .content("The content of the article")
                .topics(Set.of(topic1, topic2))
                .createdAt(LocalDateTime.now())
                .build();

        articleRepository.save(article);
    }

    public List<Article> findAllArticles(Sort sort) {
        return articleRepository.findAll(sort);
    }

    public Page<Article> findAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    public Optional<Article> findArticleById(Long id) {
        return articleRepository.findById(id);
    }

    public List<Article> findByAuthor(User author) {
        return articleRepository.findByAuthor(author);
    }

    public void deleteById(Long id) {
        articleRepository.deleteById(id);
    }

    public void delete(Article article) {
        articleRepository.delete(article);
    }

    public void save(Article article) {
        articleRepository.save(article);
    }
}