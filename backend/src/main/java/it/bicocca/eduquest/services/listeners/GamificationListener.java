package it.bicocca.eduquest.services.listeners;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.bicocca.eduquest.domain.answers.*;
import it.bicocca.eduquest.domain.quiz.*;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.events.QuizCompletedEvent;
import it.bicocca.eduquest.domain.gamification.Mission;
import it.bicocca.eduquest.domain.gamification.MissionProgress;
import it.bicocca.eduquest.repository.*;
import it.bicocca.eduquest.repository.QuestionsRepository;

@Component
public class GamificationListener {
	private final MissionsRepository missionsRepository;
	private final MissionsProgressesRepository missionsProgressesRepository;

    public GamificationListener(MissionsRepository missionsRepository, MissionsProgressesRepository missionsProgressesRepository) {
        this.missionsRepository = missionsRepository;
        this.missionsProgressesRepository = missionsProgressesRepository;
    }

    @EventListener
    @Transactional
    public void handleQuizStatsUpdate(QuizCompletedEvent event) {
        QuizAttempt attempt = event.getAttempt();
        Student student = ((Student)Hibernate.unproxy(attempt.getStudent()));
        
        this.fillMissingMissionProgress(student);
        
        for (MissionProgress missionProgress : missionsProgressesRepository.findAll()) {
        	Mission mission = missionProgress.getMission();
        	missionProgress.setCurrentCount(mission.getProgress(missionProgress.getCurrentCount(), attempt));
        	missionsProgressesRepository.save(missionProgress);
        }
    }
    
    // Creates the instances of MissionProgress for each mission in the DB, if the Student doesn't have one yet
    private void fillMissingMissionProgress(Student student) {
    	for (Mission mission : missionsRepository.findAll()) {
        	List<MissionProgress> missionProgress = missionsProgressesRepository.findByMissionId(mission.getId());
        	if (missionProgress.size() == 0) {
        		MissionProgress progress = new MissionProgress(mission, student, mission.getGoal());
        		missionsProgressesRepository.save(progress);
        	}
        }
    }
}
