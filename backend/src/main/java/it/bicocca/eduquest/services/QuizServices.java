package it.bicocca.eduquest.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.bicocca.eduquest.domain.quiz.ClosedQuestion;
import it.bicocca.eduquest.domain.quiz.ClosedQuestionOption;
import it.bicocca.eduquest.domain.quiz.OpenQuestion;
import it.bicocca.eduquest.domain.quiz.OpenQuestionAcceptedAnswer;
import it.bicocca.eduquest.domain.quiz.Question;
import it.bicocca.eduquest.domain.quiz.QuestionStats;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.users.Teacher;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.dto.quiz.ClosedQuestionOptionDTO;
import it.bicocca.eduquest.dto.quiz.QuestionAddDTO;
import it.bicocca.eduquest.dto.quiz.QuestionDTO;
import it.bicocca.eduquest.dto.quiz.QuestionStatsDTO;
import it.bicocca.eduquest.dto.quiz.QuestionType;
import it.bicocca.eduquest.dto.quiz.QuizAddDTO;
import it.bicocca.eduquest.dto.quiz.QuizDTO;
import it.bicocca.eduquest.dto.quiz.QuizEditDTO;
import it.bicocca.eduquest.dto.quiz.QuizStatsDTO;
import it.bicocca.eduquest.repository.QuestionsRepository;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.UsersRepository;

@Service
public class QuizServices {
	
	private static final String CANNOT_FIND_QUIZ_MSG = "Cannot find a quiz with the given ID";
	
	private final QuizRepository quizRepository;
	private final UsersRepository usersRepository;
	private final QuestionsRepository questionsRepository;
	
	public QuizServices(QuizRepository quizRepository, UsersRepository usersRepository, QuestionsRepository questionsRepository) {
		this.quizRepository = quizRepository;
		this.usersRepository = usersRepository;
		this.questionsRepository = questionsRepository;
	}
	
