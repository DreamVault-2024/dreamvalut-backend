package com.example.dreamvalutbackend.domain.track.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.example.dreamvalutbackend.domain.track.validation.validator.TrackAudioValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TrackAudioValidator.class)
public @interface ValidTrackAudio {

    String message() default "Invalid track audio";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
