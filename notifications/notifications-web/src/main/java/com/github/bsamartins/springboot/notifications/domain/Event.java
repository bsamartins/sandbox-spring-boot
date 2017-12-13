package com.github.bsamartins.springboot.notifications.domain;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {

    private String content;
    private Date timestamp;

    public Event() {
    }

    public Event(String content, Date timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}