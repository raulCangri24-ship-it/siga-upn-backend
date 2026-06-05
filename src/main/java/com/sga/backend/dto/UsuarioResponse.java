package com.sga.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class UsuarioResponse {
    private String idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String estado;
    private String rol;
    private String fechaCreacion;
}