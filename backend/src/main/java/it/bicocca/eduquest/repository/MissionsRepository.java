package it.bicocca.eduquest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.bicocca.eduquest.domain.answers.Answer;
import it.bicocca.eduquest.domain.answers.QuizAttempt;
import it.bicocca.eduquest.domain.gamification.Mission;

public interface MissionsRepository extends JpaRepository<Mission, Long> {
	
}
