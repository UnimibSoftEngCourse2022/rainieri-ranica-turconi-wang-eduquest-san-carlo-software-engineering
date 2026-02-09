package it.bicocca.eduquest.services.listeners;


import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.bicocca.eduquest.domain.answers.*;
import it.bicocca.eduquest.domain.events.QuizCompletedEvent;
import it.bicocca.eduquest.services.GamificationServices;

@Component
public class GamificationListener {
	private final GamificationServices gamificationServices;
	
    public GamificationListener(GamificationServices gamificationServices) {
        this.gamificationServices = gamificationServices;
    }

    @EventListener
    @Transactional
    public void handleQuizStatsUpdate(QuizCompletedEvent event) {
        QuizAttempt attempt = event.getAttempt();
        gamificationServices.updateMissionsProgresses(attempt);        
    }
}
