package com.thy.scrum_planning_be.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    // İstersen log seviyesini veya aktifliğini buradan yönetebilirsin ama şimdilik sade tutalım.
}
