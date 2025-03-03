package me.artemiyulyanov.uptodate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import me.artemiyulyanov.uptodate.models.converters.TranslativeStringConvertor;
import me.artemiyulyanov.uptodate.models.text.TranslativeString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "topics")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private int count;

    @Convert(converter = TranslativeStringConvertor.class)
    private TranslativeString parent;

    @Convert(converter = TranslativeStringConvertor.class)
    @Column(unique = true)
    private TranslativeString name;

    @JsonIgnore
    @ManyToMany(mappedBy = "topics")
    private Set<Article> articles = new HashSet<>();

    public int getCount() {
        return articles.size();
    }

    public static ArticleTopic of(String englishParent, String russianParent, String englishName, String russianName) {
        return ArticleTopic.builder()
                .parent(TranslativeString.builder()
                        .english(englishParent)
                        .russian(russianParent)
                        .build())
                .name(TranslativeString.builder()
                        .english(englishName)
                        .russian(russianName)
                        .build())
                .build();
    }
}