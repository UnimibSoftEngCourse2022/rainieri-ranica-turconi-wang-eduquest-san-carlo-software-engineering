package it.bicocca.eduquest.controller; 

import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import it.bicocca.eduquest.dto.user.*;
import it.bicocca.eduquest.services.UserServices;

@RestController
@RequestMapping("/auth") // http://localhost:8080/auth
public class UserController {

    private final UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    // Registration
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO dto) {
        try {
            return ResponseEntity.ok(userServices.registerUser(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO dto) {
        try {
            return ResponseEntity.ok(userServices.loginUser(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage()); // 401 = Non autorizzato
        }
    }
}