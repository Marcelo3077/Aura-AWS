package com.example.aura.Security.Config;

import com.example.aura.Security.Filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/services/**").permitAll()
                        .requestMatchers("/api/search/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/technicians/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()

                        .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN", "SUPERADMIN")
                        .requestMatchers("/api/reservations/**").hasAnyRole("USER", "TECHNICIAN", "ADMIN", "SUPERADMIN")
                        .requestMatchers("/api/payments/**").hasAnyRole("USER", "ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/support-tickets/**").hasAnyRole("USER", "TECHNICIAN")

                        .requestMatchers(HttpMethod.POST, "/api/technicians").permitAll() // Registro público
                        .requestMatchers(HttpMethod.PUT, "/api/technicians/**").hasAnyRole("TECHNICIAN", "ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/technicians/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers("/api/technician-services/**").hasAnyRole("TECHNICIAN", "ADMIN", "SUPERADMIN")
                        .requestMatchers("/api/certifications/**").hasAnyRole("TECHNICIAN", "ADMIN", "SUPERADMIN")
                        .requestMatchers("/api/schedules/**").hasAnyRole("TECHNICIAN", "ADMIN", "SUPERADMIN")

                        .requestMatchers("/api/admins/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers("/api/support-tickets/assign/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers("/api/support-tickets/unassigned").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "SUPERADMIN")

                        .requestMatchers("/api/chats/**").authenticated()
                        .requestMatchers("/api/messages/**").authenticated()

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
