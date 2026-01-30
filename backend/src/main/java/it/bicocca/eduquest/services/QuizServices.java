package it.bicocca.eduquest.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.bicocca.eduquest.domain.quiz.ClosedQuestion;
import it.bicocca.eduquest.domain.quiz.ClosedQuestionOption;
import it.bicocca.eduquest.domain.quiz.OpenQuestion;
import it.bicocca.eduquest.domain.quiz.OpenQuestionAcceptedAnswer;
import it.bicocca.eduquest.domain.quiz.Question;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.users.Teacher;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.dto.quiz.ClosedQuestionOptionDTO;
import it.bicocca.eduquest.dto.quiz.QuestionAddDTO;
import it.bicocca.eduquest.dto.quiz.QuestionDTO;
import it.bicocca.eduquest.dto.quiz.QuestionType;
import it.bicocca.eduquest.dto.quiz.QuizAddDTO;
import it.bicocca.eduquest.dto.quiz.QuizDTO;
import it.bicocca.eduquest.dto.quiz.QuizEditDTO;
import it.bicocca.eduquest.repository.QuestionsRepository;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.UsersRepository;

@Service
public class QuizServices {
	private final QuizRepository quizRepository;
	private final UsersRepository usersRepository;
	private final QuestionsRepository questionsRepository;
	
	public QuizServices(QuizRepository quizRepository, UsersRepository usersRepository, QuestionsRepository questionsRepository) {
		this.quizRepository = quizRepository;
		this.usersRepository = usersRepository;
		this.questionsRepository = questionsRepository;
	}
	
