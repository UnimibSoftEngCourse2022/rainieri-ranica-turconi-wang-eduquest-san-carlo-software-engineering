package it.bicocca.eduquest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import it.bicocca.eduquest.dto.quiz.*;
import it.bicocca.eduquest.services.QuizServices;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
	
	private final QuizServices quizService;
	
	public QuizController(QuizServices quizService) {
		this.quizService = quizService;
	}
	
	@GetMapping
	public List<QuizDTO> getAllQuizzes() {
		return quizService.getAllQuizzes();
	}
	
	@PostMapping
	public ResponseEntity<?> addQuiz(@RequestBody QuizAddDTO quiz, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		
		try {
			return ResponseEntity.ok(quizService.addQuiz(quiz, userId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
	}
}
