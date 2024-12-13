package me.artemiyulyanov.uptodate.mail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailVerificationCode {
    private String email, code;
    private List<Credential> credentials;

    public Credential getCredential(String key) {
        return credentials.stream().filter(credential -> credential.getKey().equals(key)).findAny().get();
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Credential {
        @Getter
        @Setter
        private String key;

        @Getter
        @Setter
        private Object value;

        public <T> T getValue(Class<T> classOfValue) {
            return classOfValue.cast(value);
        }
    }
}