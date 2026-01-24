package it.bicocca.eduquest.controller;

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
	public QuizDTO addQuiz(QuizDTO quiz) {
		System.out.println("chi legge è frocio\n");
		return quizService.addQuiz(quiz);
	}
}
