package me.artemiyulyanov.uptodate.repositories;

import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByAuthor(User author);
}