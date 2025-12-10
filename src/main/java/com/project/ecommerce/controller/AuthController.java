package com.project.ecommerce.controller;

import com.project.ecommerce.dto.LoginRequest;
import com.project.ecommerce.dto.LoginResponseDTO;
import com.project.ecommerce.dto.RegistroRequestDTO;
import com.project.ecommerce.security.JwtUtils;
import com.project.ecommerce.service.UsuarioService;
import com.project.ecommerce.model.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistroRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getCorreo(),
                        loginRequest.getContrasena()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario userDetails = (Usuario) authentication.getPrincipal();

        String jwt = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(
                new LoginResponseDTO(
                        jwt,
                        "Bearer",
                        userDetails.getId(),
                        userDetails.getNombre(),
                        userDetails.getCorreo(),
                        userDetails.getRol().name()
                )
        );
    }
}

