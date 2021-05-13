package com.hse.organazer_client.entities.dto;

public class ChangePasswordDto {
    String username;
    String password;

    public ChangePasswordDto(){}

    public ChangePasswordDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
