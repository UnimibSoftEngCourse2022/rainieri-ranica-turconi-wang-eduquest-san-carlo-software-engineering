package it.bicocca.eduquest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.bicocca.eduquest.services.QuizAttemptServices;

@RestController
@RequestMapping("/api/quizAttempt")
public class QuizAttemptController {
	private final QuizAttemptServices quizAttemptServices;

	public QuizAttemptController(QuizAttemptServices quizAttemptServices) {
		this.quizAttemptServices = quizAttemptServices;
	}
}
