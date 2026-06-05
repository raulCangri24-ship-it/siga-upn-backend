package com.sga.backend.service;

import com.sga.backend.dto.UsuarioRequest;
import com.sga.backend.dto.UsuarioResponse;
import com.sga.backend.entity.Rol;
import com.sga.backend.entity.Usuario;
import com.sga.backend.repository.RolRepository;
import com.sga.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Listar todos
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // Buscar por ID
    public UsuarioResponse buscarPorId(String id) {
        Usuario u = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return toResponse(u);
    }

    // Crear usuario
    public UsuarioResponse crear(UsuarioRequest req) {
        if (usuarioRepository.existsByCorreo(req.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado en el sistema");
        }

        Rol rol = rolRepository.findById(req.getIdRol())
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario u = new Usuario();
        u.setIdUsuario(req.getIdUsuario());
        u.setNombre(req.getNombre());
        u.setApellido(req.getApellido());
        u.setCorreo(req.getCorreo());
        u.setContrasena(passwordEncoder.encode(req.getContrasena()));
        u.setEstado(Usuario.EstadoUsuario.ACTIVO);
        u.setRol(rol);
        u.setIntentosFallidos(0);

        usuarioRepository.save(u);
        return toResponse(u);
    }

    // Editar usuario
    public UsuarioResponse editar(String id, UsuarioRequest req) {
        Usuario u = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        u.setNombre(req.getNombre());
        u.setApellido(req.getApellido());

        if (req.getContrasena() != null && !req.getContrasena().isBlank()) {
            u.setContrasena(passwordEncoder.encode(req.getContrasena()));
        }

        if (req.getIdRol() != null) {
            Rol rol = rolRepository.findById(req.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            u.setRol(rol);
        }

        usuarioRepository.save(u);
        return toResponse(u);
    }

    // Cambiar estado
    public UsuarioResponse cambiarEstado(String id, String estado) {
        Usuario u = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setEstado(Usuario.EstadoUsuario.valueOf(estado.toUpperCase()));
        if (estado.equalsIgnoreCase("ACTIVO")) {
            u.setIntentosFallidos(0);
        }
        usuarioRepository.save(u);
        return toResponse(u);
    }

    // Carga masiva CSV
    public List<String> cargarCsv(MultipartFile file) {
        List<String> resultados = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {
            String linea;
            int fila = 0;
            while ((linea = br.readLine()) != null) {
                fila++;
                if (fila == 1) continue; // saltar encabezado
                String[] cols = linea.split(",");
                if (cols.length < 6) {
                    resultados.add("Fila " + fila + ": formato inválido");
                    continue;
                }
                try {
                    UsuarioRequest req = new UsuarioRequest();
                    req.setIdUsuario(cols[0].trim());
                    req.setNombre(cols[1].trim());
                    req.setApellido(cols[2].trim());
                    req.setCorreo(cols[3].trim());
                    req.setContrasena(cols[4].trim());
                    req.setIdRol(cols[5].trim());
                    crear(req);
                    resultados.add("Fila " + fila + ": " + req.getCorreo() + " creado OK");
                } catch (Exception e) {
                    resultados.add("Fila " + fila + ": error - " + e.getMessage());
                }
            }
        } catch (Exception e) {
            resultados.add("Error al leer el archivo: " + e.getMessage());
        }
        return resultados;
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
            u.getIdUsuario(),
            u.getNombre(),
            u.getApellido(),
            u.getCorreo(),
            u.getEstado().name(),
            u.getRol().getNombre().name(),
            u.getFechaCreacion() != null ? u.getFechaCreacion().format(FMT) : ""
        );
    }
}