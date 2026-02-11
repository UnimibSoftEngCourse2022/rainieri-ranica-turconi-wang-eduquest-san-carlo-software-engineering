package it.bicocca.eduquest.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import it.bicocca.eduquest.domain.answers.QuizAttempt;
import it.bicocca.eduquest.domain.answers.QuizAttemptStatus;
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
import it.bicocca.eduquest.repository.QuizAttemptsRepository; 
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.UsersRepository;
import org.springframework.web.multipart.MultipartFile;
import it.bicocca.eduquest.domain.multimedia.*;
import it.bicocca.eduquest.dto.multimedia.*;
import it.bicocca.eduquest.repository.MultimediaRepository;

@Service
public class QuizServices {
	
	private static final String CANNOT_FIND_QUIZ_MSG = "Cannot find a quiz with the given ID";
	
	private final QuizRepository quizRepository;
	private final UsersRepository usersRepository;
	private final QuestionsRepository questionsRepository;
	private final MultimediaService multimediaService;
	private final MultimediaRepository multimediaRepository;
	private final QuizAttemptsRepository quizAttemptsRepository;
	
	public QuizServices(QuizRepository quizRepository, UsersRepository usersRepository, 
			QuestionsRepository questionsRepository, MultimediaService multimediaService,
			MultimediaRepository multimediaRepository, QuizAttemptsRepository quizAttemptsRepository) { 
		this.quizRepository = quizRepository;
		this.usersRepository = usersRepository;
		this.questionsRepository = questionsRepository;
		this.multimediaService = multimediaService;
		this.multimediaRepository = multimediaRepository;
		this.quizAttemptsRepository = quizAttemptsRepository;
	}
	
