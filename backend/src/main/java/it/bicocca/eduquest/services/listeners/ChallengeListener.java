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
import java.util.Optional;
import java.time.LocalDateTime;

@Component
public class ChallengeListener {

    private final ChallengeRepository challengeRepository;

    public ChallengeListener(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @EventListener
    @Transactional
    public void handleChallengeUpdate(QuizCompletedEvent event) {
        QuizAttempt attempt = event.getAttempt();
        User user = attempt.getStudent();
        Long quizId = attempt.getQuiz().getId();

        // Check if there is an active challenge for this user and this quiz
        Optional<Challenge> challengeOpt = challengeRepository.findActiveChallengeForUserAndQuiz(user.getId(), quizId);

        if (challengeOpt.isEmpty()) {
            return;
        }

        Challenge challenge = challengeOpt.get();

        // Check whether it has expired before proceeding
        if (challenge.isExpired()) {
            challenge.setStatus(ChallengeStatus.EXPIRED);
            challengeRepository.save(challenge);
            return;
        }

        // Connect the attempt to the right student
        if (challenge.getChallenger().getId().equals(user.getId())) {
            challenge.setChallengerAttempt(attempt);
        } else {
            challenge.setOpponentAttempt(attempt);
        }

        // Check if both have played
        if (challenge.getChallengerAttempt() != null && challenge.getOpponentAttempt() != null) {
            closeChallenge(challenge);
        }

        challengeRepository.save(challenge);
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
        
    }
}
