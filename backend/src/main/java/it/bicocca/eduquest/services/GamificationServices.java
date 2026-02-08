package it.bicocca.eduquest.services;

import java.util.ArrayList;
import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.Collectors;
import java.util.Collections;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Transactional
	public List<MissionProgressDTO> getAllMissionsProgressesByUserId(long userId, boolean onlyCompleted) {
		List<MissionProgressDTO> missionsProgresses = new ArrayList<>();
		
		for (MissionProgress progress : missionsProgressesRepository.findByStudentId(userId)) {
			Mission mission = progress.getMission();
			
			if (onlyCompleted && !progress.isCompleted()) {
				continue;
			}
			
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
        	if (missionProgress.isEmpty()) {
        		MissionProgress progress = new MissionProgress(mission, student, mission.getGoal());
        		missionsProgressesRepository.save(progress);
        	}
        }
    }
    
    private void updateAndSaveProgress(MissionProgress progress, int newCurrentCount) {
    	progress.setCurrentCount(newCurrentCount);
    	
    	if (newCurrentCount >= progress.getGoal()) {
    		progress.setCurrentCount(progress.getGoal());
    		progress.setCompleted(true);
    	}
    	missionsProgressesRepository.save(progress);
    }
    
    private boolean isMissionListExpired(List<MissionProgress> missions) {
    	if (missions.isEmpty()) return true;
    	MissionProgress sample = missions.get(0);
        LocalDate assignmentDate = sample.getAssignmentDate();
        if (assignmentDate == null) {
        	return true;
        }

        LocalDate today = LocalDate.now();
        LocalDate thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        return assignmentDate.isBefore(thisMonday);
    }
    
    @Transactional
    public void refreshWeeklyMissions(long userId) {
    	missionsProgressesRepository.deleteByStudentId(userId);
    	
    	List<Mission> allMissions = missionsRepository.findAll();
    	Collections.shuffle(allMissions);
        List<Mission> selectedMissions = allMissions.stream()
                                                    .limit(4)
                                                    .collect(Collectors.toList());
        Student studentReference = new Student(); 
        studentReference.setId(userId); 

        for (Mission m : selectedMissions) {
            MissionProgress mp = new MissionProgress(m, studentReference, m.getGoal());
            missionsProgressesRepository.save(mp);
        }
    }
}
