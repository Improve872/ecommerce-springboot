package com.project.ecommerce.service;

import com.project.ecommerce.dto.UsuarioDTO;
import com.project.ecommerce.dto.RegistroRequestDTO;
import com.project.ecommerce.mapper.Mapper;
import com.project.ecommerce.model.Usuario;
import com.project.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // --- MÉTODOS DE BÚSQUEDA ---

    /**
     * Obtiene el ID del usuario a partir de su correo. Usado por los controladores protegidos (ej. Carrito).
     */
    public Integer getUserIdByEmail(String email) {
        return usuarioRepository.findByCorreoIgnoreCase(email)
                .map(Usuario::getId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + email));
    }

    /**
     * Obtiene el DTO de un usuario a partir de su correo (Usado por el endpoint /me).
     */
    public UsuarioDTO obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo)
                .map(Mapper::toUsuarioDTO)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
    }

    // Listar todos los usuarios (CRUD)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(Mapper::toUsuarioDTO)
                .collect(Collectors.toList());
    }

    // Obtener usuario por ID (CRUD)
    public UsuarioDTO obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .map(Mapper::toUsuarioDTO)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    // --- MÉTODOS DE MODIFICACIÓN ---

    @Transactional
    public UsuarioDTO registrar(RegistroRequestDTO dto) {

        // 1. Validación de unicidad de correo
        if (usuarioRepository.findByCorreoIgnoreCase(dto.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado: " + dto.getCorreo());
        }

        // 2. Validación de contraseña
        if (dto.getContrasena() == null || dto.getContrasena().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria para el registro.");
        }

        // 3. Mapear DTO a Entidad (Asignar nombre/correo)
        Usuario usuario = Mapper.toUsuarioEntity(dto);

        // 🛑 Corrección crítica: Asignar Rol y Activo
        usuario.setRol(com.project.ecommerce.model.RolUsuario.CLIENTE);
        usuario.setActivo(true);

        // 5. Encriptar contraseña y setear
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));

        Usuario savedUser = usuarioRepository.save(usuario);

        // 6. Devolver DTO de salida
        return Mapper.toUsuarioDTO(savedUser);
    }

    @Transactional
    public UsuarioDTO actualizarDatos(Integer id, RegistroRequestDTO dto) {

        return usuarioRepository.findById(id).map(usuario -> {

            // 1. Actualizar campos básicos
            if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());

            // 2. Actualizar correo si cambia y verificar unicidad
            if (dto.getCorreo() != null && !usuario.getCorreo().equalsIgnoreCase(dto.getCorreo())) {
                if (usuarioRepository.findByCorreoIgnoreCase(dto.getCorreo()).isPresent()) {
                    throw new RuntimeException("El nuevo correo ya está en uso.");
                }
                usuario.setCorreo(dto.getCorreo());
            }

            // 3. Actualizar contraseña solo si viene nueva
            if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
                usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
            }

            Usuario updatedUser = usuarioRepository.save(usuario);
            return Mapper.toUsuarioDTO(updatedUser);

        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}

