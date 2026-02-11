package it.bicocca.eduquest.services.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import it.bicocca.eduquest.domain.answers.*;
import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.domain.events.QuizCompletedEvent;
import it.bicocca.eduquest.domain.gamification.Challenge;
import it.bicocca.eduquest.domain.gamification.ChallengeStatus;
import it.bicocca.eduquest.repository.ChallengeRepository;
import it.bicocca.eduquest.services.GamificationServices;

import java.util.List;
import java.time.LocalDateTime;

@Component
public class ChallengeListener {

    private final ChallengeRepository challengeRepository;
    private final GamificationServices gamificationServices;

    public ChallengeListener(ChallengeRepository challengeRepository, GamificationServices gamificationServices) {
		this.challengeRepository = challengeRepository;
		this.gamificationServices = gamificationServices;
	}

	@EventListener
    @Transactional
    public void handleChallengeUpdate(QuizCompletedEvent event) {
        QuizAttempt attempt = event.getAttempt();
        User user = attempt.getStudent();
        Long quizId = attempt.getQuiz().getId();

        List<Challenge> challengesOpt = challengeRepository.findActiveChallengeForUserAndQuiz(user.getId(), quizId);

        if (challengesOpt.isEmpty()) {
            return;
        }

        for (Challenge challenge : challengesOpt) {
            if (challenge.isExpired()) {
                challenge.setStatus(ChallengeStatus.EXPIRED);
                challengeRepository.save(challenge);
                return;
            }

            boolean isUpdated = false;

            if (challenge.getChallenger().getId().equals(user.getId())) {
                if (challenge.getChallengerAttempt() == null) {
                    challenge.setChallengerAttempt(attempt);
                    isUpdated = true;
                }
            } 
            else {
                if (challenge.getOpponentAttempt() == null) {
                    challenge.setOpponentAttempt(attempt);
                    isUpdated = true;
                }
            }

            if (isUpdated) {
                if (challenge.getChallengerAttempt() != null && challenge.getOpponentAttempt() != null) {
                    closeChallenge(challenge);
                }
                challengeRepository.save(challenge);
                
                break; 
            }
        }
    }

    private void closeChallenge(Challenge challenge) {
        double score1 = challenge.getChallengerAttempt().getScore();
        double score2 = challenge.getOpponentAttempt().getScore();
        
        if (score1 > score2) {
            challenge.setWinner(challenge.getChallenger());
        } else if (score2 > score1) {
            challenge.setWinner(challenge.getOpponent());
        } else {
            // Draw
        	challenge.setWinner(null);
        }

        challenge.setStatus(ChallengeStatus.COMPLETED);
        challenge.setCompletedAt(LocalDateTime.now());
        
        if (challenge.getWinner() != null) {
            gamificationServices.updateMissionsProgresses(challenge.getWinner().getId(), true);
        }
    }
}
