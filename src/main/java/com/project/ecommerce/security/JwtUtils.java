package com.project.ecommerce.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    // 1. INYECTAR desde application.properties
    // Asegúrate de que este nombre (jwt.secret) coincida con tu archivo de propiedades.
    @Value("${jwt.secret}")
    private String SECRET_KEY_STRING;

    // 2. INYECTAR el tiempo de validez
    // El valor debe estar en milisegundos (ms).
    @Value("${jwt.expirationMs}")
    private long JWT_TOKEN_VALIDITY; // El nombre del campo lo cambié para que refleje la inyección.

    // Método privado para convertir la cadena de clave en SecretKey (necesario para JJWT)
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());
    }

    // Obtener username (Subject) del token
    public String getUsernameFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getSubject);
        } catch (Exception e) {
            // Manejo de errores simplificado. Si no puede obtenerlo, retorna null.
            return null;
        }
    }

    // Obtener expiración del token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Resolver cualquier claim específico del token
    public <T> T getClaimFromToken(String token, Function<Claims, T> resolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return resolver.apply(claims);
    }

    // Parser principal para obtener todos los Claims
    private Claims getAllClaimsFromToken(String token) {
        // Usa el parser para construir la clave de firma y validar el token
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Verifica si el token ha expirado
    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        // Retorna verdadero si la fecha de expiración es anterior a la fecha actual
        return expiration != null && expiration.before(new Date());
    }

    // Generar token (usado en el login)
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Puedes añadir roles u otra información útil aquí
        claims.put("roles", userDetails.getAuthorities());

        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // Generalmente el correo electrónico o ID
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validación de token (usado en el filtro JWT)
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);

        // Verifica que el username exista, que coincida con el UserDetails y que no haya expirado
        return (username != null &&
                username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token));
    }
}
