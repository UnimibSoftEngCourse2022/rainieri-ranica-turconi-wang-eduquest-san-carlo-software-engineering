package it.bicocca.eduquest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.bicocca.eduquest.domain.gamification.MissionProgress;

public interface MissionsProgressesRepository extends JpaRepository<MissionProgress, Long> {
	List<MissionProgress> findByMissionId(long authorId);
	List<MissionProgress> findByStudentId(long studentId);
	List<MissionProgress> findByMissionIdAndStudentId(long missionId, long studentId);
	void deleteByStudentId(long studentId);
}
