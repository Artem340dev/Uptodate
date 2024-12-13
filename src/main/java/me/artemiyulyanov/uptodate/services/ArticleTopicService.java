package me.artemiyulyanov.uptodate.services;

import jakarta.annotation.PostConstruct;
import me.artemiyulyanov.uptodate.models.ArticleTopic;
import me.artemiyulyanov.uptodate.repositories.ArticleTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleTopicService {
    public static final Map<String, List<String>> TOPICS = Map.of(
            "Technology", List.of(
                    "Artificial Intelligence",
                    "Software Development",
                    "Cybersecurity",
                    "Blockchain",
                    "Cloud Computing",
                    "Virtual Reality & Augmented Reality",
                    "Internet of Things (IoT)",
                    "Big Data",
                    "Wearable Tech"
            ),
            "Health & Wellness", List.of(
                    "Mental Health",
                    "Nutrition",
                    "Fitness & Exercise",
                    "Chronic Illness",
                    "Holistic Medicine",
                    "Medical Research",
                    "Sleep Health",
                    "Skin Care",
                    "Women's Health",
                    "Physical Therapy"
            ),
            "Business & Finance", List.of(
                    "Entrepreneurship",
                    "Investing & Stock Market",
                    "Personal Finance",
                    "Cryptocurrency",
                    "Corporate Strategies",
                    "Startups",
                    "E-commerce",
                    "Real Estate",
                    "Marketing & Advertising",
                    "Leadership & Management"
            ),
            "Science & Innovation", List.of(
                    "Space Exploration",
                    "Environmental Science",
                    "Physics",
                    "Chemistry",
                    "Biotechnology",
                    "Climate Change",
                    "Renewable Energy",
                    "Medical Breakthroughs",
                    "Quantum Computing",
                    "Robotics"
            ),
            "Education & Learning", List.of(
                    "Online Learning",
                    "STEM Education",
                    "Language Learning",
                    "Skill Development",
                    "Educational Technology",
                    "Classroom Strategies",
                    "Higher Education",
                    "Early Childhood Education",
                    "Adult Learning",
                    "Special Education"
            ),
            "Arts & Culture", List.of(
                    "Fine Arts (Painting, Sculpture)",
                    "Music",
                    "Literature",
                    "Film & Television",
                    "Performing Arts (Theater, Dance)",
                    "Photography",
                    "Cultural Heritage",
                    "Fashion",
                    "Architecture",
                    "Street Art"
            ),
            "Travel & Adventure", List.of(
                    "Budget Travel",
                    "Luxury Travel",
                    "Adventure Tourism",
                    "Cultural Travel",
                    "Sustainable Travel",
                    "Solo Travel",
                    "Travel Tips & Hacks",
                    "Travel Photography",
                    "Backpacking",
                    "City Guides"
            ),
            "Food & Drink", List.of(
                    "Recipes",
                    "Baking",
                    "Vegan & Vegetarian",
                    "World Cuisines",
                    "Wine & Beer",
                    "Street Food",
                    "Healthy Eating",
                    "Food Science",
                    "Culinary Techniques",
                    "Restaurant Reviews"
            ),
            "Lifestyle", List.of(
                    "Home & Interior Design",
                    "Fashion & Style",
                    "Self-Improvement",
                    "Relationships & Dating",
                    "Work-Life Balance",
                    "Sustainability in Lifestyle",
                    "Minimalism",
                    "Parenting",
                    "Personal Growth",
                    "Mindfulness"
            ),
            "Sports & Recreation", List.of(
                    "Football (Soccer)",
                    "Basketball",
                    "Tennis",
                    "Extreme Sports",
                    "Olympics",
                    "Martial Arts",
                    "Fitness & Bodybuilding",
                    "Water Sports",
                    "Esports",
                    "Running & Marathon"
            )
    );

    @Autowired
    private ArticleTopicRepository articleTopicRepository;

    @PostConstruct
    public void init() {
        if (articleTopicRepository.count() > 0) return;

        List<ArticleTopic> topics = TOPICS
                .entrySet()
                .stream()
                .map(entry -> entry.getValue()
                        .stream()
                        .map(topic -> ArticleTopic
                                .builder()
                                .parent(entry.getKey())
                                .name(topic)
                                .build())
                        .toList())
                .flatMap(List::stream)
                .toList();
        articleTopicRepository.saveAll(topics);
    }

    public Optional<ArticleTopic> findById(Long id) {
        return articleTopicRepository.findById(id);
    }

    public List<ArticleTopic> findByParent(String parent) {
        return articleTopicRepository.findByParent(parent);
    }

    public Optional<ArticleTopic> findByName(String name) {
        return articleTopicRepository.findByName(name);
    }
}