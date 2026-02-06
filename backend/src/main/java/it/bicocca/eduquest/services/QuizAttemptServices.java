package it.bicocca.eduquest.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import it.bicocca.eduquest.domain.multimedia.*;
import it.bicocca.eduquest.dto.quizAttempt.QuizAttemptDTO;
import it.bicocca.eduquest.repository.AnswersRepository;
import it.bicocca.eduquest.repository.QuestionsRepository;
import it.bicocca.eduquest.repository.QuizAttemptsRepository;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.TestRepository;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import it.bicocca.eduquest.dto.multimedia.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuizAttemptServices {
	private final AnswersRepository answersRepository;
	private final QuizAttemptsRepository quizAttemptsRepository;
	private final QuizRepository quizRepository;
	private final UsersRepository usersRepository;
	private final QuestionsRepository questionsRepository;
	private final TestRepository testRepository;
	private final ApplicationEventPublisher eventPublisher;
	
	public QuizAttemptServices(AnswersRepository answersRepository, QuizAttemptsRepository quizAttemptsRepository,
			QuizRepository quizRepository, UsersRepository usersRepository, QuestionsRepository questionsRepository, TestRepository testRepository,
			ApplicationEventPublisher eventPublisher) {
		this.answersRepository = answersRepository;
		this.quizAttemptsRepository = quizAttemptsRepository;
		this.quizRepository = quizRepository;
		this.usersRepository = usersRepository;
		this.questionsRepository = questionsRepository;
		this.testRepository = testRepository;
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
        return startQuiz(quizId, studentId, null);
    }
	
	public QuizSessionDTO startQuiz(long quizId, long studentId, Long testId) {
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
			
			if (testId != null) {
				Test test = testRepository.findById(testId)
						.orElseThrow(() -> new IllegalArgumentException("Test not found with ID " + testId));
				
				long attemptsDone = quizAttemptsRepository.countByStudentAndTest(user, test);
				if (test.getMaxTries() > 0 && attemptsDone >= test.getMaxTries()) {
					throw new IllegalStateException("Max attempts reached for this test (" + test.getMaxTries() + ")");
				}
				
				quizAttempt.setTest(test);
			}
			quizAttemptsRepository.save(quizAttempt);
		}
		
		List<QuestionDTO> safeQuestionsDTO = convertQuestionsToSafeDTOs(quiz.getQuestions());
		
		List<AnswerDTO> existingAnswersDTO = new ArrayList<>();
		for (Answer a : quizAttempt.getAnswers()) {
			existingAnswersDTO.add(convertAnswerToDTO(a));
		}
		
		return new QuizSessionDTO(quizAttempt.getId(), quiz.getTitle(), quiz.getDescription(), safeQuestionsDTO, existingAnswersDTO);
	}
	
	public QuizSessionDTO getQuizSession(long quizAttemptId) {
		QuizAttempt quizAttempt = quizAttemptsRepository.findById(quizAttemptId).orElseThrow(() -> new IllegalArgumentException("Cannot find a quiz attempt with the given ID"));
		
		long quizId = quizAttempt.getQuiz().getId();
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Cannot find a quiz with the given ID" + quizId));
		
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
			QuestionStats stats = question.getStats();
			QuestionStatsDTO questionStatsDTO = new QuestionStatsDTO(stats.getAverageSuccess(), stats.getTotalAnswers(), stats.getCorrectAnswer());
			
			QuestionDTO questionDTO;
			
			if (question.getQuestionType() == QuestionType.OPENED) {
				questionDTO = new QuestionDTO(question.getId(), question.getText(), question.getDifficulty(), question.getTopic(), question.getQuestionType(), null, null, question.getAuthor().getId(), questionStatsDTO);   
	        } else if (question.getQuestionType() == QuestionType.CLOSED) {
	            List<ClosedQuestionOptionDTO> safeOptions = new ArrayList<>();
	            for (ClosedQuestionOption optionDTO : ((ClosedQuestion)question).getOptions()) {
	                safeOptions.add(new ClosedQuestionOptionDTO(optionDTO.getId(), optionDTO.getText(), false)); 
	            }
	            questionDTO = new QuestionDTO(question.getId(), question.getText(), question.getDifficulty(), question.getTopic(), question.getQuestionType(), null, safeOptions, question.getAuthor().getId(), questionStatsDTO);
	        } else { 
				throw new IllegalArgumentException("Not supported question type."); 
			}
			if (question.getMultimedia() != null) {
				MultimediaSupport media = question.getMultimedia();
				MultimediaDTO mediaDTO = new MultimediaDTO();
				mediaDTO.setUrl(media.getUrl());
				mediaDTO.setType(media.getType());
				
				if (media instanceof VideoSupport) {
					mediaDTO.setIsYoutube(((VideoSupport) media).getIsYoutube());
				} else {
					mediaDTO.setIsYoutube(false);
				}
				
				questionDTO.setMultimedia(mediaDTO);
			}
			
			safeQuestions.add(questionDTO);
		}
		
		return safeQuestions;
	}
	
	private AnswerDTO convertAnswerToDTO(Answer answer) {
		AnswerDTO answerDTO = new AnswerDTO();
		
		answerDTO.setId(answer.getId());
		answerDTO.setQuizAttemptId(answer.getQuizAttempt().getId());
		answerDTO.setQuestionId(answer.getQuestion().getId());

		// FIX: Pattern Matching for instanceof
		if (answer instanceof OpenAnswer openAnswer) {
			answerDTO.setQuestionType(QuestionType.OPENED);
			answerDTO.setTextOpenAnswer(openAnswer.getText());
		} else if (answer instanceof ClosedAnswer closedAnswer) {
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
		
		if (quizAttempt.getTest() != null && quizAttempt.getTest().getMaxDuration() != null) {
			long limitMinutes = quizAttempt.getTest().getMaxDuration().toMinutes();
			
			if (limitMinutes > 0) {
				long elapsedMinutes = Duration.between(quizAttempt.getStartedAt(), LocalDateTime.now()).toMinutes();
				if (elapsedMinutes > limitMinutes + 2) {
					throw new IllegalStateException("Time limit exceeded for this test!");
				}
			}
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
		} else  if (question instanceof ClosedQuestion closedQuestion) { // FIX: Pattern Matching
			updateClosedAnswer(answer, closedQuestion, answerDTO);
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
		// FIX: Pattern Matching for instanceof
		if (a instanceof ClosedAnswer closedA) {
			return isClosedAnswerCorrect(closedA);
		} else if (a instanceof OpenAnswer openA) {
			return isOpenAnswerCorrect(openA);
		} else { 
			throw new IllegalArgumentException("Not supported question type."); 
		}
	}
	
	private boolean isClosedAnswerCorrect(ClosedAnswer closedA) {
		return closedA.getChosenOption() != null && closedA.getChosenOption().isTrue();
	}
	
	private boolean isOpenAnswerCorrect(OpenAnswer openA) {
		long questionId = openA.getQuestion().getId();
		Question question = questionsRepository.findById(questionId)
				.orElseThrow(() -> new IllegalStateException("Question not found"));
		
		question = (Question) Hibernate.unproxy(question);

		if (question.getQuestionType() != QuestionType.OPENED) {
			return false;
		}
		
		OpenQuestion openQ;
		if (question instanceof OpenQuestion) {
			openQ = (OpenQuestion) question;
		} else {
			openQ = (OpenQuestion) question; 
		}
		
		String studentText = openA.getText();

		
		if (openQ.getValidAnswers() != null) {
			for (OpenQuestionAcceptedAnswer ans : openQ.getValidAnswers()) {
				if (studentText != null && studentText.trim().equalsIgnoreCase(ans.getText().trim())) {
					return true;
				}
			}
		}
		
		return false;
	}
}