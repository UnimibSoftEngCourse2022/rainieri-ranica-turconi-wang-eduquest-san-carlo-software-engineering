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
import it.bicocca.eduquest.services.GamificationServices;

@Component
public class GamificationListener {
	private final MissionsRepository missionsRepository;
	private final MissionsProgressesRepository missionsProgressesRepository;
	private final GamificationServices gamificationServices;
	
    public GamificationListener(MissionsRepository missionsRepository, MissionsProgressesRepository missionsProgressesRepository, GamificationServices gamificationServices) {
        this.missionsRepository = missionsRepository;
        this.missionsProgressesRepository = missionsProgressesRepository;
        this.gamificationServices = gamificationServices;
    }

    @EventListener
    @Transactional
    public void handleQuizStatsUpdate(QuizCompletedEvent event) {
        QuizAttempt attempt = event.getAttempt();
        gamificationServices.updateMissionsProgresses(attempt);        
    }
}
