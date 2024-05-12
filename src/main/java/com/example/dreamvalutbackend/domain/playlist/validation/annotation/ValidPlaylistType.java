package com.example.dreamvalutbackend.domain.playlist.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.example.dreamvalutbackend.domain.playlist.validation.validator.PlaylistTypeValidator;

@Documented
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PlaylistTypeValidator.class)
public @interface ValidPlaylistType {

    String message() default "Invalid playlist type. Allowed values are 'user_created' or 'curated'.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
