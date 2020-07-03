package com.jeferson.os.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenModel {
    private String token;
    private UserModel user;
}
