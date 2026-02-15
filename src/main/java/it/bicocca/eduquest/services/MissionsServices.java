package it.bicocca.eduquest.services;

import it.bicocca.eduquest.repository.MissionsRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.bicocca.eduquest.domain.gamification.Mission;
import it.bicocca.eduquest.dto.gamification.*;

@Service
public class MissionsServices {
	private final MissionsRepository missionsRepository;
	
	public MissionsServices(MissionsRepository missionsRepository) {
		this.missionsRepository = missionsRepository;
	}
	
	public List<MissionDTO> getAllMissions() {
		List<MissionDTO> missionsDTO = new ArrayList<>();
		for (Mission mission : missionsRepository.findAll()) {
			MissionDTO missionDTO = new MissionDTO(mission.getId(), mission.getTitle(), mission.getDescription(), mission.getGoal());
			missionsDTO.add(missionDTO);
		}
		return missionsDTO;
	}
}
