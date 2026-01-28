package it.bicocca.eduquest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import it.bicocca.eduquest.services.QuizAttemptServices;

@RestController
@RequestMapping("/api/quizAttempt")
public class QuizAttemptController {
	private final QuizAttemptServices quizAttemptServices;

	public QuizAttemptController(QuizAttemptServices quizAttemptServices) {
		this.quizAttemptServices = quizAttemptServices;
	}
}
