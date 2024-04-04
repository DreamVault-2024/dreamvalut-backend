package com.example.dreamvalutbackend.global.utils.audio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioDataParser {

    public static int extractDurationInSeconds(MultipartFile audioFile) throws IOException {
        Path tempFile = null;
        try {
            // 임시 파일 생성
            tempFile = Files.createTempFile("temp", audioFile.getOriginalFilename());
            Files.copy(audioFile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // 임시 파일로부터 음악 길이 추출
            AudioFile f = AudioFileIO.read(tempFile.toFile());
            return f.getAudioHeader().getTrackLength();
        } catch (Exception e) {
            log.error("Failed to extract audio duration", e);
            return 0;
        } finally {
            // 임시 파일 삭제
            if (tempFile != null && Files.exists(tempFile)) {
                Files.delete(tempFile);
            }
        }
    }
}
