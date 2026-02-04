package it.bicocca.eduquest.services;

import org.springframework.stereotype.Service;
import it.bicocca.eduquest.repository.ChallengeRepository;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.UsersRepository;
import it.bicocca.eduquest.dto.gamification.*;
import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.domain.quiz.*;
import it.bicocca.eduquest.domain.gamification.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Service
public class ChallengeServices {
	private final ChallengeRepository challengesRepository;
	private final QuizRepository quizRepository;
	private final UsersRepository usersRepository;
	
	public ChallengeServices(ChallengeRepository challengesRepository, QuizRepository quizRepository,
			UsersRepository usersRepository) {
		this.challengesRepository = challengesRepository;
		this.quizRepository = quizRepository;
		this.usersRepository = usersRepository;
	}
	
	public List<ChallengeDTO> getChallengesByUserId(Long studentId) {
		List<Challenge> challenges = challengesRepository.findAll();
		
		List<ChallengeDTO> challengesDTO = new ArrayList<ChallengeDTO>();
		for (Challenge c : challenges) {
			if (c.getChallenger().getId().equals(studentId) || c.getOpponent().getId().equals(studentId)) {
				challengesDTO.add(convertChallengeToDTO(c));
			}
		}
		
		return challengesDTO;
	}
	
	public void markExpiredChallenges() {
	    List<Challenge> expiredChallenges = challengesRepository.findExpiredActiveChallenges(LocalDateTime.now());

	    for (Challenge challenge : expiredChallenges) {
	        challenge.setStatus(ChallengeStatus.EXPIRED);
	        challengesRepository.save(challenge);
	    }
	}
	
	@Transactional
	public ChallengeDTO createChallenge(Long challengerId, ChallengeCreateDTO challengeCreateDTO) {
		User challenger = usersRepository.findById(challengerId).orElseThrow(() -> new RuntimeException("Challenger not found"));
		
		User opponent = usersRepository.findById(challengeCreateDTO.getOpponentId()).orElseThrow(() -> new RuntimeException("Opponent not found"));
		
		Quiz quiz = quizRepository.findById(challengeCreateDTO.getQuizId()).orElseThrow(() -> new RuntimeException("Quiz not found"));
		
		Challenge challenge = new Challenge(challenger, opponent, quiz, challengeCreateDTO.getDurationInHours());
		
		challengesRepository.save(challenge);
				
		return convertChallengeToDTO(challenge);
	}
	
	private ChallengeDTO convertChallengeToDTO(Challenge challenge) {
		ChallengeDTO challengeDTO = new ChallengeDTO();
		challengeDTO.setId(challenge.getId());
		challengeDTO.setChallengerId(challenge.getChallenger().getId());
		challengeDTO.setChallengerName(challenge.getChallenger().getName());
		challengeDTO.setChallengerSurname(challenge.getChallenger().getSurname());
		challengeDTO.setOpponentId(challenge.getOpponent().getId());
		challengeDTO.setOpponentName(challenge.getOpponent().getName());
		challengeDTO.setOpponentSurname(challenge.getOpponent().getSurname());
		challengeDTO.setQuizTitle(challenge.getQuiz().getTitle());
		challengeDTO.setStatus(challenge.getStatus());
		challengeDTO.setExpiresAt(challenge.getExpiresAt());
		
		if (challenge.getWinner() != null) {
		    challengeDTO.setWinnerName(challenge.getWinner().getName()); 
		    challengeDTO.setWinnerSurname(challenge.getWinner().getSurname());
		}
		
		return challengeDTO;
	}
	
}
