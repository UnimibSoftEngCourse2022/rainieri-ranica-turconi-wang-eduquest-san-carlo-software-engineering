package it.bicocca.eduquest.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.bicocca.eduquest.domain.answers.QuizAttempt;
import it.bicocca.eduquest.domain.answers.QuizAttemptStatus;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.quiz.Test;
import it.bicocca.eduquest.domain.users.User;

@Repository
public interface QuizAttemptsRepository extends JpaRepository<QuizAttempt, Long> {
	
	Optional<QuizAttempt> findByStudentAndQuizAndStatus(User student, Quiz quiz, QuizAttemptStatus status);
	List<QuizAttempt> findByStudentId(Long studentId);
	
	long countByStudentAndTest(User student, Test test);

    long countByTest(Test test);
    
    @Query("SELECT COALESCE(AVG(qa.score), 0.0) FROM QuizAttempt qa WHERE qa.test = :test")
    Double getAverageScoreByTest(@Param("test") Test test);

    long countByQuizAndTestIsNull(Quiz quiz);

    @Query("SELECT COALESCE(AVG(qa.score), 0.0) FROM QuizAttempt qa WHERE qa.quiz = :quiz AND qa.test IS NULL")
    Double getAverageScoreByQuizAndTestIsNull(@Param("quiz") Quiz quiz);
}