package it.bicocca.eduquest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import it.bicocca.eduquest.dto.quiz.QuestionAddDTO;
import it.bicocca.eduquest.dto.quiz.QuizAddDTO;
import it.bicocca.eduquest.dto.quiz.QuizEditDTO;
import it.bicocca.eduquest.services.QuizServices;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
	
	private final QuizServices quizService;
	
	public QuizController(QuizServices quizService) {
		this.quizService = quizService;
	}

	@GetMapping
	public ResponseEntity<Object> getQuizzesByAuthorId(@RequestParam(required = false) Long authorId) {
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
	public ResponseEntity<Object> getQuizById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(quizService.getQuizById(id));
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity<Object> addQuiz(@RequestBody QuizAddDTO quiz, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		
		try {
			return ResponseEntity.ok(quizService.addQuiz(quiz, userId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Object> editQuiz(@PathVariable("id") long quizId, @RequestBody QuizEditDTO quiz, Authentication authentication) {
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
	public ResponseEntity<Object> getQuestions(@RequestParam(required = false) Long authorId, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		
		try {
			if (authorId != null) {
				return ResponseEntity.ok(quizService.getQuestionsByAuthorId(authorId, userId));				
			} else {
				return ResponseEntity.ok(quizService.getAllQuestions(userId));
			}
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
	}
	
	@PostMapping("/question")
	public ResponseEntity<Object> addQuestion(@RequestBody QuestionAddDTO question, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		
		try {
			return ResponseEntity.ok(quizService.addQuestion(question, userId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
	}
	
	@PostMapping("/{quizId}/add-question/{questionId}")
	public ResponseEntity<Object> addQuestionToQuiz(@PathVariable Long quizId, @PathVariable Long questionId, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		try {
			return ResponseEntity.ok(quizService.addQuestionToQuiz(quizId, questionId, userId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(403).body(e.getMessage());	
		}
	}
	
	@DeleteMapping("/{quizId}/remove-question/{questionId}")
	public ResponseEntity<Object> removeQuestionFromQuiz(@PathVariable Long quizId, @PathVariable Long questionId, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		try {
			return ResponseEntity.ok(quizService.removeQuestionFromQuiz(quizId, questionId, userId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(403).body(e.getMessage());	
		}
	}
	
	@GetMapping("/{quizId}/quiz-for-student")
	public ResponseEntity<Object> getQuizForStudent(@PathVariable("quizId") long quizId, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.valueOf(userIdString).longValue();
		try {
			return ResponseEntity.ok(quizService.getQuizForStudent(quizId, userId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(403).body(e.getMessage());	
		}
	}
	
}
