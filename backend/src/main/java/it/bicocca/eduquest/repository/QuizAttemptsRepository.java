package it.bicocca.eduquest.repository;

import it.bicocca.eduquest.domain.answers.*;
import it.bicocca.eduquest.domain.users.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.bicocca.eduquest.domain.quiz.Quiz;
import java.util.Optional;

@Repository
public interface QuizAttemptsRepository extends JpaRepository<QuizAttempt, Long> {
	// Check whether there is already a STARTED attempt for this student and this quiz
	Optional<QuizAttempt> findByStudentAndQuizAndStatus(User student, Quiz quiz, QuizAttemptStatus status);
}
