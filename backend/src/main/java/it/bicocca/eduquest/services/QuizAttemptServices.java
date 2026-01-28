package it.bicocca.eduquest.services;

import org.springframework.stereotype.Service;

import it.bicocca.eduquest.dto.quizAttempt.QuizAttemptDTO;
import it.bicocca.eduquest.repository.AnswersRepository;
import it.bicocca.eduquest.repository.QuestionsRepository;
import it.bicocca.eduquest.repository.QuizAttemptsRepository;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.UsersRepository;

@Service
public class QuizAttemptServices {
	private final AnswersRepository answersRepository;
	private final QuizAttemptsRepository quizAttemptsRepository;
	private final QuizRepository quizRepository;
	private final UsersRepository usersRepository;
	private final QuestionsRepository questionsRepository;
	
	public QuizAttemptServices(AnswersRepository answersRepository, QuizAttemptsRepository quizAttemptsRepository,
			QuizRepository quizRepository, UsersRepository usersRepository, QuestionsRepository questionsRepository) {
		this.answersRepository = answersRepository;
		this.quizAttemptsRepository = quizAttemptsRepository;
		this.quizRepository = quizRepository;
		this.usersRepository = usersRepository;
		this.questionsRepository = questionsRepository;
	}
	
	public QuizAttemptDTO startQuiz(long quizId, long studentId) {
		
		
		return null;
	}
	
}
