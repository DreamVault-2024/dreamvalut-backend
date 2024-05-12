package com.example.dreamvalutbackend.domain.playlist.validation.validator;

import com.example.dreamvalutbackend.domain.playlist.validation.annotation.ValidPlaylistType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PlaylistTypeValidator implements ConstraintValidator<ValidPlaylistType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("curated") || value.equals("user_created");
    }
}
