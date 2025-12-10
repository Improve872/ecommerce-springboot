package com.project.ecommerce.security;

import com.project.ecommerce.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    // 🛑 Rutas a ignorar por este filtro (TODAS las que pusiste en permitAll)
    private static final String[] EXCLUDED_PATHS_PREFIXES = {
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/pedidos/crear",
            "/api/v1/carritos",
            "/api/v1/productos", // Si quieres que el GET a productos pase
            "/api/v1/categorias" // Si quieres que el GET a categorías pase
    };

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    // 🛑 MÉTODO CRÍTICO: Si devuelve TRUE, el filtro NO se ejecuta.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        for (String excludedPath : EXCLUDED_PATHS_PREFIXES) {
            // Usamos startsWith para manejar el wildcard '/**' del SecurityConfig
            if (path.startsWith(excludedPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Si shouldNotFilter es TRUE, este método es ignorado, pero filterChain.doFilter() se llama automáticamente.

        try {
            String token = parseJwt(request);

            if (token != null) {
                // ... (La lógica de autenticación JWT solo se ejecuta si hay token) ...
                String username = jwtUtils.getUsernameFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtils.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception ex) {
            // Esto solo se ejecuta si la ruta está protegida Y el token es inválido.
            System.err.println("❌ ERROR DE AUTENTICACIÓN JWT EN EL FILTRO: " + ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
