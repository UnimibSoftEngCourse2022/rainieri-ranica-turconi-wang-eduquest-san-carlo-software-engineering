package it.bicocca.eduquest.services;

import org.springframework.stereotype.Service;

import it.bicocca.eduquest.dto.quizAttempt.QuizAttemptDTO;
import it.bicocca.eduquest.repository.AnswersRepository;
import it.bicocca.eduquest.repository.QuestionsRepository;
import it.bicocca.eduquest.repository.QuizAttemptsRepository;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.UsersRepository;
import it.bicocca.eduquest.domain.quiz.*;
import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.domain.answers.*;
import it.bicocca.eduquest.dto.quizAttempt.*;
import it.bicocca.eduquest.dto.quiz.*;
import it.bicocca.eduquest.repository.*;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

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
	
	public QuizSessionDTO startQuiz(long quizId, long studentId) {
		User user = usersRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Cannot find a student with the given ID"));
		
		if (!(user instanceof Student)) {
			throw new RuntimeException("Given ID is associated to a Teacher, not a Student");
		}
		
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Cannot find quiz with ID " + quizId));
		
		Optional<QuizAttempt> existingAttemptOpt = quizAttemptsRepository
	            .findByStudentAndQuizAndStatus(user, quiz, QuizAttemptStatus.STARTED);
		
		QuizAttempt quizAttempt;
		
		if (existingAttemptOpt.isPresent()) {
			quizAttempt = existingAttemptOpt.get(); // Resume existing attempt
		} else {
			quizAttempt = new QuizAttempt(user, quiz);
			quizAttemptsRepository.save(quizAttempt);
		}
		
		List<QuestionDTO> safeQuestionsDTO = convertQuestionsToSafeDTOs(quiz.getQuestions());
		
		List<AnswerDTO> existingAnswersDTO = new ArrayList<>();
		for (Answer a : quizAttempt.getAnswers()) {
			existingAnswersDTO.add(convertAnswerToDTO(a));
		}
		
		return new QuizSessionDTO(quizAttempt.getId(), quiz.getTitle(), quiz.getDescription(), safeQuestionsDTO, existingAnswersDTO);
	}
	
	private List<QuestionDTO> convertQuestionsToSafeDTOs(List<Question> questions) {
		List<QuestionDTO> safeQuestions = new ArrayList<>();
		
		for (Question question : questions) {
			if (question.getQuestionType() == QuestionType.OPENED) {
				safeQuestions.add(new QuestionDTO(question.getId(), question.getText(), question.getDifficulty(), question.getTopic(), question.getQuestionType(), null, null, question.getAuthor().getId()));   
	        } else if (question.getQuestionType() == QuestionType.CLOSED) {
	            List<ClosedQuestionOptionDTO> safeOptions = new ArrayList<>();
	            for (ClosedQuestionOption optionDTO : ((ClosedQuestion)question).getOptions()) {
	                safeOptions.add(new ClosedQuestionOptionDTO(optionDTO.getId(), optionDTO.getText(), false)); 
	            }
	            safeQuestions.add(new QuestionDTO(question.getId(), question.getText(), question.getDifficulty(), question.getTopic(), question.getQuestionType(), null, safeOptions, question.getAuthor().getId()));
	        } else { 
				throw new IllegalArgumentException("Not supported question type."); 
			}
		}
		
		return safeQuestions;
	}
	
	private AnswerDTO convertAnswerToDTO(Answer answer) {
		AnswerDTO answerDTO = new AnswerDTO();
		
		answerDTO.setId(answer.getId());
		answerDTO.setQuizAttemptId(answer.getQuizAttempt().getId());
		answerDTO.setQuestionId(answer.getQuestion().getId());

		if (answer instanceof OpenAnswer) {
			answerDTO.setQuestionType(QuestionType.OPENED);
			answerDTO.setTextOpenAnswer(((OpenAnswer)answer).getText());
		} else if (answer instanceof ClosedAnswer) {
			ClosedAnswer closedAnswer = (ClosedAnswer) answer;
			answerDTO.setQuestionType(QuestionType.CLOSED);
			answerDTO.setSelectedOptionId(closedAnswer.getChosenOption().getId());
			answerDTO.setSelectedOptionText(closedAnswer.getChosenOption().getText());
		} else { 
			throw new IllegalArgumentException("Not supported answer type."); 
		}
		
		return answerDTO;
	}
	
}
