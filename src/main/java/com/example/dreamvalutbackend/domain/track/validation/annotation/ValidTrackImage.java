package com.example.dreamvalutbackend.domain.track.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.example.dreamvalutbackend.domain.track.validation.validator.TrackImageValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TrackImageValidator.class)
public @interface ValidTrackImage {

    String message() default "Invalid track image";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
