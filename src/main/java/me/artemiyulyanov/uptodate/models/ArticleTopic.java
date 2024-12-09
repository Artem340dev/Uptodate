package me.artemiyulyanov.uptodate.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "topics")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String parent;

    @Getter
    @Setter
    @Column(unique = true)
    private String name;

    @Getter
    @Setter
    @ManyToMany(mappedBy = "topics")
    private Set<Article> articles = new HashSet<>();
}