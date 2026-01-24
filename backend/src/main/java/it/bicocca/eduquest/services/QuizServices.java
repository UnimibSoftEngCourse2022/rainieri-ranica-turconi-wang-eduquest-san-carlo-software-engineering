package it.bicocca.eduquest.services;

import org.springframework.stereotype.Service;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.domain.quiz.Question;
import it.bicocca.eduquest.domain.quiz.*;
import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.dto.quiz.*;
import it.bicocca.eduquest.repository.UsersRepository;
import java.util.List;
import java.util.ArrayList;

@Service
public class QuizServices {
	private final QuizRepository quizRepository;
	private final UsersRepository usersRepository;
	
	public QuizServices(QuizRepository quizRepository, UsersRepository usersRepository) {
		this.quizRepository = quizRepository;
		this.usersRepository = usersRepository;
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
			QuizDTO quizDTO = new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), quiz.getMaxScore(), questionsDTO);
			quizzesDTO.add(quizDTO);
		}
		
		return quizzesDTO;
	}
	
	public QuizDTO addQuiz(QuizDTO quizDTO) {
		long id = -1;

		User user = usersRepository.findById(quizDTO.getTeacherAuthorId()).orElseThrow(() -> new RuntimeException("Cannot find teacher (martinfowler.com)"));
		
		if (!(user instanceof Teacher)) {
			// FIXME handle errors with exceptions
			return null;
		}
		
		Teacher author = (Teacher) user;
		Quiz quiz = new Quiz(id, quizDTO.getTitle(), quizDTO.getDescription(), quizDTO.getMaxScore(), author);
		quizRepository.save(quiz);
		
		return quizDTO;
	}
}
