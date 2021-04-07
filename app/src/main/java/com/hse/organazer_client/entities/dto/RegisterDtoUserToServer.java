package com.hse.organazer_client.entities.dto;

public class RegisterDtoUserToServer {
    String username;
    String firstName;
    String lastName;
    String email;
    String password;

    public RegisterDtoUserToServer(String username, String firstName, String lastName, String email, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
