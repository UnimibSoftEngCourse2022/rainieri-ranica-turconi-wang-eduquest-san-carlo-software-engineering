package it.bicocca.eduquest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import it.bicocca.eduquest.dto.quizAttempt.*;

import it.bicocca.eduquest.services.QuizAttemptServices;

@RestController
@RequestMapping("/api/quizAttempt")
// @CrossOrigin(origins = "*") serve per evitare blocchi se il frontend lavora su una porta diversa, lo usiamo?
public class QuizAttemptController {
	private final QuizAttemptServices quizAttemptServices;

	public QuizAttemptController(QuizAttemptServices quizAttemptServices) {
		this.quizAttemptServices = quizAttemptServices;
	}
	
	@PostMapping("/start")
    public ResponseEntity<?> startQuiz(@RequestParam Long quizId, @RequestParam Long studentId) {
        try {
            QuizSessionDTO session = quizAttemptServices.startQuiz(quizId, studentId);
            return ResponseEntity.ok(session);     
        } catch (RuntimeException e) {
        	return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
