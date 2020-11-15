package com.hejz.springsecurityjwt.entity;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String jwt;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }
}
