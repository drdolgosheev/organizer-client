package com.hse.organazer_client.entities.dto;

public class AuthDtoToServer {
    String username;
    String password;

    public AuthDtoToServer(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
