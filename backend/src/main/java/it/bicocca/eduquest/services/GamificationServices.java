package it.bicocca.eduquest.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.bicocca.eduquest.domain.gamification.Mission;
import it.bicocca.eduquest.domain.gamification.MissionProgress;
import it.bicocca.eduquest.dto.gamification.*;
import it.bicocca.eduquest.repository.MissionsProgressesRepository;

@Service
public class GamificationServices {
	private MissionsProgressesRepository missionsProgressesRepository;
	
	public GamificationServices(MissionsProgressesRepository missionsProgressesRepository) {
		this.missionsProgressesRepository = missionsProgressesRepository;
	}
	
	public List<MissionProgressDTO> getAllMissionsProgressesByUserId(long userId) {
		List<MissionProgressDTO> missionsProgresses = new ArrayList<MissionProgressDTO>();
		
		for (MissionProgress progress : missionsProgressesRepository.findByStudentId(userId)) {
			Mission mission = progress.getMission();
			MissionDTO missionDTO = new MissionDTO(mission.getId(), mission.getTitle(), mission.getDescription(), mission.getGoal());
			MissionProgressDTO progressDTO = new MissionProgressDTO(progress.getId(), progress.getCurrentCount(), progress.getGoal(), progress.isCompleted(), missionDTO, progress.getStudent().getId());
			missionsProgresses.add(progressDTO);
		}
		
		return missionsProgresses;
	}
}
