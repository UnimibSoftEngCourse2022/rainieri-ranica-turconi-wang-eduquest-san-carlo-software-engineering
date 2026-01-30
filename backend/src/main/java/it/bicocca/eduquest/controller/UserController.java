package it.bicocca.eduquest.controller; 

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.bicocca.eduquest.dto.user.UserInfoDTO;
import it.bicocca.eduquest.dto.user.UserLoginDTO;
import it.bicocca.eduquest.dto.user.UserRegistrationDTO;
import it.bicocca.eduquest.services.UserServices;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth") // http://localhost:8080/auth
public class UserController {

    private final UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    // Registration
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody UserRegistrationDTO dto) {
        try {
            return ResponseEntity.ok(userServices.registerUser(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // login
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserLoginDTO dto) {
        try {
            return ResponseEntity.ok(userServices.loginUser(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage()); // 401 = Non autorizzato
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserInfo(@PathVariable long id) {
    	try {
    		return ResponseEntity.ok(userServices.getUserInfo(id));
    	} catch (RuntimeException e) {
    		return ResponseEntity.status(401).body(e.getMessage());
    	}
    }
    
    @GetMapping("/me")
    public ResponseEntity<Object> getUserInfoFromJwt(Authentication authentication) {
    	try {
    		long userId = (long)authentication.getPrincipal();
    		return ResponseEntity.ok(userServices.getUserInfo(userId));
    	} catch (RuntimeException e) {
    		return ResponseEntity.status(401).body(e.getMessage());
    	}
    }
    
    // see all users
    @GetMapping("/all")
    public ResponseEntity<Object> getAllInfos(Authentication authentication) {
    	if (authentication == null || !authentication.isAuthenticated()) {
    		return ResponseEntity.status(401).body("Accesso negato devi essere autenticato");
    	}
    	try {
    		List<UserInfoDTO> users = userServices.getAllUsers();
    		return ResponseEntity.ok(users);
    	} catch (RuntimeException e){
    		return ResponseEntity.status(401).body(e.getMessage());
    	}
    		
    }
}