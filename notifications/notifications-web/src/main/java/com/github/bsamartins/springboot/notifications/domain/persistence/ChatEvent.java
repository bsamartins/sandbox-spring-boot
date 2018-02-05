package com.github.bsamartins.springboot.notifications.domain.persistence;

import com.github.bsamartins.springboot.notifications.domain.File;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Document
public class ChatMembership {

    @Id
    private String id;

    @NotNull
    private String userId;

    @NotNull
    private String groupId;

    @NotNull
    private OffsetDateTime date;

    public ChatMembership() {
    }

    public ChatMembership(ChatMembership other) {
        this.id = other.id;
        this.name = other.name;
        this.pictureId = other.pictureId;
    }

    public ChatMembership(GroupCreate other) {
        this.name = other.name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getPictureUri() {
        return String.format("http://localhost/api/files/%s", this.pictureId);
    }

    public static class GroupCreate {
        private String name;
        private File picture;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public File getPicture() {
            return picture;
        }

        public void setPicture(File picture) {
            this.picture = picture;
        }
    }

}
