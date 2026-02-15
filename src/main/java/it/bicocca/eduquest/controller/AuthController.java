package it.bicocca.eduquest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.bicocca.eduquest.dto.user.UserLoginDTO;
import it.bicocca.eduquest.dto.user.UserRegistrationDTO;
import it.bicocca.eduquest.services.UserServices;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserServices userServices;

    public AuthController(UserServices userServices) {
        this.userServices = userServices;
    }
	
    // Registration
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody UserRegistrationDTO dto) {
        try {
            return ResponseEntity.ok(userServices.registerUser(dto));
        } catch (RuntimeException e) {
        	// 400 -> bad request (incorrect or duplicated data)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserLoginDTO dto) {
        try {
            return ResponseEntity.ok(userServices.loginUser(dto));
        } catch (RuntimeException e) {
        	// 401 -> failed login
            return ResponseEntity.status(401).body(e.getMessage()); // 401 = Not authorized
        }
    }
}
