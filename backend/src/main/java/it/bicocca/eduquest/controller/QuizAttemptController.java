package it.bicocca.eduquest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import it.bicocca.eduquest.dto.quizAttempt.*;

import it.bicocca.eduquest.services.QuizAttemptServices;

@RestController
@RequestMapping("/api/quizAttempt")
// @CrossOrigin(origins = "*") // serve per evitare blocchi se il frontend lavora su una porta diversa, lo usiamo?
@CrossOrigin(origins = "http://127.0.0.1:5500") // Aggiungilo qui esplicitamente per test
public class QuizAttemptController {
	private final QuizAttemptServices quizAttemptServices;

	public QuizAttemptController(QuizAttemptServices quizAttemptServices) {
		this.quizAttemptServices = quizAttemptServices;
	}
	
	@GetMapping("/{quizAttemptId}")
	public ResponseEntity<Object> getQuizAttemptById(@PathVariable Long quizAttemptId, Authentication authentication) {
		String loggedIdString = authentication.getName();
        Long loggedId = Long.valueOf(loggedIdString);
        
        try {
            return ResponseEntity.ok(quizAttemptServices.getQuizAttemptById(quizAttemptId, loggedId));     
        } catch (RuntimeException e) {
        	String msg = e.getMessage();
            if (msg.contains("not found")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
        }
	}
	
	@GetMapping
    public ResponseEntity<Object> getQuizAttemptsByUserId(@RequestParam Long studentId, Authentication authentication) {
		String loggedIdString = authentication.getName();
        Long loggedId = Long.valueOf(loggedIdString);
        
        if (!loggedId.equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot see the quiz attempts of someone else!");
        }
		
		try {
            return ResponseEntity.ok(quizAttemptServices.getQuizAttemptsByUserId(studentId));     
        } catch (RuntimeException e) {
        	String msg = e.getMessage();
            if (msg.contains("not found")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
        }
    }
	
	
	@PostMapping("/start")
    public ResponseEntity<Object> startQuiz(@RequestParam Long quizId, @RequestParam Long studentId, Authentication authentication) {
		String loggedIdString = authentication.getName();
        Long loggedId = Long.valueOf(loggedIdString);
        
        if (!loggedId.equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot start a quiz of someone else!");
        }
		try {
            return ResponseEntity.ok(quizAttemptServices.startQuiz(quizId, studentId));     
        } catch (RuntimeException e) {
        	String msg = e.getMessage();

            // 404 -> quiz or user not found
            if (msg.contains("Cannot find")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }

            // 403 -> teacher wants to start a quiz
            if (msg.contains("Teacher") || msg.contains("Student")) { 
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            }

            // 400 -> quiz completed or already started
            return ResponseEntity.badRequest().body(msg);
        }
    }
	
	@PutMapping("/answers") 
	public ResponseEntity<Object> saveSingleAnswer(@RequestBody AnswerDTO answerDTO, Authentication authentication) {
		String loggedIdString = authentication.getName();
        Long loggedId = Long.valueOf(loggedIdString);
        
		try {
            return ResponseEntity.ok(quizAttemptServices.saveSingleAnswer(answerDTO, loggedId));
        } catch (RuntimeException e) {
        	String msg = e.getMessage();

            // 404 -> quizAttempt or question not found
            if (msg.contains("Cannot find") || msg.contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            
            // 403
            if (msg.contains("not your attempt")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            }

            // 400
            return ResponseEntity.badRequest().body(msg);
        }
	}
	
	@PostMapping("/{quizAttemptId}/complete")
	public ResponseEntity<Object> completeQuizAttempt(@PathVariable long quizAttemptId, Authentication authentication) {
		String loggedIdString = authentication.getName();
	    Long loggedId = Long.valueOf(loggedIdString);
		
		try {
			return ResponseEntity.ok(quizAttemptServices.completeQuizAttempt(quizAttemptId, loggedId));	
		} catch (NullPointerException e) {
            return ResponseEntity.internalServerError().body("Generic error completing the quiz.");
            
		} catch (RuntimeException e) {
			String msg = e.getMessage();

            // 404 -> attempt not found
            if (msg.contains("Cannot find") || msg.contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            
            // 403
            if (msg.contains("not your attempt")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            }

            // 400 -> attempt already completed
            return ResponseEntity.badRequest().body(msg);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Generic error completing the quiz.");
		}
	}
}