	public QuizDTO getQuizById(long id) {
		Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find quiz with ID " + id));
		List<QuestionDTO> questionsDTO = new ArrayList<QuestionDTO>();

		for (Question question : quiz.getQuestions()) {
			QuestionDTO questionDTO = new QuestionDTO(question.getId(), question.getText(), question.getDifficulty(), question.getTopic(), QuestionType.OPENED, new ArrayList<String>(), new ArrayList<ClosedQuestionOptionDTO>(), question.getAuthor().getId());	
			questionsDTO.add(questionDTO);
		}

		QuizDTO quizDTO = new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), questionsDTO);
		return quizDTO;
	}
	
	public List<QuizDTO> getAllQuizzes() {
		List<Quiz> quizzes = quizRepository.findAll();
		
		List<QuizDTO> quizzesDTO = new ArrayList<QuizDTO>();
		for (Quiz quiz : quizzes) {
			List<QuestionDTO> questionsDTO = convertQuestionsToDTOs(quiz.getQuestions());
			QuizDTO quizDTO = new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), questionsDTO);
			quizzesDTO.add(quizDTO);
		}
		
		return quizzesDTO;
	}
	
	public List<QuizDTO> getQuizzesByAuthorId(long authorId) {
		List<Quiz> quizzes = quizRepository.findAll();
		
		List<QuizDTO> quizzesDTO = new ArrayList<QuizDTO>();
		for (Quiz quiz : quizzes) {
			if (quiz.getAuthor().getId() != authorId) {
				continue;
			}
			List<QuestionDTO> questionsDTO = convertQuestionsToDTOs(quiz.getQuestions());
			QuizDTO quizDTO = new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), questionsDTO);
			quizzesDTO.add(quizDTO);
		}
		
		return quizzesDTO;
	}
	
	public QuizDTO addQuiz(QuizAddDTO quizAddDTO, long userIdFromRequest) {
		User user = usersRepository.findById(userIdFromRequest).orElseThrow(() -> new RuntimeException("Cannot find a teacher with the given ID"));
		if (!(user instanceof Teacher)) {
			throw new RuntimeException("Given ID is associated to a Student, not a Teacher");
		}
		
		if (quizAddDTO.getTitle() == null || quizAddDTO.getTitle().trim().isEmpty()) {
			throw new RuntimeException("The quiz title cannot be empty!");
		}
		if (quizAddDTO.getDescription() == null || quizAddDTO.getDescription().trim().isEmpty()) {
			throw new RuntimeException("The quiz description cannot be empty!");
		}
		
		Teacher author = (Teacher) user;
		
		Quiz quiz = new Quiz(quizAddDTO.getTitle(), quizAddDTO.getDescription(), author);
		quizRepository.save(quiz);
		
		Quiz savedQuiz = quizRepository.save(quiz);
		QuizDTO quizDTO = new QuizDTO(savedQuiz.getId(), savedQuiz.getTitle(), savedQuiz.getDescription(), savedQuiz.getAuthor().getId(), new ArrayList<QuestionDTO>());
		
		return quizDTO;
	}
	
	public QuizDTO editQuiz(long quizId, QuizEditDTO quizEditDTO, long userIdFromRequest) {
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Cannot find a quiz with the given ID"));
		
		if (!quiz.getAuthor().getId().equals(userIdFromRequest)) {
			throw new RuntimeException("You cannot edit quiz from another author!");
		}    
		
		if (quizEditDTO.getTitle() == null || quizEditDTO.getTitle().trim().isEmpty()) {
	        throw new RuntimeException("The quiz title cannot be empty!");
	    }
	    if (quizEditDTO.getDescription() == null || quizEditDTO.getDescription().trim().isEmpty()) {
	        throw new RuntimeException("The quiz description cannot be empty!");
	    }
		
		quiz.setTitle(quizEditDTO.getTitle());
		quiz.setDescription(quizEditDTO.getDescription());
		
		Quiz updatedQuiz = quizRepository.save(quiz);
		
		QuizDTO quizDTO = new QuizDTO(updatedQuiz.getId(), updatedQuiz.getTitle(), updatedQuiz.getDescription(), updatedQuiz.getAuthor().getId(), new ArrayList<QuestionDTO>());
		
		return quizDTO; 
	}
	
	public List<QuestionDTO> getAllQuestions(long requestUserId) {
		User requestUser = usersRepository.findById(requestUserId).orElseThrow(() -> new RuntimeException("Cannot find a user with the given ID"));
		
		List<Question> questions = questionsRepository.findAll();
		List<QuestionDTO> questionsDTO = new ArrayList<QuestionDTO>();
		for (Question question : questions) {
			if (!(requestUser instanceof Teacher) && !requestUser.getId().equals(question.getAuthor().getId())) {
				continue;
			}
			
			List<String> validAnswersOpenQuestion = new ArrayList<String>();
			List<ClosedQuestionOptionDTO> closedQuestionOptions = new ArrayList<ClosedQuestionOptionDTO>();
			
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
			
			QuestionDTO questionDTO = new QuestionDTO(question.getId(), question.getText(), question.getDifficulty(), question.getTopic(), question.getQuestionType(), validAnswersOpenQuestion, closedQuestionOptions, question.getAuthor().getId());
			questionsDTO.add(questionDTO);
		}
		return questionsDTO;
	}
	
	public List<QuestionDTO> getQuestionsByAuthorId(long authorId, long requestUserId) {
	    List<QuestionDTO> questionsDTO = this.getAllQuestions(requestUserId);
	    questionsDTO.removeIf(questionDTO -> questionDTO.getAuthorId() != authorId);
	    return questionsDTO;
	}
	
	public QuestionDTO addQuestion(QuestionAddDTO questionAddDTO, long userIdFromRequest) {
		User user = usersRepository.findById(userIdFromRequest).orElseThrow(() -> new RuntimeException("Cannot find a user with the given ID"));
		
		if (questionAddDTO.getText() == null || questionAddDTO.getText().trim().isEmpty()) {
			throw new RuntimeException("The question's text cannot be empty!");
		}
		if (questionAddDTO.getTopic() == null || questionAddDTO.getTopic().trim().isEmpty()) {
			throw new RuntimeException("The question's topic cannot be empty!");
		}
		
		User author = user;
		Question question;
		QuestionDTO questionDTO;
		OpenQuestionAcceptedAnswer openQuestionAnswer;
		ClosedQuestionOption closedQuestionOption;
		List<String> openQuestionAnswerList = new ArrayList<String>();
		List<ClosedQuestionOptionDTO> closedQuestionOptionList = new ArrayList<ClosedQuestionOptionDTO>();
		
		if (questionAddDTO.getQuestionType() == QuestionType.OPENED) {
			question = new OpenQuestion(questionAddDTO.getText(), questionAddDTO.getTopic(), author, questionAddDTO.getDifficulty());
			if (questionAddDTO.getValidAnswersOpenQuestion() != null) {
				for (String text : questionAddDTO.getValidAnswersOpenQuestion()) {
					if (text == null || text.trim().isEmpty()) {
						throw new RuntimeException("The question's options cannot be empty!");
					}
					openQuestionAnswer = new OpenQuestionAcceptedAnswer(text);
					((OpenQuestion) question).addAnswer(openQuestionAnswer);
					openQuestionAnswerList.add(text);
				}
			}
			if (openQuestionAnswerList.isEmpty()) {
            throw new RuntimeException("You must enter at least one accepted correct answer!");
			} 
		} else if (questionAddDTO.getQuestionType() == QuestionType.CLOSED) {
			question = new ClosedQuestion(questionAddDTO.getText(), questionAddDTO.getTopic(), author, questionAddDTO.getDifficulty());
			if (questionAddDTO.getClosedQuestionOptions() == null || questionAddDTO.getClosedQuestionOptions().isEmpty()) {
				throw new RuntimeException("A closed question must have options!");
			}
			
			boolean hasCorrectAnswer = false;
			
			for (ClosedQuestionOptionDTO closedQuestionOptionDTO  : questionAddDTO.getClosedQuestionOptions()) {
				if (closedQuestionOptionDTO.getText() == null || closedQuestionOptionDTO.getText().trim().isEmpty()) {
					throw new RuntimeException("The text of an option cannot be empty!");
				}
				
				closedQuestionOption = new ClosedQuestionOption(closedQuestionOptionDTO.getText(), closedQuestionOptionDTO.isTrue());
				((ClosedQuestion) question).addOption(closedQuestionOption);
				closedQuestionOptionList.add(closedQuestionOptionDTO);
				
				if (closedQuestionOptionDTO.isTrue()) {
					hasCorrectAnswer = true;
				}
			} 
			
			if (!hasCorrectAnswer) {
				throw new RuntimeException("You must select at least one correct answer for the closed question!");
			}
			
		} else { 
			throw new IllegalArgumentException("Not supported question type."); 
		}
		
		questionsRepository.save(question);
		
		if (question instanceof OpenQuestion) {
			questionDTO = new QuestionDTO(question.getId(), question.getText(), question.getDifficulty(), question.getTopic(), question.getQuestionType(), openQuestionAnswerList, null, question.getAuthor().getId());
		} else {
			questionDTO = new QuestionDTO(question.getId(), question.getText(), question.getDifficulty(), question.getTopic(), question.getQuestionType(), null, closedQuestionOptionList, question.getAuthor().getId());
		}
		
		return questionDTO;
	}
	
	public QuizDTO addQuestionToQuiz (long quizId, long questionId, long userIdFromRequest) {
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Cannot find a quiz with the given ID"));
		
		Question question = questionsRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Cannot find a question with the given ID"));
		
		if (!quiz.getAuthor().getId().equals(userIdFromRequest)) {
			throw new RuntimeException("You cannot edit quiz from another author!");
		}
		
		for (Question quizQuestion : quiz.getQuestions()) {
			if (quizQuestion.getId() == question.getId()) {
				throw new RuntimeException("Question already included in the quiz!");
			}
		}
		
		quiz.addQuestion(question);
		
		quizRepository.save(quiz);
		
		List<QuestionDTO> questionsDTO = convertQuestionsToDTOs(quiz.getQuestions());
		
		QuizDTO quizDTO = new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), userIdFromRequest, questionsDTO);
		
		return quizDTO; 
	}
	
	public QuizDTO removeQuestionFromQuiz(long quizId, long questionId, long userIdFromRequest) {
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Cannot find a quiz with the given ID"));
		
		Question question = questionsRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Cannot find a question with the given ID"));
		
		if (!quiz.getAuthor().getId().equals(userIdFromRequest)) {
			throw new RuntimeException("You cannot edit quiz from another author!");
		}
		
		quiz.removeQuestion(question);
		
		quizRepository.save(quiz);
		
		List<QuestionDTO> questionsDTO = convertQuestionsToDTOs(quiz.getQuestions());
		
		QuizDTO quizDTO = new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), userIdFromRequest, questionsDTO);
		
		return quizDTO;
	}
	
	public QuizDTO getQuizForStudent(long quizId, long userIdFromRequest) {
		User user = usersRepository.findById(userIdFromRequest).orElseThrow(() -> new RuntimeException("Cannot find a student with the given ID"));
		if (!(user instanceof Student)) {
			throw new RuntimeException("Given ID is associated to a Teacher, not a Student");
		}
		
		Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Cannot find a quiz with the given ID"));
		
		List<QuestionDTO> fullQuestions = convertQuestionsToDTOs(quiz.getQuestions());
		
		List<QuestionDTO> studentQuestions = new ArrayList<>();
		
		for (QuestionDTO qDTO : fullQuestions) {
			if (qDTO.getQuestionType() == QuestionType.OPENED) {
	            studentQuestions.add(new QuestionDTO(qDTO.getId(), qDTO.getText(), qDTO.getDifficulty(), qDTO.getTopic(), qDTO.getQuestionType(), null, null, qDTO.getAuthorId()));   
	        } else {
	            List<ClosedQuestionOptionDTO> safeOptions = new ArrayList<>();
	            for (ClosedQuestionOptionDTO oDTO : qDTO.getClosedQuestionOptions()) {
	                safeOptions.add(new ClosedQuestionOptionDTO(oDTO.getId(), oDTO.getText(), false)); 
	            }
	            studentQuestions.add(new QuestionDTO(qDTO.getId(), qDTO.getText(), qDTO.getDifficulty(), qDTO.getTopic(), qDTO.getQuestionType(), null, safeOptions, qDTO.getAuthorId()));
	        }
		}
		
		QuizDTO quizDTO = new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getAuthor().getId(), studentQuestions);
		
		return quizDTO;
	}
	
	private List<QuestionDTO> convertQuestionsToDTOs (List<Question> questions) {
		List<QuestionDTO> questionsDTO = new ArrayList<QuestionDTO>();
		QuestionDTO qDTO;
		for (Question q : questions) {
			if (q instanceof OpenQuestion) {
				List<String> openAnswerTextString = new ArrayList<String>();
				List<OpenQuestionAcceptedAnswer> openAnswerList = ((OpenQuestion)q).getValidAnswers();
				for (OpenQuestionAcceptedAnswer a : openAnswerList) {
					openAnswerTextString.add(a.getText());
				}
				qDTO = new QuestionDTO(q.getId(), q.getText(), q.getDifficulty(), q.getTopic(), q.getQuestionType(), openAnswerTextString, null, q.getAuthor().getId());
			} else {
				List<ClosedQuestionOptionDTO> optionDTOList = new ArrayList<ClosedQuestionOptionDTO>();
				List<ClosedQuestionOption> optionList = ((ClosedQuestion)q).getOptions();
				for (ClosedQuestionOption o : optionList) {
					ClosedQuestionOptionDTO optionDTO = new ClosedQuestionOptionDTO(o.getId(), o.getText(), o.isTrue());
					optionDTOList.add(optionDTO);
				}
				qDTO = new QuestionDTO(q.getId(), q.getText(), q.getDifficulty(), q.getTopic(), q.getQuestionType(), null, optionDTOList, q.getAuthor().getId());
			}
			questionsDTO.add(qDTO);
		}
		return questionsDTO;
	}
	
}
