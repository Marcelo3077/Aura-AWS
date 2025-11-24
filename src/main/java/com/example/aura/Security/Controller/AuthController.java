package com.example.aura.Security.Controller;

import com.example.aura.Security.DTO.AuthResponseDTO;
import com.example.aura.Security.DTO.LoginRequestDTO;
import com.example.aura.Security.DTO.RegisterRequestDTO;
import com.example.aura.Security.Service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authenticationService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hello")
    public String saludar(){
        return "Hola DBP hemos deployado";
    }

    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser() {
        return ResponseEntity.ok("Authenticated successfully");
    }
}
