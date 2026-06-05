package com.sga.backend.dto;

import lombok.Data;

@Data
public class UsuarioRequest {
    private String idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private String estado;
    private String idRol;
}