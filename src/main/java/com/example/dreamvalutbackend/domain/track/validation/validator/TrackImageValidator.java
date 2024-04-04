package com.example.dreamvalutbackend.domain.track.validation.validator;

import org.springframework.web.multipart.MultipartFile;

import com.example.dreamvalutbackend.domain.track.validation.annotation.ValidTrackImage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TrackImageValidator implements ConstraintValidator<ValidTrackImage, MultipartFile> {

    private static final long MAX_IMAGE_SIZE = 1024 * 1024 * 1; // 1MB

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        // 파일이 없거나 빈 파일인 경우
        if (value == null || value.isEmpty()) {
            return false;
        }

        // 이미지 파일의 크기가 1MB를 넘어가는 경우
        if (value.getSize() > MAX_IMAGE_SIZE) {
            return false;
        }

        // 이미지 파일의 확장자가 jpeg, png, webp가 아닌 경우
        return value.getContentType() != null && value.getContentType().matches("image/(jpeg|png|webp)");
    }
}
