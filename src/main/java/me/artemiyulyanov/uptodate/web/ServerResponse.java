package me.artemiyulyanov.uptodate.web;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Component
public class ServerResponse<T> {
    private T response;
    private int code;
}