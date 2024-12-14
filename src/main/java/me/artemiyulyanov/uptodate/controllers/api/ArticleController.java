package me.artemiyulyanov.uptodate.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.ArticleTopic;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.requests.RequestService;
import me.artemiyulyanov.uptodate.requests.SafeEntity;
import me.artemiyulyanov.uptodate.requests.ServerResponse;
import me.artemiyulyanov.uptodate.services.ArticleService;
import me.artemiyulyanov.uptodate.services.ArticleTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
@Tag(name = "Articles", description = "Endpoints for managing articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleTopicService articleTopicService;

    @Autowired
    private RequestService requestService;

    @GetMapping("/get")
    @Operation(summary = "Get an article by ID", description = "Provide an ID to look up specific article")
    public ResponseEntity<ServerResponse> getArticleById(@Parameter(description = "ID пользователя", required = true) @RequestParam Long id, Model model) {
        Optional<Article> articleOptional = articleService.getArticle(id);

        if (!articleOptional.isPresent()) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 30, "User is not found!");
        }

        return requestService.executeEntity(HttpStatus.OK, 200, articleOptional.get());
    }

    @GetMapping("/getAll")
    public ResponseEntity<ServerResponse> getAllArticles(Model model) {
        Page<Article> articles = articleService.getAllArticles(Pageable.ofSize(20));
        return requestService.executePaginatedEntity(HttpStatus.OK, 200, articles);
    }

//    @DeleteMapping("/delete")
//    public ResponseEntity<ServerResponse> deleteArticle(Model model) {
//
//    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("test");
    }
}