package com.turnosmedicos.turnosmedicos.models;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private User user;
}