	public QuizDTO getQuizById(long id) {
		Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cannot find quiz with ID " + id));
		
		QuizStatsDTO statsDTO = calculateQuizOnlyStats(quiz);
		
		List<QuestionDTO> questionsDTO = new ArrayList<>();
		
		for (Question question : quiz.getQuestions()) {
			QuestionDTO questionDTO = convertSingleQuestionToDTO(question);
			questionsDTO.add(questionDTO);
		}

		return new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), questionsDTO, statsDTO, quiz.getDifficulty(), quiz.isPublic());
	}
	
	public List<QuizDTO> getAllQuizzes() {
		List<Quiz> quizzes = quizRepository.findAll();
		return convertQuizzesToDTOs(quizzes);
	}
	
	public List<QuizDTO> getQuizzesByAuthorId(long authorId) {
		List<Quiz> quizzes = quizRepository.findByAuthorId(authorId);
		
		return convertQuizzesToDTOs(quizzes);
	}
	
	public List<QuizDTO> getAllPublicQuizzes() {
		List<Quiz> publicQuizzes = quizRepository.findByIsPublicTrue();
		
		return convertQuizzesToDTOs(publicQuizzes);
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
		quiz.setPublic(quizAddDTO.isPublic());
		Quiz savedQuiz = quizRepository.save(quiz);
		
		QuizStatsDTO statsDTO = new QuizStatsDTO(0.0, 0, new HashMap<>()); // Quiz nuovo = 0 stats
		return new QuizDTO(savedQuiz.getId(), savedQuiz.getTitle(), savedQuiz.getDescription(), savedQuiz.getAuthor().getId(), new ArrayList<>(), statsDTO, quiz.getDifficulty(), savedQuiz.isPublic());
	}
	
	@Transactional
	public QuizDTO editQuiz(long quizId, QuizEditDTO quizEditDTO, long userIdFromRequest) {
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException(CANNOT_FIND_QUIZ_MSG));
		
		if (quiz.isPublic() && !quizEditDTO.isPublic()) {
            List<QuizAttempt> attempts = quizAttemptsRepository.findByQuiz(quiz);
            if (!attempts.isEmpty()) {
            	for (QuizAttempt qa : attempts) {
            		if (qa.getStatus() == QuizAttemptStatus.STARTED) {
            			quizAttemptsRepository.delete(qa);
            		}
            	}
            }
        }
		
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
		quiz.setPublic(quizEditDTO.isPublic());
		
		Quiz updatedQuiz = quizRepository.save(quiz);
		
		QuizStatsDTO statsDTO = calculateQuizOnlyStats(updatedQuiz);
		
		return new QuizDTO(updatedQuiz.getId(), updatedQuiz.getTitle(), updatedQuiz.getDescription(), updatedQuiz.getAuthor().getId(), new ArrayList<>(), statsDTO, quiz.getDifficulty(), updatedQuiz.isPublic()); 
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
	
	public QuestionDTO addQuestion(QuestionAddDTO questionAddDTO, long userIdFromRequest, MultipartFile file) {
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
		
		String url = null;
		MultimediaSupport savedMedia = null;
		
		if (file != null && !file.isEmpty()) {
		    if (questionAddDTO.getMultimediaType() == MultimediaType.AUDIO) {
		    	url = multimediaService.uploadMedia(file, "eduquest_audios");
		    	AudioSupport audioSupport = new AudioSupport();
		    	audioSupport.setUrl(url);
		    	question.setMultimedia(audioSupport);
		    	savedMedia = multimediaRepository.save(audioSupport);
		    } else if (questionAddDTO.getMultimediaType() == MultimediaType.IMAGE) {
		    	url = multimediaService.uploadMedia(file, "eduquest_images");
		    	ImageSupport imageSupport = new ImageSupport();
		    	imageSupport.setUrl(url);
		    	question.setMultimedia(imageSupport);
		    	savedMedia = multimediaRepository.save(imageSupport);
		    } else if (questionAddDTO.getMultimediaType() == MultimediaType.VIDEO) {
		    	url = multimediaService.uploadMedia(file, "eduquest_videos");
		    	VideoSupport videoSupport = new VideoSupport();
		    	videoSupport.setIsYoutube(false);
		    	videoSupport.setUrl(url);
		    	question.setMultimedia(videoSupport);
		    	savedMedia = multimediaRepository.save(videoSupport);
		    } else {
		    	throw new IllegalArgumentException("Not supported multimedia type.");
		    }
		} else if (questionAddDTO.getMultimediaType() == MultimediaType.VIDEO && questionAddDTO.getMultimediaUrl() != null) {
			VideoSupport videoSupport = new VideoSupport();
			videoSupport.setIsYoutube(true);
			videoSupport.setUrl(questionAddDTO.getMultimediaUrl());
			question.setMultimedia(videoSupport);
			savedMedia = multimediaRepository.save(videoSupport);
		}
		
		if (savedMedia != null) {
	        question.setMultimedia(savedMedia);
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
		quiz.recalculateDifficulty();
		quizRepository.save(quiz);
		
		QuizStatsDTO statsDTO = calculateQuizOnlyStats(quiz);
		
		List<QuestionDTO> questionsDTO = convertQuestionsToDTOs(quiz.getQuestions());
		
		return new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), userIdFromRequest, questionsDTO, statsDTO, quiz.getDifficulty(), quiz.isPublic()); 
	}
	
	public QuizDTO removeQuestionFromQuiz(long quizId, long questionId, long userIdFromRequest) {
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException(CANNOT_FIND_QUIZ_MSG));
		
		Question question = questionsRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Cannot find a question with the given ID"));
		
		if (!quiz.getAuthor().getId().equals(userIdFromRequest)) {
			throw new IllegalStateException("You cannot edit quiz from another author!");
		}
		
		quiz.removeQuestion(question);
		quiz.recalculateDifficulty();
		quizRepository.save(quiz);
		
		QuizStatsDTO statsDTO = calculateQuizOnlyStats(quiz);
		
		List<QuestionDTO> questionsDTO = convertQuestionsToDTOs(quiz.getQuestions());
		
		return new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), userIdFromRequest, questionsDTO, statsDTO, quiz.getDifficulty(), quiz.isPublic());
	}
	
	public QuizDTO getQuizForStudent(long quizId, long userIdFromRequest) {
		User user = usersRepository.findById(userIdFromRequest).orElseThrow(() -> new IllegalArgumentException("Cannot find a student with the given ID"));
		if (!(user instanceof Student)) {
			throw new IllegalArgumentException("Given ID is associated to a Teacher, not a Student");
		}
		
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException(CANNOT_FIND_QUIZ_MSG));
		
		QuizStatsDTO statsDTO = calculateQuizOnlyStats(quiz);
		
		List<QuestionDTO> fullQuestions = convertQuestionsToDTOs(quiz.getQuestions());
		List<QuestionDTO> studentQuestions = new ArrayList<>();
		
		for (QuestionDTO qDTO : fullQuestions) {
			QuestionDTO sanitizedQuestion;
			if (qDTO.getQuestionType() == QuestionType.OPENED) {
				sanitizedQuestion = new QuestionDTO(qDTO.getId(), qDTO.getText(), qDTO.getDifficulty(), qDTO.getTopic(), qDTO.getQuestionType(), null, null, qDTO.getAuthorId(), qDTO.getStats());   
	        } else {
	            List<ClosedQuestionOptionDTO> safeOptions = new ArrayList<>();
	            for (ClosedQuestionOptionDTO oDTO : qDTO.getClosedQuestionOptions()) {
	                safeOptions.add(new ClosedQuestionOptionDTO(oDTO.getId(), oDTO.getText(), false)); 
	            }
	            sanitizedQuestion = new QuestionDTO(qDTO.getId(), qDTO.getText(), qDTO.getDifficulty(), qDTO.getTopic(), qDTO.getQuestionType(), null, safeOptions, qDTO.getAuthorId(), qDTO.getStats());
	        }
			
			if (qDTO.getMultimedia() != null) {
				sanitizedQuestion.setMultimedia(qDTO.getMultimedia());
			}
			
			studentQuestions.add(sanitizedQuestion);
		}
		
		return new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), studentQuestions, statsDTO, quiz.getDifficulty(), quiz.isPublic());
	}
	
	private List<QuestionDTO> convertQuestionsToDTOs (List<Question> questions) {
		List<QuestionDTO> questionsDTO = new ArrayList<>();
		for (Question q : questions) {
			questionsDTO.add(convertSingleQuestionToDTO(q));
		}
		return questionsDTO;
	}
	
	private QuestionDTO convertSingleQuestionToDTO(Question q) {
		QuestionStats stats = q.getStats();
		QuestionStatsDTO questionStatsDTO = new QuestionStatsDTO(stats.getAverageSuccess(), stats.getTotalAnswers(), stats.getCorrectAnswer());
		
		QuestionDTO questionDTO;
		
		if (q instanceof OpenQuestion openQuestion) {
			List<String> openAnswerTextString = new ArrayList<>();
			List<OpenQuestionAcceptedAnswer> openAnswerList = openQuestion.getValidAnswers();
			for (OpenQuestionAcceptedAnswer a : openAnswerList) {
				openAnswerTextString.add(a.getText());
			}
			questionDTO = new QuestionDTO(q.getId(), q.getText(), q.getDifficulty(), q.getTopic(), q.getQuestionType(), openAnswerTextString, null, q.getAuthor().getId(), questionStatsDTO);
		} else {
			List<ClosedQuestionOptionDTO> optionDTOList = new ArrayList<>();
			List<ClosedQuestionOption> optionList = ((ClosedQuestion)q).getOptions();
			for (ClosedQuestionOption o : optionList) {
				ClosedQuestionOptionDTO optionDTO = new ClosedQuestionOptionDTO(o.getId(), o.getText(), o.isTrue());
				optionDTOList.add(optionDTO);
			}
			questionDTO = new QuestionDTO(q.getId(), q.getText(), q.getDifficulty(), q.getTopic(), q.getQuestionType(), null, optionDTOList, q.getAuthor().getId(), questionStatsDTO);
		}
		
		if (q.getMultimedia() != null) {
	        MultimediaSupport media = q.getMultimedia();
	        MultimediaDTO mediaDTO = new MultimediaDTO();
	        mediaDTO.setUrl(media.getUrl());
	        mediaDTO.setType(media.getType());
	        
	        if (media instanceof VideoSupport videoSupport) {
	            mediaDTO.setIsYoutube(videoSupport.getIsYoutube());
	        } else {
	            mediaDTO.setIsYoutube(false);
	        }

	        questionDTO.setMultimedia(mediaDTO);
	    }
	    return questionDTO;
	}
	
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
	
	private List<QuizDTO> convertQuizzesToDTOs(List<Quiz> quizzes) {
		List<QuizDTO> quizzesDTO = new ArrayList<>();
		
		for (Quiz quiz : quizzes) {
			List<QuestionDTO> questionsDTO = convertQuestionsToDTOs(quiz.getQuestions());
			QuizStatsDTO statsDTO = calculateQuizOnlyStats(quiz);
			QuizDTO quizDTO = new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), questionsDTO, statsDTO, quiz.getDifficulty(), quiz.isPublic());
			quizzesDTO.add(quizDTO);
		}
		
		return quizzesDTO;
    }
	
	private QuizStatsDTO calculateQuizOnlyStats(Quiz quiz) {
		Double avg = quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(quiz);
		long count = quizAttemptsRepository.countByQuizAndTestIsNull(quiz);
		Map<Long, QuestionStats> statsPerQuestion = quiz.getStats().getStatsPerQuestion();
		
		Map<Long, QuestionStatsDTO> statsPerQuestionDTO = new HashMap<>();
		for (Map.Entry<Long, QuestionStats> entry : statsPerQuestion.entrySet()) {
			QuestionStats questionStats = entry.getValue();
			QuestionStatsDTO questionStatsDTO = new QuestionStatsDTO(questionStats.getAverageSuccess(), questionStats.getTotalAnswers(), questionStats.getCorrectAnswer());
			statsPerQuestionDTO.put(entry.getKey(), questionStatsDTO);
		}
		
		return new QuizStatsDTO(avg, (int) count, statsPerQuestionDTO);
	}
}