package it.bicocca.eduquest.services;

import org.springframework.stereotype.Service;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.domain.quiz.Quiz;
import java.util.List;

@Service
public class QuizServices {
	private final QuizRepository quizRepository;
	
	public QuizServices(QuizRepository quizRepository) {
		this.quizRepository = quizRepository;
	}
	
	public List<Quiz> getAllQuizzes() {
		return quizRepository.findAll();
	}
}
