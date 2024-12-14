package me.artemiyulyanov.uptodate.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExemptInRequest {
    ExemptionType type() default ExemptionType.FIELD;
    String[] targetedFields() default ""; // available only if ExemptionType.CLASS

    enum ExemptionType {
        FIELD, CLASS;
    }
}