package it.bicocca.eduquest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.bicocca.eduquest.services.GamificationServices;
import it.bicocca.eduquest.services.MissionsServices;
import it.bicocca.eduquest.services.RankingServices;
import it.bicocca.eduquest.services.ranking.StrategyNotFoundException;
import it.bicocca.eduquest.services.ChallengeServices;
import it.bicocca.eduquest.dto.gamification.*;

@RestController
@RequestMapping("/api/gamification")
@CrossOrigin(origins = "http://127.0.0.1:5500") 
public class GamificationController {
	private MissionsServices missionsServices;
	private GamificationServices gamificationServices;
	private RankingServices rankingServices;
	private ChallengeServices challengeServices;
	
	private static final String INTERNAL_SERVER_ERROR = "Internal server error";
	
	public GamificationController(MissionsServices missionsServices, GamificationServices gamificationServices, RankingServices rankingServices, ChallengeServices challengeServices) {
		this.missionsServices = missionsServices;
		this.gamificationServices = gamificationServices;
		this.rankingServices = rankingServices;
		this.challengeServices = challengeServices;
	}
	
	@GetMapping("/missions/")
	public ResponseEntity<Object> getAllMissions() {
		try {
            return ResponseEntity.ok(missionsServices.getAllMissions());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR);
        }
	}
	
	@GetMapping("/missions/progresses")
	public ResponseEntity<Object> getAllMissionsProgresses(Authentication authentication) {
		String loggedIdString = authentication.getName();
        Long loggedId = Long.valueOf(loggedIdString);
        
		try {
            return ResponseEntity.ok(gamificationServices.getAllMissionsProgressesByUserId(loggedId, false));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR);
        }
	}
	
	@GetMapping("/missions/progresses/{userId}")
	public ResponseEntity<Object> getMissionsByUserId(Authentication authentication, @PathVariable Long userId, @RequestParam(required = false) Boolean onlyCompleted) {
       
		try {
			boolean showOnlyCompleted;
			if (onlyCompleted == null) {
				showOnlyCompleted = false;
			} else {
				showOnlyCompleted = onlyCompleted.booleanValue();
			}
			return ResponseEntity.ok(gamificationServices.getAllMissionsProgressesByUserId(userId, showOnlyCompleted));				
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR);
        }
	}
	
	@GetMapping("/badges/{userId}")
    public ResponseEntity<Object> getStudentBadges(@PathVariable long userId) {
        try {
        	return ResponseEntity.ok(gamificationServices.getStudentBadges(userId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
	
	@GetMapping("/ranking")
	public ResponseEntity<Object> getRankingNumberOfQuizzesCompleted(@RequestParam String rankingType, Authentication authentication) {
		try {
            return ResponseEntity.ok(rankingServices.getRanking(rankingType));
        } catch (StrategyNotFoundException e) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR);
        }
	}
	
	@GetMapping("/challenges")
	public ResponseEntity<Object> getChallengesByUserId(Authentication authentication) {
		try {
			long studentId = Long.parseLong(authentication.getName());
			return ResponseEntity.ok(challengeServices.getChallengesByUserId(studentId));
		} catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@PostMapping("/challenges")
	public ResponseEntity<Object> createChallenge(@RequestBody ChallengeCreateDTO challengeCreateDTO, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.parseLong(userIdString);
		
		try {
			return ResponseEntity.ok(challengeServices.createChallenge(userId, challengeCreateDTO));
		} catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/challenges/refresh-status")
    public ResponseEntity<Object> forceExpiryCheck() {
        try {
            challengeServices.markExpiredChallenges();
            return ResponseEntity.ok("Deadline check successfully completed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
	
}
