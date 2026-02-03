package it.bicocca.eduquest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.bicocca.eduquest.services.GamificationServices;
import it.bicocca.eduquest.services.MissionsServices;

@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = "http://127.0.0.1:5500") 
public class GamificationController {
	private MissionsServices missionsServices;
	private GamificationServices gamificationServices;
	
	public GamificationController(MissionsServices missionsServices, GamificationServices gamificationServices) {
		this.missionsServices = missionsServices;
		this.gamificationServices = gamificationServices;
	}
	
	@GetMapping
	public ResponseEntity<Object> getAllMissions() {
		try {
            return ResponseEntity.ok(missionsServices.getAllMissions());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error while getting all missions");
        }
	}
	
	@GetMapping("/progresses")
	public ResponseEntity<Object> getAllMissionsProgresses(Authentication authentication) {
		String loggedIdString = authentication.getName();
        Long loggedId = Long.valueOf(loggedIdString);
        
		try {
            return ResponseEntity.ok(gamificationServices.getAllMissionsProgressesByUserId(loggedId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error while getting all missions");
        }
	}
}
