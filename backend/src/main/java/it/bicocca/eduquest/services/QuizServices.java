package it.bicocca.eduquest.services;

import org.springframework.stereotype.Service;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.domain.quiz.Question;
import it.bicocca.eduquest.domain.quiz.*;
import it.bicocca.eduquest.dto.quiz.*;
import java.util.List;
import java.util.ArrayList;

@Service
public class QuizServices {
	private final QuizRepository quizRepository;
	
	public QuizServices(QuizRepository quizRepository) {
		this.quizRepository = quizRepository;
	}
	
	public List<QuizDTO> getAllQuizzes() {
		List<Quiz> quizzes = quizRepository.findAll();
		
		List<QuizDTO> quizzesDTO = new ArrayList<QuizDTO>();
		for (Quiz quiz : quizzes) {
			List<QuestionDTO> questionsDTO = new ArrayList<QuestionDTO>();
			for (Question question : quiz.getQuestions()) {
				QuestionDTO questionDTO = new QuestionDTO(question.getId(), question.getText(), question.getDifficulty(), question.getTopic(), QuestionType.OPENED, new ArrayList<String>(), new ArrayList<ClosedQuestionOptionDTO>());	
				questionsDTO.add(questionDTO);			
			}
			QuizDTO quizDTO = new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getDuration(), quiz.getMaxTries(), questionsDTO);
			quizzesDTO.add(quizDTO);
		}
		
		return quizzesDTO;
	}
}
