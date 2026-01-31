package it.bicocca.eduquest.services;

import org.hibernate.Hibernate;
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
import it.bicocca.eduquest.domain.events.*;
import org.springframework.context.ApplicationEventPublisher;
import it.bicocca.eduquest.dto.quizAttempt.*;
import it.bicocca.eduquest.dto.quiz.*;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class QuizAttemptServices {
	private final AnswersRepository answersRepository;
	private final QuizAttemptsRepository quizAttemptsRepository;
	private final QuizRepository quizRepository;
	private final UsersRepository usersRepository;
	private final QuestionsRepository questionsRepository;
	private final ApplicationEventPublisher eventPublisher;
	
	public QuizAttemptServices(AnswersRepository answersRepository, QuizAttemptsRepository quizAttemptsRepository,
			QuizRepository quizRepository, UsersRepository usersRepository, QuestionsRepository questionsRepository,
			ApplicationEventPublisher eventPublisher) {
		this.answersRepository = answersRepository;
		this.quizAttemptsRepository = quizAttemptsRepository;
		this.quizRepository = quizRepository;
		this.usersRepository = usersRepository;
		this.questionsRepository = questionsRepository;
		this.eventPublisher = eventPublisher;
	}

	public QuizAttemptDTO getQuizAttemptById(long id, long userId) {
		QuizAttempt quizAttempt = quizAttemptsRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find quiz attempt with the given ID"));
		if (quizAttempt.getStudent().getId() != userId) {
			throw new RuntimeException("Not authorized");
		}
		QuizAttemptDTO quizAttemptDTO = new QuizAttemptDTO(quizAttempt.getId(), quizAttempt.getQuiz().getId(), quizAttempt.getQuiz().getTitle(), 
				   quizAttempt.getStudent().getId(), quizAttempt.getStudent().getName(), 
				   quizAttempt.getStudent().getSurname(), quizAttempt.getScore(), quizAttempt.getMaxScore(), 
				   quizAttempt.getStartedAt(), quizAttempt.getFinishedAt(), quizAttempt.getStatus());
		return quizAttemptDTO;
	}
	
	public List<QuizAttemptDTO> getQuizAttemptsByUserId(long userId) {
		List<QuizAttempt> quizAttempts = quizAttemptsRepository.findByStudentId(userId);
		
		List<QuizAttemptDTO> quizAttemptsDTO = new ArrayList<>();
		for (QuizAttempt quizAttempt : quizAttempts) {
			if (quizAttempt.getStudent().getId() != userId) {
				continue;
			}
			QuizAttemptDTO quizAttemptDTO = new QuizAttemptDTO(quizAttempt.getId(), quizAttempt.getQuiz().getId(), quizAttempt.getQuiz().getTitle(), 
															   quizAttempt.getStudent().getId(), quizAttempt.getStudent().getName(), 
															   quizAttempt.getStudent().getSurname(), quizAttempt.getScore(), quizAttempt.getMaxScore(), 
															   quizAttempt.getStartedAt(), quizAttempt.getFinishedAt(), quizAttempt.getStatus());
			quizAttemptsDTO.add(quizAttemptDTO);
		}
		return quizAttemptsDTO;
	}
	
	public QuizSessionDTO startQuiz(long quizId, long studentId) {
		// FIX: RuntimeException -> IllegalArgumentException
		User user = usersRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Cannot find a student with the given ID"));
		
		if (!(user instanceof Student)) {
			// FIX: RuntimeException -> IllegalArgumentException
			throw new IllegalArgumentException("Given ID is associated to a Teacher, not a Student");
		}
		
		// FIX: RuntimeException -> IllegalArgumentException
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Cannot find quiz with ID " + quizId));
		
		Optional<QuizAttempt> existingAttemptOpt = quizAttemptsRepository
	            .findByStudentAndQuizAndStatus(user, quiz, QuizAttemptStatus.STARTED);
		
		QuizAttempt quizAttempt;
		
		if (existingAttemptOpt.isPresent()) {
			quizAttempt = existingAttemptOpt.get(); 
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
	

	public AnswerDTO saveSingleAnswer(AnswerDTO answerDTO, long requestUserId) {
		QuizAttempt quizAttempt = getValidQuizAttempt(answerDTO.getQuizAttemptId());
		
		if (!quizAttempt.getStudent().getId().equals(requestUserId)) {
			// FIX: RuntimeException -> IllegalStateException (Accesso negato/Stato non valido per questo utente)
	        throw new IllegalStateException("This is not your attempt! You cannot edit it.");
	    }
		
		// FIX: RuntimeException -> IllegalArgumentException
		Question question = questionsRepository.findById(answerDTO.getQuestionId())
				.orElseThrow(() -> new IllegalArgumentException("Cannot find a Question with the given ID"));
		
		Answer answer = getOrCreateAnswer(quizAttempt, question);
		
		updateAnswerContent(answer, question, answerDTO);
		
		Answer savedAnswer = answersRepository.save(answer);
		
		return convertAnswerToDTO(savedAnswer);
	}
	
	
	public QuizAttemptDTO completeQuizAttempt(long quizAttemptId, long requestUserId) {
		
		QuizAttempt quizAttempt = getValidQuizAttempt(quizAttemptId);
		
		if (!quizAttempt.getStudent().getId().equals(requestUserId)) {
			// FIX: RuntimeException -> IllegalStateException
	        throw new IllegalStateException("This is not your attempt! You cannot complete it.");
	    }
		
		quizAttempt.setFinishedAt(LocalDateTime.now());
		quizAttempt.setStatus(QuizAttemptStatus.COMPLETED);
		
		List<Answer> answers = quizAttempt.getAnswers();
		quizAttempt.setMaxScore(answers.size());
		
		int score = calculateScore(answers);
		quizAttempt.setScore(score);
		
		quizAttemptsRepository.save(quizAttempt);
		
		eventPublisher.publishEvent(new QuizCompletedEvent(this, quizAttempt));
		
		return new QuizAttemptDTO(quizAttempt.getId(), quizAttempt.getQuiz().getId(), quizAttempt.getQuiz().getTitle(), quizAttempt.getStudent().getId(),
				quizAttempt.getStudent().getName(), quizAttempt.getStudent().getSurname(), quizAttempt.getScore(), quizAttempt.getMaxScore(),
				quizAttempt.getStartedAt(), quizAttempt.getFinishedAt(), quizAttempt.getStatus());
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

	
	
	private QuizAttempt getValidQuizAttempt(long quizAttemptId) {
		// FIX: RuntimeException -> IllegalArgumentException
		QuizAttempt quizAttempt = quizAttemptsRepository.findById(quizAttemptId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find a QuizAttempt with the given ID"));
		
		if (quizAttempt.getStatus() != QuizAttemptStatus.STARTED) {
			// FIX: RuntimeException -> IllegalStateException
			throw new IllegalStateException("You cannot edit a quiz that has already been submitted or has expired!");
		}
		return quizAttempt;
	}

	private Answer getOrCreateAnswer(QuizAttempt quizAttempt, Question question) {
		Optional<Answer> existingAnswer = answersRepository.findByQuizAttemptAndQuestion(quizAttempt, question);
		
		if (existingAnswer.isPresent()) {
			return existingAnswer.get();
		}
		
		Answer answer;
		if (question instanceof OpenQuestion) {
			answer = new OpenAnswer();
		} else if (question instanceof ClosedQuestion) {
			answer = new ClosedAnswer();
		} else { 
			throw new IllegalArgumentException("Not supported question type."); 
		}
		answer.setQuizAttempt(quizAttempt);
		answer.setQuestion(question);
		return answer;
	}

	private void updateAnswerContent(Answer answer, Question question, AnswerDTO answerDTO) {
		if (question instanceof OpenQuestion) {
			updateOpenAnswer(answer, answerDTO);
		} else  if (question instanceof ClosedQuestion) {
			updateClosedAnswer(answer, (ClosedQuestion) question, answerDTO);
		} else { 
			throw new IllegalArgumentException("Not supported question type."); 
		}
	}

	private void updateOpenAnswer(Answer answer, AnswerDTO answerDTO) {
		if (!(answer instanceof OpenAnswer)) {
			// FIX: RuntimeException -> IllegalStateException
            throw new IllegalStateException("Question/answer inconsistency");
       } 
       ((OpenAnswer)answer).setText(answerDTO.getTextOpenAnswer());
	}

	private void updateClosedAnswer(Answer answer, ClosedQuestion question, AnswerDTO answerDTO) {
		if (!(answer instanceof ClosedAnswer)) {
			// FIX: RuntimeException -> IllegalStateException
            throw new IllegalStateException("Question/answer inconsistency");
		} 
		if (answerDTO.getSelectedOptionId() == null) {
			// FIX: RuntimeException -> IllegalArgumentException
            throw new IllegalArgumentException("You must select an option!");
        }
		
		ClosedQuestionOption selectedOption = null;
		
		for (ClosedQuestionOption opt : question.getOptions()) {
            if (opt.getId() == answerDTO.getSelectedOptionId()) {
                selectedOption = opt;
                break;
            }
        }

        if (selectedOption == null) {
        	// FIX: RuntimeException -> IllegalArgumentException
            throw new IllegalArgumentException("Selected option is invalid or does not belong to this question");
        }
        
        ((ClosedAnswer) answer).setChosenOption(selectedOption);
	}
	
	
	
	private int calculateScore(List<Answer> answers) {
		int score = 0;
		for (Answer a : answers) {
			boolean isCorrect = isAnswerCorrect(a);
			a.setCorrect(isCorrect);
			if (isCorrect) {
				score++;
			}
		}
		return score;
	}
	
	private boolean isAnswerCorrect(Answer a) {
		if (a instanceof ClosedAnswer) {
			return isClosedAnswerCorrect((ClosedAnswer)a);
		} else if (a instanceof OpenAnswer) {
			return isOpenAnswerCorrect((OpenAnswer)a);
		} else { 
			throw new IllegalArgumentException("Not supported question type."); 
		}
	}
	
	private boolean isClosedAnswerCorrect(ClosedAnswer closedA) {
		return closedA.getChosenOption() != null && closedA.getChosenOption().isTrue();
	}
	
	private boolean isOpenAnswerCorrect(OpenAnswer openA) {
		OpenQuestion openQ = (OpenQuestion) Hibernate.unproxy(openA.getQuestion());
		String studentText = openA.getText();
		
		if (studentText != null && openQ.getValidAnswers() != null) {
			for (OpenQuestionAcceptedAnswer validAnswer : openQ.getValidAnswers()) {
				if (studentText.trim().equalsIgnoreCase(validAnswer.getText().trim())) {
					return true;
				}
			}
		}
		return false;
	}
}