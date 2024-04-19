package com.example.dreamvalutbackend.global.utils.multipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.example.dreamvalutbackend.global.utils.resizer.ImageResizer;

public class ThumbnailImageMultipartFile implements MultipartFile {

    private final byte[] bytes;
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final boolean isEmpty;
    private final long size;

    public ThumbnailImageMultipartFile(MultipartFile file, String trackTitle) throws IOException {
        this.bytes = ImageResizer.resize(file, 300, 300);
        this.name = "thumbnailImage";
        this.originalFilename = "thumbnail/" + UUID.randomUUID().toString() + "-" + trackTitle + ".jpeg";
        this.contentType = file.getContentType();
        this.isEmpty = this.bytes == null || this.bytes.length == 0;
        this.size = this.bytes.length;
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
}
