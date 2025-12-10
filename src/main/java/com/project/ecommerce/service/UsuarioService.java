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

    // metodos busqueda


     // obtiene el id del usuario a partir de su correo

    public Integer getUserIdByEmail(String email) {
        return usuarioRepository.findByCorreoIgnoreCase(email)
                .map(Usuario::getId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + email));
    }


     // obtiene el dto de un usuario a partir de su correo

    public UsuarioDTO obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo)
                .map(Mapper::toUsuarioDTO)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
    }

    // listar los usuarios
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(Mapper::toUsuarioDTO)
                .collect(Collectors.toList());
    }

    // obtener usuario por id
    public UsuarioDTO obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .map(Mapper::toUsuarioDTO)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    // metodos de modificacion

    @Transactional
    public UsuarioDTO registrar(RegistroRequestDTO dto) {

        if (usuarioRepository.findByCorreoIgnoreCase(dto.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado: " + dto.getCorreo());
        }

        // validar contraseña
        if (dto.getContrasena() == null || dto.getContrasena().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria para el registro.");
        }

        // mapear dto a entidad
        Usuario usuario = Mapper.toUsuarioEntity(dto);

        usuario.setRol(com.project.ecommerce.model.RolUsuario.CLIENTE);
        usuario.setActivo(true);

        // encriptar contraseña y setear
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));

        Usuario savedUser = usuarioRepository.save(usuario);

        // devolver dto
        return Mapper.toUsuarioDTO(savedUser);
    }

    @Transactional
    public UsuarioDTO actualizarDatos(Integer id, RegistroRequestDTO dto) {

        return usuarioRepository.findById(id).map(usuario -> {

            // actualizar campos
            if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());

            // actualizar correo y verificar
            if (dto.getCorreo() != null && !usuario.getCorreo().equalsIgnoreCase(dto.getCorreo())) {
                if (usuarioRepository.findByCorreoIgnoreCase(dto.getCorreo()).isPresent()) {
                    throw new RuntimeException("El nuevo correo ya está en uso.");
                }
                usuario.setCorreo(dto.getCorreo());
            }

            // actualizar contraseña si es nueva
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

