package it.bicocca.eduquest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import it.bicocca.eduquest.dto.quiz.QuizAddDTO;
import it.bicocca.eduquest.dto.quiz.QuizEditDTO;
import it.bicocca.eduquest.services.QuizServices;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
	
	private static final String CANNOT_FIND_MSG = "Cannot find";
	private static final String CANNOT_EDIT_MSG = "cannot edit";
	
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
			// 500 internal error -> list recovery fails
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> getQuizById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(quizService.getQuizById(id));
		} catch (RuntimeException e) {
			// 404 -> quiz not found
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());		}
	}
	
	@PostMapping
	public ResponseEntity<Object> addQuiz(@RequestBody QuizAddDTO quiz, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.parseLong(userIdString);
		
		try {
			return ResponseEntity.ok(quizService.addQuiz(quiz, userId));
		} catch (RuntimeException e) {
			// 400 -> bad request (for example, empty title or user!=teacher
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Object> editQuiz(@PathVariable("id") long quizId, @RequestBody QuizEditDTO quiz, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.parseLong(userIdString);
		
		try {
			return ResponseEntity.ok(quizService.editQuiz(quizId, quiz, userId));
		} catch (IllegalArgumentException e){
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (RuntimeException e) {
			String msg = e.getMessage();
            
            if (msg.contains(CANNOT_FIND_MSG)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg); // 404
            }
            
            if (msg.contains(CANNOT_EDIT_MSG)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg); // 403
            }

            // 400 -> other generic errors
			return ResponseEntity.badRequest().body(msg);
		}
	}
	
	@PostMapping("/{quizId}/questions/{questionId}")
	public ResponseEntity<Object> addQuestionToQuiz(@PathVariable Long quizId, @PathVariable Long questionId, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.parseLong(userIdString);
		try {
			return ResponseEntity.ok(quizService.addQuestionToQuiz(quizId, questionId, userId));
		} catch (RuntimeException e) {
			String msg = e.getMessage();
			
			if (msg.contains(CANNOT_FIND_MSG)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg); // 404
			}
			
			if (msg.contains(CANNOT_EDIT_MSG)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg); // 403
			}
			
			return ResponseEntity.badRequest().body(msg); // 400	
		}
	}
	
	@DeleteMapping("/{quizId}/questions/{questionId}")
	public ResponseEntity<Object> removeQuestionFromQuiz(@PathVariable Long quizId, @PathVariable Long questionId, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.parseLong(userIdString);
		try {
			return ResponseEntity.ok(quizService.removeQuestionFromQuiz(quizId, questionId, userId));
		} catch (RuntimeException e) {
			String msg = e.getMessage();
			
			// Quiz or qomanda not found
			if (msg.contains(CANNOT_FIND_MSG)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
			}
			
			// Edit a quiz of another author
			if (msg.contains(CANNOT_EDIT_MSG)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
			}
			
			// Other erros, for example "Question already included in the quiz!"
			return ResponseEntity.badRequest().body(msg);	
		}
	}
	
	@GetMapping("/{quizId}/quiz-for-student")
	public ResponseEntity<Object> getQuizForStudent(@PathVariable("quizId") long quizId, Authentication authentication) {
		String userIdString = authentication.getName();
		long userId = Long.parseLong(userIdString);
		try {
			return ResponseEntity.ok(quizService.getQuizForStudent(quizId, userId));
		} catch (RuntimeException e) {
			String msg = e.getMessage();

			// 404 -> quiz not Found
			if (msg.contains(CANNOT_FIND_MSG)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
			}

			// Teacher wants to compile a quiz
			if (msg.contains("Teacher")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
			}
			
			// 500 -> internal error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
		}
	}
	
}

