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
import it.bicocca.eduquest.repository.BadgeRepository;
import it.bicocca.eduquest.domain.gamification.ChallengeNumberMission;
import it.bicocca.eduquest.domain.gamification.Badge;

@Service
public class GamificationServices {
	private final MissionsProgressesRepository missionsProgressesRepository;
	private final MissionsRepository missionsRepository;
	private final BadgeRepository badgeRepository;
	
	public GamificationServices(MissionsProgressesRepository missionsProgressesRepository,
			MissionsRepository missionsRepository, BadgeRepository badgeRepository) {
		this.missionsProgressesRepository = missionsProgressesRepository;
		this.missionsRepository = missionsRepository;
		this.badgeRepository = badgeRepository;
	}

	@Transactional
	public List<MissionProgressDTO> getAllMissionsProgressesByUserId(long userId, boolean onlyCompleted) {
		if (isMissionListExpired(missionsProgressesRepository.findByStudentId(userId))) {
			refreshWeeklyMissions(userId);
		}
		
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
	
	public List<BadgeDTO> getStudentBadges(long studentId) {
        List<Badge> badges = badgeRepository.findByStudentId(studentId);
        
        return badges.stream()
            .map(b -> new BadgeDTO(b.getId(), b.getName(), b.getDescription(),  b.getObtainedDate()))
            .collect(Collectors.toList());
    }
	
	public void updateMissionsProgresses(QuizAttempt quizAttempt) {
        Student student = ((Student)Hibernate.unproxy(quizAttempt.getStudent()));
        
        for (MissionProgress missionProgress : missionsProgressesRepository.findByStudentId(student.getId())) {
        	if (missionProgress.isCompleted()) {
        		continue;
        	}
        	
        	Mission mission = missionProgress.getMission(); 
        	
        	int newProgress = mission.getProgress(missionProgress.getCurrentCount(), quizAttempt);
            updateAndSaveProgress(missionProgress, newProgress);
        }
	}
	
	public void updateMissionsProgresses(long studentId, boolean isVictory) {
		for (MissionProgress missionProgress : missionsProgressesRepository.findByStudentId(studentId)) {
			Mission mission = missionProgress.getMission();

			if (!missionProgress.isCompleted() && mission instanceof ChallengeNumberMission) {
				if (isVictory) {
					updateAndSaveProgress(missionProgress, missionProgress.getCurrentCount() + 1);
				}
			}
		}
	}
    
    private void updateAndSaveProgress(MissionProgress progress, int newCurrentCount) {
    	progress.setCurrentCount(newCurrentCount);
    	
    	if (newCurrentCount >= progress.getGoal()) {
    		progress.setCurrentCount(progress.getGoal());
    		if (!progress.isCompleted()) {
                progress.setCompleted(true);
                Student realStudent = (Student) Hibernate.unproxy(progress.getStudent());
                Badge newBadge = new Badge(progress.getMission(), realStudent);
                badgeRepository.save(newBadge);
            }
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
