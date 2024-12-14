package me.artemiyulyanov.uptodate.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.web.RequestService;
import me.artemiyulyanov.uptodate.web.ServerResponse;
import me.artemiyulyanov.uptodate.services.ArticleService;
import me.artemiyulyanov.uptodate.services.ArticleTopicService;
import me.artemiyulyanov.uptodate.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
@Tag(name = "Articles", description = "Endpoints for managing articles")
public class ArticleController extends AuthenticatedController {
    public static final int ARTICLE_PAGE_SIZE = 20;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleTopicService articleTopicService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @GetMapping(value = "/search", params = {"id"})
    @Operation(summary = "Find articles with a query", description = "Provide conditions to look up specific article")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The request has been proceeded successfully"),
        @ApiResponse(responseCode = "10", description = "User is undefined"),
        @ApiResponse(responseCode = "20", description = "Article is undefined")
    })
    public ResponseEntity<ServerResponse> getArticleById(@Parameter(description = "An ID of article to find a matching article", required = true) @RequestParam Long id, Model model) {
        Optional<Article> wrappedArticle = articleService.findArticleById(id);

        if (!wrappedArticle.isPresent()) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 20, "Article is undefined!");
        }

        return requestService.executeEntity(HttpStatus.OK, 200, wrappedArticle.get());
    }

    @GetMapping(value = "/search", params = {"authorId"})
    public ResponseEntity<ServerResponse> getArticleByAuthor(@Parameter(description = "An ID of author to find matching articles", required = true) @RequestParam Long authorId, Model model) {
        Optional<User> wrappedAuthor = userService.findById(authorId);

        if (!wrappedAuthor.isPresent()) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 10, "User is undefined!");
        }

        List<Article> articles = articleService.findByAuthor(wrappedAuthor.get());
        return requestService.executeEntity(HttpStatus.OK, 200, articles);
    }

    @GetMapping("/search")
    public ResponseEntity<ServerResponse> getAllArticles(@Parameter(description = "A page of paginated data") @RequestParam(defaultValue = "1", required = false) Integer page, Model model) {
        Page<Article> articles = articleService.findAllArticles(PageRequest.of(page - 1, ARTICLE_PAGE_SIZE));
        return requestService.executePaginatedEntity(HttpStatus.OK, 200, articles);
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new article", description = "Provide article data to create a new article", responses = {
            @ApiResponse(responseCode = "200", description = "The request has been proceeded successfully"),
            @ApiResponse(responseCode = "10", description = "The authorized user is undefined")
    })
    public ResponseEntity<ServerResponse> createArticle(@Schema(implementation = Article.class, description = "An article to be saved") @RequestBody Article article, Model model) {
        Optional<User> wrappedUser = getAuthorizedUser();

        if (!isUserAuthorized()) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 10, "The authorized user is undefined!");
        }

        article.setCreatedAt(LocalDateTime.now());
        article.setAuthor(wrappedUser.get());

        articleService.save(article);
        return requestService.executeMessage(HttpStatus.OK, 200, "The article has been created!");
    }

    @PatchMapping("/edit")
    @Operation(summary = "Edit an article", description = "Provide an ID of article and changes to apply", responses = {
            @ApiResponse(responseCode = "200", description = "The request has been proceeded successfully"),
            @ApiResponse(responseCode = "10", description = "The authorized user is undefined"),
            @ApiResponse(responseCode = "11", description = "The authorized user has no authority to apply changes"),
            @ApiResponse(responseCode = "20", description = "Article user is undefined")
    })
    public ResponseEntity<ServerResponse> editArticle(@Parameter(description = "An ID of article to apply changes", required = true) @RequestParam Long id, @Schema(implementation = Article.class, description = "The changed article data to put in") @RequestBody Map<String, Object> updates, Model model) {
        Optional<User> wrappedUser = getAuthorizedUser();
        Optional<Article> wrappedArticle = articleService.findArticleById(id);

        if (!isUserAuthorized()) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 10, "The authorized user is undefined!");
        }

        if (!wrappedArticle.isPresent()) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 20, "Article is undefined!");
        }

        Article newArticle = wrappedArticle.get();
        if (!newArticle.getAuthor().getId().equals(wrappedUser.get().getId())) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 11, "The authorized user has no authority to proceed the changes!");
        }

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Article.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, newArticle, value);
            }
        });

        articleService.save(newArticle);
        return requestService.executeMessage(HttpStatus.OK, 200, "The changes have been applied successfully!");
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete an article", description = "Provide an ID of article to delete", responses = {
            @ApiResponse(responseCode = "200", description = "The request has been proceeded successfully"),
            @ApiResponse(responseCode = "10", description = "The authorized user is undefined"),
            @ApiResponse(responseCode = "11", description = "The authorized user has no authority to apply changes"),
            @ApiResponse(responseCode = "20", description = "Article user is undefined")
    })
    public ResponseEntity<ServerResponse> deleteArticle(@Parameter(description = "An ID of article to delete", required = true) @RequestParam Long id, Model model) {
        Optional<User> wrappedUser = getAuthorizedUser();
        Optional<Article> wrappedArticle = articleService.findArticleById(id);

        if (!isUserAuthorized()) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 10, "The authorized user is undefined!");
        }

        if (!wrappedArticle.isPresent()) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 20, "Article is undefined!");
        }

        Article article = wrappedArticle.get();
        if (!article.getAuthor().getId().equals(wrappedUser.get().getId())) {
            return requestService.executeError(HttpStatus.BAD_REQUEST, 11, "The authorized user has no authority to proceed the removal!");
        }

        articleService.delete(article);
        return requestService.executeMessage(HttpStatus.OK, 200, "The removal has been processed successfully!");
    }
}