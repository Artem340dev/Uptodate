package me.artemiyulyanov.uptodate.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.Entity;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.expression.Fields;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class RequestService {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        return objectMapper;
    }

    public String mapToJson(Map<String, Object> params) {
        try {
            return objectMapper().writeValueAsString(params);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<ServerResponse> executeError(HttpStatus status, int code, String description) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerResponse
                        .builder()
                        .code(code)
                        .response(Map.of("error", description))
                        .build()
                );
    }

    public ResponseEntity<ServerResponse> executeCustomTemplate(HttpStatus status, int code, Map<String, Object> template) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerResponse
                        .builder()
                        .code(code)
                        .response(template)
                        .build()
                );
    }

    public ResponseEntity<ServerResponse> executeEntity(HttpStatus status, int code, Object entity) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerResponse
                        .builder()
                        .code(code)
                        .response(entity)
                        .build()
                );
    }

    public <T> ResponseEntity<ServerResponse> executePaginatedEntity(HttpStatus status, int code, Page<T> entity) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(PaginatedResponse
                        .builder()
                        .totalPages(entity.getTotalPages())
                        .page(entity.getNumber())
                        .size(entity.getSize())
                        .totalElements(entity.getTotalElements())
                        .last(entity.isLast())
                        .code(code)
                        .response(entity.getContent())
                        .build()
                );
    }

    public ResponseEntity<ServerResponse> executeMessage(HttpStatus status, int code, String description) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerResponse
                        .builder()
                        .code(code)
                        .response(Map.of("message", description))
                        .build()
                );
    }

//    public <E> E applyExemptionRequirements(E entity) {
//        try {
//            Field[] fields = entity.getClass().getDeclaredFields();
//
//            for (Field field : fields) {
//                field.setAccessible(true);
//
//                if (field.isAnnotationPresent(ExemptInRequest.class)) {
//                    ExemptInRequest annotation = field.getAnnotation(ExemptInRequest.class);
//                    if (annotation.type() == ExemptInRequest.ExemptionType.FIELD) {
//                        String fieldName = field.getName();
//                        Method exemptMethod = entity.getClass().getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), field.getType());
//                        exemptMethod.invoke(entity, (Object) null);
//                    }
//
//                    if (annotation.type() == ExemptInRequest.ExemptionType.CLASS) {
//                        Object entityToExemptValues = field.get(entity);
//
//                        for (String targetedFieldName : annotation.targetedFields()) {
//                            Field targetedField = entityToExemptValues.getClass().getDeclaredField(targetedFieldName);
//                            Method exemptMethod = entityToExemptValues.getClass().getMethod("set" + targetedFieldName.substring(0, 1).toUpperCase() + targetedFieldName.substring(1), targetedField.getType());
//                            exemptMethod.invoke(entityToExemptValues, (Object) null);
//                        }
//
//                        Method setExemptedTarget = entity.getClass().getMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), entityToExemptValues.getClass());
//                        setExemptedTarget.invoke(entity, entityToExemptValues);
//                    }
//                }
//
//                field.setAccessible(false);
//            }
//
//            return entity;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return entity;
//        }
//    }
}