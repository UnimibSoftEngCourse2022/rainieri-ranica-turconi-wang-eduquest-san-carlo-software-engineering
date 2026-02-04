package it.bicocca.eduquest.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import it.bicocca.eduquest.domain.gamification.*;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
	@Query("SELECT c FROM Challenge c " + "WHERE (c.challenger.id = :userId OR c.opponent.id = :userId) " +
	       "AND c.quiz.id = :quizId " + "AND c.status = 'ACTIVE'")
	Optional<Challenge> findActiveChallengeForUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);
}
