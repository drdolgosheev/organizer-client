package com.hse.organazer_client.entities.dto;

public class AuthDtoFromServer {
    String username;
    String token;

    public AuthDtoFromServer(String username, String token) {
        this.username = username;
        this.token = token;
    }
}
