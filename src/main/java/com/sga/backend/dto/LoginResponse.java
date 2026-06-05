package com.sga.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String idUsuario;
    private String token;
    private String rol;
    private String nombre;
    private String correo;
}