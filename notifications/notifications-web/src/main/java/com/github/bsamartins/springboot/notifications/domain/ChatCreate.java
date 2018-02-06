package com.github.bsamartins.springboot.notifications.domain;

public class ChatCreate {
        private String name;
        private com.github.bsamartins.springboot.notifications.domain.File picture;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public com.github.bsamartins.springboot.notifications.domain.File getPicture() {
            return picture;
        }

        public void setPicture(File picture) {
            this.picture = picture;
        }
    }
