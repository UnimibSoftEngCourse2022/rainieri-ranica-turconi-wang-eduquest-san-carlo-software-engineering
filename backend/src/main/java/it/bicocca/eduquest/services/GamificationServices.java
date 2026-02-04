package it.bicocca.eduquest.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import it.bicocca.eduquest.domain.answers.QuizAttempt;
import it.bicocca.eduquest.domain.gamification.Mission;
import it.bicocca.eduquest.domain.gamification.MissionProgress;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.dto.gamification.*;
import it.bicocca.eduquest.repository.MissionsProgressesRepository;
import it.bicocca.eduquest.repository.MissionsRepository;

@Service
public class GamificationServices {
	private MissionsProgressesRepository missionsProgressesRepository;
	private MissionsRepository missionsRepository;
	
	public GamificationServices(MissionsProgressesRepository missionsProgressesRepository, MissionsRepository missionsRepository) {
		this.missionsProgressesRepository = missionsProgressesRepository;
		this.missionsRepository = missionsRepository;
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
	
	public void updateMissionsProgresses(QuizAttempt quizAttempt) {
        Student student = ((Student)Hibernate.unproxy(quizAttempt.getStudent()));
		this.fillMissingMissionProgress(student);
        
        for (MissionProgress missionProgress : missionsProgressesRepository.findByStudentId(student.getId())) {
        	if (missionProgress.isCompleted()) {
        		continue;
        	}

        	Mission mission = missionProgress.getMission();        	
        	
        	missionProgress.setCurrentCount(mission.getProgress(missionProgress.getCurrentCount(), quizAttempt));
        	if (missionProgress.getCurrentCount() == missionProgress.getGoal()) {
        		missionProgress.setCompleted(true);
        	}
        	missionsProgressesRepository.save(missionProgress);
        }
	}
	
    // Creates the instances of MissionProgress for each mission in the DB, if the Student doesn't have one yet
    private void fillMissingMissionProgress(Student student) {
    	for (Mission mission : missionsRepository.findAll()) {
        	List<MissionProgress> missionProgress = missionsProgressesRepository.findByMissionIdAndStudentId(mission.getId(), student.getId());
        	if (missionProgress.size() == 0) {
        		MissionProgress progress = new MissionProgress(mission, student, mission.getGoal());
        		missionsProgressesRepository.save(progress);
        	}
        }
    }
}
