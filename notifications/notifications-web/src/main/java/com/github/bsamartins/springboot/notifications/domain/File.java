package com.github.bsamartins.springboot.notifications.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.bsamartins.springboot.notifications.jackson.deserializer.MediaTypeDeserializer;
import org.springframework.http.MediaType;

public class File {

    private String name;
    private byte[] content;

    @JsonDeserialize(using = MediaTypeDeserializer.class)
    private MediaType mediaType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
}
