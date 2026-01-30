package it.bicocca.eduquest.controller; 

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.bicocca.eduquest.dto.user.UserInfoDTO;
import it.bicocca.eduquest.services.UserServices;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }
    
    // See all users
    @GetMapping
    public ResponseEntity<Object> getAllInfos(Authentication authentication) {
    	if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied, you must be authenticated");
        }
    	try {
    		List<UserInfoDTO> users = userServices.getAllUsers();
    		return ResponseEntity.ok(users);
    	} catch (RuntimeException e){
    		// 500 internal error -> list recovery fails
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno nel recupero utenti: " + e.getMessage());
    	}
    		
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserInfo(@PathVariable long id) {
    	try {
    		return ResponseEntity.ok(userServices.getUserInfo(id));
    	} catch (RuntimeException e) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    	}
    }
    
    @GetMapping("/me")
    public ResponseEntity<Object> getUserInfoFromJwt(Authentication authentication) {
    	if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied, you must be authenticated");
        }
    	try {
    		String userIdString = authentication.getName(); 
    		long userId = Long.valueOf(userIdString).longValue();
   
    		return ResponseEntity.ok(userServices.getUserInfo(userId));
    	} catch (RuntimeException e) {
    		// Valid token but there is not the user in the database
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    	}
    }
    
}