	public QuizDTO getQuizById(long id) {
		Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cannot find quiz with ID " + id));
		QuizStatsDTO statsDTO = new QuizStatsDTO(quiz.getStats().getAverageScore(), quiz.getStats().getTotalAttempts());
		List<QuestionDTO> questionsDTO = new ArrayList<>();
		
		// FIX: Diamond operator <>
		List<String> validAnswersOpenQuestionDTO = new ArrayList<>();
		List<ClosedQuestionOptionDTO> closedQuestionOptionsDTO = new ArrayList<>();
		
		for (Question question : quiz.getQuestions()) {
			// FIX: Pattern Matching for instanceof
			if (question instanceof OpenQuestion openQuestion) {		
				List<OpenQuestionAcceptedAnswer> validAnswersOpenQuestion = openQuestion.getValidAnswers();
				for (OpenQuestionAcceptedAnswer a : validAnswersOpenQuestion) {
					validAnswersOpenQuestionDTO.add(a.getText());
				}
			} else if (question instanceof ClosedQuestion closedQuestion) {
				List<ClosedQuestionOption> optionsClosedQuestion = closedQuestion.getOptions();
				for (ClosedQuestionOption c : optionsClosedQuestion) {
					closedQuestionOptionsDTO.add(new ClosedQuestionOptionDTO(c.getId(), c.getText(), c.isTrue()));
				}
			}
			
			QuestionStats stats = question.getStats();
			QuestionStatsDTO questionStatsDTO = new QuestionStatsDTO(stats.getAverageSuccess(), stats.getTotalAnswers(), stats.getCorrectAnswer());
			QuestionDTO questionDTO = new QuestionDTO(question.getId(), question.getText(), question.getDifficulty(), question.getTopic(), question.getQuestionType(), validAnswersOpenQuestionDTO, closedQuestionOptionsDTO, question.getAuthor().getId(), questionStatsDTO);	
			questionsDTO.add(questionDTO);
		}

		return new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), questionsDTO, statsDTO);
	}
	
	public List<QuizDTO> getAllQuizzes() {
		List<Quiz> quizzes = quizRepository.findAll();
		
		List<QuizDTO> quizzesDTO = new ArrayList<>();
		for (Quiz quiz : quizzes) {
			List<QuestionDTO> questionsDTO = convertQuestionsToDTOs(quiz.getQuestions());
			QuizStatsDTO statsDTO = new QuizStatsDTO(quiz.getStats().getAverageScore(), quiz.getStats().getTotalAttempts());
			QuizDTO quizDTO = new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), questionsDTO, statsDTO);
			quizzesDTO.add(quizDTO);
		}
		
		return quizzesDTO;
	}
	
	public List<QuizDTO> getQuizzesByAuthorId(long authorId) {
		List<Quiz> quizzes = quizRepository.findAll();
		
		List<QuizDTO> quizzesDTO = new ArrayList<>();
		for (Quiz quiz : quizzes) {
			if (quiz.getAuthor().getId() != authorId) {
				continue;
			}
			List<QuestionDTO> questionsDTO = convertQuestionsToDTOs(quiz.getQuestions());
			QuizStatsDTO statsDTO = new QuizStatsDTO(quiz.getStats().getAverageScore(), quiz.getStats().getTotalAttempts());
			QuizDTO quizDTO = new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), questionsDTO, statsDTO);
			quizzesDTO.add(quizDTO);
		}
		
		return quizzesDTO;
	}
	
	public QuizDTO addQuiz(QuizAddDTO quizAddDTO, long userIdFromRequest) {
		User user = usersRepository.findById(userIdFromRequest).orElseThrow(() -> new IllegalArgumentException("Cannot find a teacher with the given ID"));
		if (!(user instanceof Teacher)) {
			throw new IllegalArgumentException("Given ID is associated to a Student, not a Teacher");
		}
		
		if (quizAddDTO.getTitle() == null || quizAddDTO.getTitle().trim().isEmpty()) {
			throw new IllegalArgumentException("The quiz title cannot be empty!");
		}
		if (quizAddDTO.getDescription() == null || quizAddDTO.getDescription().trim().isEmpty()) {
			throw new IllegalArgumentException("The quiz description cannot be empty!");
		}
		
		Teacher author = (Teacher) user;
		
		Quiz quiz = new Quiz(quizAddDTO.getTitle(), quizAddDTO.getDescription(), author);
		quizRepository.save(quiz);
		
		Quiz savedQuiz = quizRepository.save(quiz);
		
		QuizStatsDTO statsDTO = new QuizStatsDTO(quiz.getStats().getAverageScore(), quiz.getStats().getTotalAttempts());
		return new QuizDTO(savedQuiz.getId(), savedQuiz.getTitle(), savedQuiz.getDescription(), savedQuiz.getAuthor().getId(), new ArrayList<>(), statsDTO);
	}
	
	public QuizDTO editQuiz(long quizId, QuizEditDTO quizEditDTO, long userIdFromRequest) {
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException(CANNOT_FIND_QUIZ_MSG));
		
		if (!quiz.getAuthor().getId().equals(userIdFromRequest)) {
			throw new IllegalStateException("You cannot edit quiz from another author!");
		}    
		
		if (quizEditDTO.getTitle() == null || quizEditDTO.getTitle().trim().isEmpty()) {
	        throw new IllegalArgumentException("The quiz title cannot be empty!");
	    }
	    if (quizEditDTO.getDescription() == null || quizEditDTO.getDescription().trim().isEmpty()) {
	        throw new IllegalArgumentException("The quiz description cannot be empty!");
	    }
		
		quiz.setTitle(quizEditDTO.getTitle());
		quiz.setDescription(quizEditDTO.getDescription());
		
		Quiz updatedQuiz = quizRepository.save(quiz);
		
		QuizStatsDTO statsDTO = new QuizStatsDTO(quiz.getStats().getAverageScore(), quiz.getStats().getTotalAttempts());
		
		return new QuizDTO(updatedQuiz.getId(), updatedQuiz.getTitle(), updatedQuiz.getDescription(), updatedQuiz.getAuthor().getId(), new ArrayList<>(), statsDTO); 
	}
	
	public List<QuestionDTO> getAllQuestions(long requestUserId) {
		User requestUser = usersRepository.findById(requestUserId).orElseThrow(() -> new IllegalArgumentException("Cannot find a user with the given ID"));
		
		List<Question> questions;
		
		if (requestUser instanceof Teacher) {
	        questions = questionsRepository.findAll();
	    } else {
	        questions = questionsRepository.findByAuthorId(requestUserId); 
	    }
		
		List<QuestionDTO> questionsDTO = new ArrayList<>();
		for (Question question : questions) {			
			List<String> validAnswersOpenQuestion = new ArrayList<>();
			List<ClosedQuestionOptionDTO> closedQuestionOptions = new ArrayList<>();
			
			if (question.getQuestionType() == QuestionType.OPENED) {
				OpenQuestion openQuestion = (OpenQuestion)question;
				for (OpenQuestionAcceptedAnswer answer : openQuestion.getValidAnswers()) {
					validAnswersOpenQuestion.add(answer.getText());
				}
			} else if (question.getQuestionType() == QuestionType.CLOSED) {
				ClosedQuestion closedQuestion = (ClosedQuestion)question;
				for (ClosedQuestionOption option : closedQuestion.getOptions()) {
					ClosedQuestionOptionDTO optionDTO = new ClosedQuestionOptionDTO(option.getId(), option.getText(), option.isTrue());
					closedQuestionOptions.add(optionDTO);
				}
			}
			
			QuestionStats stats = question.getStats();
			QuestionStatsDTO questionStatsDTO = new QuestionStatsDTO(stats.getAverageSuccess(), stats.getTotalAnswers(), stats.getCorrectAnswer());

			QuestionDTO questionDTO = new QuestionDTO(question.getId(), question.getText(), question.getDifficulty(), question.getTopic(), question.getQuestionType(), validAnswersOpenQuestion, closedQuestionOptions, question.getAuthor().getId(), questionStatsDTO);
			questionsDTO.add(questionDTO);
		}
		return questionsDTO;
	}
	
	public List<QuestionDTO> getQuestionsByAuthorId(long authorId, long requestUserId) {
	    List<QuestionDTO> questionsDTO = this.getAllQuestions(requestUserId);
	    questionsDTO.removeIf(questionDTO -> questionDTO.getAuthorId() != authorId);
	    return questionsDTO;
	}
	
	// Refactored method to reduce Cognitive Complexity
	public QuestionDTO addQuestion(QuestionAddDTO questionAddDTO, long userIdFromRequest) {
		User user = usersRepository.findById(userIdFromRequest).orElseThrow(() -> new IllegalArgumentException("Cannot find a user with the given ID"));
		
		validateQuestionInput(questionAddDTO);
		
		Question question;
		if (questionAddDTO.getQuestionType() == QuestionType.OPENED) {
			question = createOpenQuestion(questionAddDTO, user);
		} else if (questionAddDTO.getQuestionType() == QuestionType.CLOSED) {
			question = createClosedQuestion(questionAddDTO, user);
		} else { 
			throw new IllegalArgumentException("Not supported question type."); 
		}
		
		Question savedQuestion = questionsRepository.save(question);
		
		return convertSingleQuestionToDTO(savedQuestion);
	}
	
	public QuizDTO addQuestionToQuiz (long quizId, long questionId, long userIdFromRequest) {
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException(CANNOT_FIND_QUIZ_MSG));
		
		Question question = questionsRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Cannot find a question with the given ID"));
		
		if (!quiz.getAuthor().getId().equals(userIdFromRequest)) {
			throw new IllegalStateException("You cannot edit quiz from another author!");
		}
		
		for (Question quizQuestion : quiz.getQuestions()) {
			if (quizQuestion.getId() == question.getId()) {
				throw new IllegalStateException("Question already included in the quiz!");
			}
		}
		
		quiz.addQuestion(question);
		
		quizRepository.save(quiz);
		
		QuizStatsDTO statsDTO = new QuizStatsDTO(quiz.getStats().getAverageScore(), quiz.getStats().getTotalAttempts());
		
		List<QuestionDTO> questionsDTO = convertQuestionsToDTOs(quiz.getQuestions());
		
		return new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), userIdFromRequest, questionsDTO, statsDTO); 
	}
	
	public QuizDTO removeQuestionFromQuiz(long quizId, long questionId, long userIdFromRequest) {
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException(CANNOT_FIND_QUIZ_MSG));
		QuizStatsDTO statsDTO = new QuizStatsDTO(quiz.getStats().getAverageScore(), quiz.getStats().getTotalAttempts());
		
		Question question = questionsRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Cannot find a question with the given ID"));
		
		if (!quiz.getAuthor().getId().equals(userIdFromRequest)) {
			throw new IllegalStateException("You cannot edit quiz from another author!");
		}
		
		quiz.removeQuestion(question);
		
		quizRepository.save(quiz);
		
		List<QuestionDTO> questionsDTO = convertQuestionsToDTOs(quiz.getQuestions());
		
		return new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), userIdFromRequest, questionsDTO, statsDTO);
	}
	
	public QuizDTO getQuizForStudent(long quizId, long userIdFromRequest) {
		User user = usersRepository.findById(userIdFromRequest).orElseThrow(() -> new IllegalArgumentException("Cannot find a student with the given ID"));
		if (!(user instanceof Student)) {
			throw new IllegalArgumentException("Given ID is associated to a Teacher, not a Student");
		}
		
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException(CANNOT_FIND_QUIZ_MSG));
		QuizStatsDTO statsDTO = new QuizStatsDTO(quiz.getStats().getAverageScore(), quiz.getStats().getTotalAttempts());
		
		List<QuestionDTO> fullQuestions = convertQuestionsToDTOs(quiz.getQuestions());
		
		List<QuestionDTO> studentQuestions = new ArrayList<>();
		
		for (QuestionDTO qDTO : fullQuestions) {
			if (qDTO.getQuestionType() == QuestionType.OPENED) {
	            studentQuestions.add(new QuestionDTO(qDTO.getId(), qDTO.getText(), qDTO.getDifficulty(), qDTO.getTopic(), qDTO.getQuestionType(), null, null, qDTO.getAuthorId(), qDTO.getStats()));   
	        } else {
	            List<ClosedQuestionOptionDTO> safeOptions = new ArrayList<>();
	            for (ClosedQuestionOptionDTO oDTO : qDTO.getClosedQuestionOptions()) {
	                safeOptions.add(new ClosedQuestionOptionDTO(oDTO.getId(), oDTO.getText(), false)); 
	            }
	            studentQuestions.add(new QuestionDTO(qDTO.getId(), qDTO.getText(), qDTO.getDifficulty(), qDTO.getTopic(), qDTO.getQuestionType(), null, safeOptions, qDTO.getAuthorId(), qDTO.getStats()));
	        }
		}
		
		return new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), studentQuestions, statsDTO);
	}
	
	private List<QuestionDTO> convertQuestionsToDTOs (List<Question> questions) {
		List<QuestionDTO> questionsDTO = new ArrayList<>();
		for (Question q : questions) {
			questionsDTO.add(convertSingleQuestionToDTO(q));
		}
		return questionsDTO;
	}
	
	private QuestionDTO convertSingleQuestionToDTO(Question q) {
		// FIX: Pattern Matching for instanceof
		QuestionStats stats = q.getStats();
		QuestionStatsDTO questionStatsDTO = new QuestionStatsDTO(stats.getAverageSuccess(), stats.getTotalAnswers(), stats.getCorrectAnswer());

		if (q instanceof OpenQuestion openQuestion) {
			List<String> openAnswerTextString = new ArrayList<>();
			List<OpenQuestionAcceptedAnswer> openAnswerList = openQuestion.getValidAnswers();
			for (OpenQuestionAcceptedAnswer a : openAnswerList) {
				openAnswerTextString.add(a.getText());
			}
			return new QuestionDTO(q.getId(), q.getText(), q.getDifficulty(), q.getTopic(), q.getQuestionType(), openAnswerTextString, null, q.getAuthor().getId(), questionStatsDTO);
		} else {
			List<ClosedQuestionOptionDTO> optionDTOList = new ArrayList<>();
			List<ClosedQuestionOption> optionList = ((ClosedQuestion)q).getOptions();
			for (ClosedQuestionOption o : optionList) {
				ClosedQuestionOptionDTO optionDTO = new ClosedQuestionOptionDTO(o.getId(), o.getText(), o.isTrue());
				optionDTOList.add(optionDTO);
			}
			return new QuestionDTO(q.getId(), q.getText(), q.getDifficulty(), q.getTopic(), q.getQuestionType(), null, optionDTOList, q.getAuthor().getId(), questionStatsDTO);
		}
	}
	
	// --- Private Helpers for addQuestion ---

	private void validateQuestionInput(QuestionAddDTO dto) {
		if (dto.getText() == null || dto.getText().trim().isEmpty()) {
			throw new IllegalArgumentException("The question's text cannot be empty!");
		}
		if (dto.getTopic() == null || dto.getTopic().trim().isEmpty()) {
			throw new IllegalArgumentException("The question's topic cannot be empty!");
		}
	}

	private Question createOpenQuestion(QuestionAddDTO dto, User author) {
		OpenQuestion question = new OpenQuestion(dto.getText(), dto.getTopic(), author, dto.getDifficulty());
		
		boolean hasAnswers = false;
		if (dto.getValidAnswersOpenQuestion() != null) {
			for (String text : dto.getValidAnswersOpenQuestion()) {
				if (text == null || text.trim().isEmpty()) {
					throw new IllegalArgumentException("The question's options cannot be empty!");
				}
				question.addAnswer(new OpenQuestionAcceptedAnswer(text));
				hasAnswers = true;
			}
		}
		
		if (!hasAnswers) {
			throw new IllegalArgumentException("You must enter at least one accepted correct answer!");
		}
		return question;
	}

	private Question createClosedQuestion(QuestionAddDTO dto, User author) {
		ClosedQuestion question = new ClosedQuestion(dto.getText(), dto.getTopic(), author, dto.getDifficulty());
		
		if (dto.getClosedQuestionOptions() == null || dto.getClosedQuestionOptions().isEmpty()) {
			throw new IllegalArgumentException("A closed question must have options!");
		}
		
		int optionsCount = dto.getClosedQuestionOptions().size();
		if (optionsCount < 2) {
			throw new IllegalArgumentException("A closed question must have at least 2 options!");
		}
		if (optionsCount > 4) {
			throw new IllegalArgumentException("A closed question cannot have more than 4 options!");
		}
		
		boolean hasCorrectAnswer = false;
		
		for (ClosedQuestionOptionDTO optionDTO  : dto.getClosedQuestionOptions()) {
			if (optionDTO.getText() == null || optionDTO.getText().trim().isEmpty()) {
				throw new IllegalArgumentException("The text of an option cannot be empty!");
			}
			
			question.addOption(new ClosedQuestionOption(optionDTO.getText(), optionDTO.isTrue()));
			
			if (optionDTO.isTrue()) {
				hasCorrectAnswer = true;
			}
		} 
		
		if (!hasCorrectAnswer) {
			throw new IllegalArgumentException("You must select at least one correct answer for the closed question!");
		}
		return question;
	}
}
