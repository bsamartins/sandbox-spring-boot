package com.github.bsamartins.springboot.notifications.domain.persistence;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public class ChatEvent {

    public enum Type {
        USER_LEFT,
        USER_JOINED
    }

    @Id
    private String id;

    @NotNull
    private String userId;

    @NotNull
    private OffsetDateTime timestamp;

    @NotNull
    private Type type;

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
