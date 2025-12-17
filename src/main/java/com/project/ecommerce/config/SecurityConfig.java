package com.project.ecommerce.config;

// ... (Todas tus importaciones existentes) ...
import com.project.ecommerce.security.JwtAuthenticationEntryPoint;
import com.project.ecommerce.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // <-- ¡Asegúrate de tener esta importación!


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final DaoAuthenticationProvider authenticationProvider;

    @Autowired
    public SecurityConfig(
            JwtAuthenticationEntryPoint unauthorizedHandler,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CorsConfigurationSource corsConfigurationSource,
            @Autowired(required = false) DaoAuthenticationProvider authenticationProvider
    ) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.authenticationProvider = authenticationProvider;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 1. Endpoints de Autenticación/Registro
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()

                        // 2. Endpoints de Lectura de Productos y Categorías
                        .requestMatchers(HttpMethod.GET, "/api/v1/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()

                        // 🛑 3. Endpoints de PEDIDOS (CREACIÓN - POST) - FORZAMOS PERMITALL
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos/crear/**").permitAll()

                        // 🛑 4. Endpoints de CARRITO (TODAS LAS OPERACIONES) - FORZAMOS PERMITALL
                        .requestMatchers("/api/v1/carritos/**").permitAll()

                        // 5. Endpoints de Swagger/Documentación
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // El resto de rutas requiere un token válido
                        .anyRequest().authenticated()
                );

        if (authenticationProvider != null) {
            http.authenticationProvider(authenticationProvider);
        }

        // filtro para que el login funcione
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}




