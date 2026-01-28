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

	// TODO getAllQuizzes()
	
	@GetMapping
	public ResponseEntity<?> getQuizzesByAuthorId(@RequestParam(required = false) Long authorId) {
		try {
			if (authorId != null) {
				return ResponseEntity.ok(quizService.getQuizzesByAuthorId(authorId));				
			} else {
				return ResponseEntity.ok(quizService.getAllQuizzes());
			}
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getQuizById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(quizService.getQuizById(id));
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
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
	
	@PutMapping("/{id}")
	public ResponseEntity<?> editQuiz(@PathVariable("id") long quizId, @RequestBody QuizEditDTO quiz, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		
		try {
			return ResponseEntity.ok(quizService.editQuiz(quizId, quiz, userId));
		} catch (IllegalArgumentException e){
			return ResponseEntity.status(403).body(e.getMessage());
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
	}
	
	@GetMapping("/question")
	public ResponseEntity<?> getQuestions(@RequestParam(required = false) Long authorId) {
		try {
			if (authorId != null) {
				return ResponseEntity.ok(quizService.getQuestionsByAuthorId(authorId));				
			} else {
				return ResponseEntity.ok(quizService.getAllQuestions());
			}
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
	}
	
	@PostMapping("/question")
	public ResponseEntity<?> addQuestion(@RequestBody QuestionAddDTO question, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		
		try {
			return ResponseEntity.ok(quizService.addQuestion(question, userId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
	}
	
	@PostMapping("/{quizId}/add-question/{questionId}")
	public ResponseEntity<?> addQuestionToQuiz(@PathVariable Long quizId, @PathVariable Long questionId, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		try {
			return ResponseEntity.ok(quizService.addQuestionToQuiz(quizId, questionId, userId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(403).body(e.getMessage());	
		}
	}
	
	@DeleteMapping("/{quizId}/remove-question/{questionId}")
	public ResponseEntity<?> removeQuestionFromQuiz(@PathVariable Long quizId, @PathVariable Long questionId, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		try {
			return ResponseEntity.ok(quizService.removeQuestionFromQuiz(quizId, questionId, userId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(403).body(e.getMessage());	
		}
	}
	
	@GetMapping("/{quizId}/quiz-for-student")
	public ResponseEntity<?> getQuizForStudent(@PathVariable("quizId") long quizId, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		try {
			return ResponseEntity.ok(quizService.getQuizForStudent(quizId, userId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(403).body(e.getMessage());	
		}
	}
	
}
