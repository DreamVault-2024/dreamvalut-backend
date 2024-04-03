package com.example.dreamvalutbackend.global.utils.resizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

public class ImageResizer {

    public static byte[] resize(MultipartFile image, int width, int height) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(image.getInputStream())
                .size(width, height)
                .outputFormat("jpeg")
                .outputQuality(1.0)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}
