package me.artemiyulyanov.uptodate.controllers.api.articles;

import me.artemiyulyanov.uptodate.models.ArticleTopic;
import me.artemiyulyanov.uptodate.web.RequestService;
import me.artemiyulyanov.uptodate.web.ServerResponse;
import me.artemiyulyanov.uptodate.services.ArticleService;
import me.artemiyulyanov.uptodate.services.ArticleTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles/topics")
public class ArticleTopicController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleTopicService articleTopicService;

    @Autowired
    private RequestService requestService;

    @GetMapping(value = "/get", params = {})
    public ResponseEntity<?> getAllArticleTopics() {
        List<ArticleTopic> topics = articleTopicService.findAll();
        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", topics);
    }

    @GetMapping(value = "/get", params = {"id"})
    public ResponseEntity<?> getArticleTopicById(@RequestParam Long id) {
        Optional<ArticleTopic> articleTopic = articleTopicService.findById(id);

        if (articleTopic.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Article topic is undefined!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", articleTopic.get());
    }

    @GetMapping(value = "/get", params = {"parent"})
    public ResponseEntity<?> getArticleTopicsByParent(@RequestParam String parent, Model model) {
        List<ArticleTopic> articleTopics = articleTopicService.findByParent(parent);
        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", articleTopics);
    }
}