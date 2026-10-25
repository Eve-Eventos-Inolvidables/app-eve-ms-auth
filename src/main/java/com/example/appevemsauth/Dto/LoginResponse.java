package com.example.appevemsauth.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginResponse {

    @ToString.Exclude
    private String token;

    private String tokenType;

    private long expiresInMinutes;
}
