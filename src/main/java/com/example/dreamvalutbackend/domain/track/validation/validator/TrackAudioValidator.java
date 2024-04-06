package com.example.dreamvalutbackend.domain.track.validation.validator;

import org.springframework.web.multipart.MultipartFile;

import com.example.dreamvalutbackend.domain.track.validation.annotation.ValidTrackAudio;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TrackAudioValidator implements ConstraintValidator<ValidTrackAudio, MultipartFile> {

    private static final long MAX_AUDIO_SIZE = 1024 * 1024 * 20; // 20MB

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        // 파일이 없거나 빈 파일인 경우
        if (value == null || value.isEmpty()) {
            return false;
        }

        // 오디오 파일의 크기가 20MB를 넘어가는 경우
        if (value.getSize() > MAX_AUDIO_SIZE) {
            return false;
        }

        // 오디오 파일의 확장자가 mp3, wav가 아닌 경우
        return value.getContentType() != null && value.getContentType().matches("audio/(mpeg|wave|wav)");
    }
}
