package it.bicocca.eduquest.repository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import it.bicocca.eduquest.domain.gamification.*;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
	@Query("SELECT c FROM Challenge c " + "WHERE (c.challenger.id = :userId OR c.opponent.id = :userId) " +
	       "AND c.quiz.id = :quizId " + "AND c.status = 'ACTIVE'")
	Optional<Challenge> findActiveChallengeForUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);
	
	// Find challenges that are still ACTIVE but whose expiry date has passed
	@Query("SELECT c FROM Challenge c WHERE c.status = 'ACTIVE' AND c.expiresAt < :now")
	List<Challenge> findExpiredActiveChallenges(@Param("now") LocalDateTime now);
}
