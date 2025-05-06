package com.sourcesense.smart_event_platform.security;

public enum Role {

    USER("user"), ORGANIZER("organizer"), ADMIN("admin");


    private final String name;

    Role(String name) {
        this.name = name;
    }

}
