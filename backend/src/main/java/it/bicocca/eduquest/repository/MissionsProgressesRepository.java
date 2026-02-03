package it.bicocca.eduquest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.bicocca.eduquest.domain.gamification.MissionProgress;
import it.bicocca.eduquest.domain.quiz.Question;

public interface MissionsProgressesRepository extends JpaRepository<MissionProgress, Long> {
	List<MissionProgress> findByMissionId(long authorId);
	List<MissionProgress> findByStudentId(long studentId);
}
