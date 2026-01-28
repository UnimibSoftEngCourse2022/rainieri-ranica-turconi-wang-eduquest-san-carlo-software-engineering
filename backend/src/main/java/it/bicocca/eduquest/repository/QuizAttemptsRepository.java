package it.bicocca.eduquest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.bicocca.eduquest.domain.answers.QuizAttempt;
import it.bicocca.eduquest.domain.answers.QuizAttemptStatus;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.users.User;

@Repository
public interface QuizAttemptsRepository extends JpaRepository<QuizAttempt, Long> {
	// Check whether there is already a STARTED attempt for this student and this quiz
	Optional<QuizAttempt> findByStudentAndQuizAndStatus(User student, Quiz quiz, QuizAttemptStatus status);
}
