package com.example.dreamvalutbackend.global.utils.multipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class TrackAudioMultipartFile implements MultipartFile {

    private final byte[] bytes;
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final boolean isEmpty;
    private final long size;

    public TrackAudioMultipartFile(MultipartFile file, String title) throws IOException {
        this.bytes = file.getBytes();
        this.name = "trackAudio";
        this.originalFilename = "audio/" + UUID.randomUUID().toString() + "-" + title
                + getExtension(file.getContentType());
        this.contentType = file.getContentType();
        this.isEmpty = this.bytes == null || this.bytes.length == 0;
        this.size = file.getSize();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(bytes);
    }

    private String getExtension(String contentType) {
        if (contentType.equals("audio/mpeg")) {
            return ".mp3";
        } else if (contentType.equals("audio/wave") || contentType.equals("audio/wav")) {
            return ".wav";
        } else {
            throw new IllegalArgumentException("Invalid content type");
        }
    }
}
