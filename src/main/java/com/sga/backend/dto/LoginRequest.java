package com.sga.backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String correo;
    private String contrasena;
}
