package it.bicocca.eduquest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import it.bicocca.eduquest.dto.quiz.QuestionAddDTO;
import it.bicocca.eduquest.services.QuizServices;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
	private final QuizServices quizService;

	public QuestionController(QuizServices quizService) {
		this.quizService = quizService;
	}
	
	@GetMapping
	public ResponseEntity<Object> getQuestions(@RequestParam(required = false) Long authorId, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.parseLong(userIdString);
		
		try {
			if (authorId != null) {
				return ResponseEntity.ok(quizService.getQuestionsByAuthorId(authorId, userId));				
			} else {
				return ResponseEntity.ok(quizService.getAllQuestions(userId));
			}
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<Object> addQuestion(@RequestPart("question") QuestionAddDTO question, @RequestPart(value = "file", required = false) MultipartFile file, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.parseLong(userIdString);
		
		try {
			return ResponseEntity.ok(quizService.addQuestion(question, userId, file));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
