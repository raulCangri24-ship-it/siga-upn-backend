package com.sga.backend.service;

import com.sga.backend.dto.LoginRequest;
import com.sga.backend.dto.LoginResponse;
import com.sga.backend.entity.Usuario;
import com.sga.backend.repository.UsuarioRepository;
import com.sga.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_INTENTOS = 3;

    public LoginResponse login(LoginRequest request) {

        // 1. Buscar usuario por correo
        Optional<Usuario> optUsuario = usuarioRepository.findByCorreo(request.getCorreo());

        if (optUsuario.isEmpty()) {
            throw new RuntimeException("Correo o contrasena incorrectos");
        }

        Usuario usuario = optUsuario.get();

        // 2. Verificar si la cuenta esta bloqueada
        if (usuario.getEstado() == Usuario.EstadoUsuario.BLOQUEADO) {
            throw new RuntimeException("Cuenta bloqueada por multiples intentos fallidos. Contacte al administrador");
        }

        // 3. Verificar si la cuenta esta inactiva
        if (usuario.getEstado() == Usuario.EstadoUsuario.INACTIVO) {
            throw new RuntimeException("Cuenta inactiva. Contacte al administrador");
        }

        // 4. Verificar contrasena
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {

            // Incrementar intentos fallidos
            usuarioRepository.incrementarIntentosFallidos(request.getCorreo());

            int intentosRestantes = MAX_INTENTOS - (usuario.getIntentosFallidos() + 1);

            if (intentosRestantes <= 0) {
                usuarioRepository.bloquearUsuario(request.getCorreo());
                throw new RuntimeException("Cuenta bloqueada por multiples intentos fallidos. Contacte al administrador");
            }

            throw new RuntimeException("Contrasena incorrecta. Intentos restantes: " + intentosRestantes);
        }

        // 5. Login exitoso - resetear intentos fallidos
        usuarioRepository.resetearIntentosFallidos(request.getCorreo());

        // 6. Generar token JWT
        String token = jwtUtil.generateToken(
            usuario.getCorreo(),
            usuario.getRol().getNombre().name()
        );

        // 7. Retornar respuesta
        LoginResponse respuesta = new LoginResponse(
            usuario.getIdUsuario(),
            token,
            usuario.getRol().getNombre().name(),
            usuario.getNombre(),
            usuario.getCorreo()
        );
        return respuesta;
    }
}