package me.artemiyulyanov.uptodate.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SafeEntity<T> {
    private T entity;
    private List<String> exceptedColumns;

    public T getSafeEntity() {
        try {
            T safeEntity = entity;

            for (String column : exceptedColumns) {
                Method method = entity.getClass().getMethod("set" + column.substring(0, 1).toUpperCase() + column.substring(1), Object.class);
                method.invoke(entity, (Object) null);            }

            return safeEntity;
        } catch (Exception e) {
            return null;
        }
    }
}