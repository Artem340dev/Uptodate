package me.artemiyulyanov.uptodate.repositories;

import me.artemiyulyanov.uptodate.models.ArticleTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleTopicRepository extends JpaRepository<ArticleTopic, Long> {
    List<ArticleTopic> findByParent(String parent);
    Optional<ArticleTopic> findByName(String name);
}