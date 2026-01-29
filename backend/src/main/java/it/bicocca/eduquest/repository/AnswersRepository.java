package it.bicocca.eduquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.bicocca.eduquest.domain.answers.*;
import it.bicocca.eduquest.domain.quiz.*;
import java.util.Optional;

@Repository
public interface AnswersRepository extends JpaRepository<Answer, Long> {
	Optional<Answer> findByQuizAttemptAndQuestion(QuizAttempt quizAttempt, Question question);
}
