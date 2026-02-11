package it.bicocca.eduquest.config;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.bicocca.eduquest.domain.answers.*;
import it.bicocca.eduquest.domain.gamification.*;
import it.bicocca.eduquest.domain.quiz.*;
import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.repository.*;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

    private final UsersRepository usersRepository;
    private final QuizRepository quizRepository;
    private final QuestionsRepository questionsRepository;
    private final MissionsRepository missionsRepository;
    private final MissionsProgressesRepository missionsProgressesRepository;
    private final BadgeRepository badgeRepository;
    private final TestRepository testRepository;
    private final QuizAttemptsRepository quizAttemptsRepository;
    private final AnswersRepository answersRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default.password}")
    private String defaultPassword;
    
    private String answerDante = "Dante Alighieri";
    private String answerLeonardo = "Leonardo";
    private String answerDaVinci = "Leonardo da Vinci";
    private String answerJackson = "Michael Jackson";

    public DatabaseLoader(UsersRepository usersRepository, QuizRepository quizRepository, 
                          QuestionsRepository questionsRepository, PasswordEncoder passwordEncoder, 
                          MissionsRepository missionsRepository, 
                          MissionsProgressesRepository missionsProgressesRepository,
                          BadgeRepository badgeRepository,
                          TestRepository testRepository,
                          QuizAttemptsRepository quizAttemptsRepository, AnswersRepository answersRepository) {
        this.usersRepository = usersRepository;
        this.quizRepository = quizRepository;
        this.questionsRepository = questionsRepository;
        this.passwordEncoder = passwordEncoder;
        this.missionsRepository = missionsRepository;
        this.missionsProgressesRepository = missionsProgressesRepository;
        this.badgeRepository = badgeRepository;
        this.testRepository = testRepository;
        this.quizAttemptsRepository = quizAttemptsRepository;
        this.answersRepository = answersRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usersRepository.count() > 0) return;

        logger.info("Test database population in progress...");
        
        String criptedPassword = passwordEncoder.encode(defaultPassword);

        Teacher teacher = new Teacher("Mario", "Rossi", "mario.rossi@unimib.it", criptedPassword);
        usersRepository.save(teacher);

        Teacher teacher1 = new Teacher("Francesco", "Ferrari", "francesco.ferrari@unimib.it", criptedPassword);
        usersRepository.save(teacher1);
        
        Student student = new Student("Luigi", "Bianchi", "l.bianchi@campus.unimib.it", criptedPassword);
        usersRepository.save(student);
        
        Student student1 = new Student("Francesco", "Brembilla", "f.brembilla@campus.unimib.it", criptedPassword);
        usersRepository.save(student1);
        
        Student student2 = new Student("Anna", "Verdi", "a.verdi@campus.unimib.it", criptedPassword);
        usersRepository.save(student2);
        
        ClosedQuestion q1 = new ClosedQuestion("Qual è la capitale d'Italia?", "Geografia", teacher, Difficulty.EASY);
        ClosedQuestionOption q1OptCorrect = new ClosedQuestionOption("Roma", true);
        q1.addOption(new ClosedQuestionOption("Milano", false));
        q1.addOption(q1OptCorrect);
        q1.addOption(new ClosedQuestionOption("Torino", false));
        questionsRepository.save(q1);

        ClosedQuestion q2 = new ClosedQuestion("Quanto fa 2 + 2?", "Matematica", teacher, Difficulty.EASY);
        ClosedQuestionOption q2OptCorrect = new ClosedQuestionOption("4", true);
        q2.addOption(new ClosedQuestionOption("3", false));
        q2.addOption(q2OptCorrect);
        questionsRepository.save(q2);

        OpenQuestion q3 = new OpenQuestion("Chi ha scritto la Divina Commedia?", "Letteratura", student, Difficulty.MEDIUM);
        q3.addAnswer(new OpenQuestionAcceptedAnswer("Dante"));
        q3.addAnswer(new OpenQuestionAcceptedAnswer(answerDante));
        q3.addAnswer(new OpenQuestionAcceptedAnswer("Alighieri"));
        questionsRepository.save(q3);
        
        ClosedQuestion q4 = new ClosedQuestion("Qual è il simbolo chimico dell'Ossigeno?", "Chimica", teacher, Difficulty.EASY);
        ClosedQuestionOption q4OptCorrect = new ClosedQuestionOption("O", true);
        q4.addOption(q4OptCorrect);
        q4.addOption(new ClosedQuestionOption("Ox", false));
        q4.addOption(new ClosedQuestionOption("Os", false));
        questionsRepository.save(q4);

        OpenQuestion q5 = new OpenQuestion("In che anno è stata scoperta l'America?", "Storia", teacher, Difficulty.MEDIUM);
        q5.addAnswer(new OpenQuestionAcceptedAnswer("1492"));
        questionsRepository.save(q5);

        ClosedQuestion q6 = new ClosedQuestion("Quale pianeta è noto come il Pianeta Rosso?", "Astronomia", teacher, Difficulty.EASY);
        ClosedQuestionOption q6OptCorrect = new ClosedQuestionOption("Marte", true);
        q6.addOption(q6OptCorrect);
        q6.addOption(new ClosedQuestionOption("Giove", false));
        q6.addOption(new ClosedQuestionOption("Venere", false));
        questionsRepository.save(q6);

        OpenQuestion q7 = new OpenQuestion("Qual è il fiume più lungo d'Italia?", "Geografia", teacher1, Difficulty.MEDIUM);
        q7.addAnswer(new OpenQuestionAcceptedAnswer("Po"));
        q7.addAnswer(new OpenQuestionAcceptedAnswer("Il Po"));
        questionsRepository.save(q7);

        ClosedQuestion q8 = new ClosedQuestion("Qual è la formulazione corretta della Seconda Legge della Dinamica?", "Fisica", teacher1, Difficulty.HARD);
        ClosedQuestionOption q8OptCorrect = new ClosedQuestionOption("F = m * a", true);
        q8.addOption(q8OptCorrect);
        q8.addOption(new ClosedQuestionOption("F = m / a", false));
        q8.addOption(new ClosedQuestionOption("F = m * v", false));
        q8.addOption(new ClosedQuestionOption("F = m * a^2", false));
        questionsRepository.save(q8);

        OpenQuestion q9 = new OpenQuestion("Chi ha dipinto la Gioconda?", "Arte", teacher1, Difficulty.MEDIUM);
        q9.addAnswer(new OpenQuestionAcceptedAnswer(answerLeonardo));
        q9.addAnswer(new OpenQuestionAcceptedAnswer(answerDaVinci));
        q9.addAnswer(new OpenQuestionAcceptedAnswer("Da Vinci"));
        questionsRepository.save(q9);

        ClosedQuestion q10 = new ClosedQuestion("Qual è il participio passato di 'Go'?", "Inglese", teacher1, Difficulty.EASY);
        ClosedQuestionOption q10OptCorrect = new ClosedQuestionOption("Gone", true);
        q10.addOption(new ClosedQuestionOption("Went", false));
        q10.addOption(q10OptCorrect);
        q10.addOption(new ClosedQuestionOption("Goed", false));
        questionsRepository.save(q10);

        OpenQuestion q11 = new OpenQuestion("Qual è la formula chimica dell'acqua?", "Chimica", teacher1, Difficulty.EASY);
        q11.addAnswer(new OpenQuestionAcceptedAnswer("H2O"));
        q11.addAnswer(new OpenQuestionAcceptedAnswer("h2o"));
        questionsRepository.save(q11);

        ClosedQuestion q12 = new ClosedQuestion("Chi ha scolpito la famosa statua del David?", "Arte", student1, Difficulty.EASY);
        ClosedQuestionOption q12OptCorrect = new ClosedQuestionOption("Michelangelo Buonarroti", true);
        q12.addOption(q12OptCorrect);
        q12.addOption(new ClosedQuestionOption("Donatello", false));
        q12.addOption(new ClosedQuestionOption(answerDaVinci, false));
        questionsRepository.save(q12);

        OpenQuestion q13 = new OpenQuestion("Chi è soprannominato il Re del Pop?", "Musica", student1, Difficulty.MEDIUM);
        q13.addAnswer(new OpenQuestionAcceptedAnswer(answerJackson));
        q13.addAnswer(new OpenQuestionAcceptedAnswer("Jackson"));
        questionsRepository.save(q13);
        
        ClosedQuestion q14 = new ClosedQuestion("Quanti giocatori ci sono in una squadra di calcio in campo?", "Sport", student, Difficulty.EASY);
        ClosedQuestionOption q14OptCorrect = new ClosedQuestionOption("11", true);
        q14.addOption(q14OptCorrect);
        q14.addOption(new ClosedQuestionOption("7", false));
        q14.addOption(new ClosedQuestionOption("5", false));
        questionsRepository.save(q14);

        logger.info("Questions created.");

        Quiz quiz1 = new Quiz("General Knowledge Quiz", "Test yourself with questions about culture.", teacher);
        quiz1.setPublic(true);
        quiz1 = quizRepository.save(quiz1);
        quiz1.addQuestion(q1); 
        quiz1.addQuestion(q3); 
        quiz1.addQuestion(q5); 
        quiz1.addQuestion(q7); 
        quiz1.addQuestion(q9); 
        quiz1.addQuestion(q10); 
        quiz1.addQuestion(q12); 
        quiz1.addQuestion(q13); 
        quiz1.recalculateDifficulty();
        quizRepository.save(quiz1);

        Quiz quiz2 = new Quiz("Science Quiz", "Challenge your scientific knowledge.", teacher1);
        quiz2.setPublic(true);
        quiz2 = quizRepository.save(quiz2);
        quiz2.addQuestion(q2); 
        quiz2.addQuestion(q4); 
        quiz2.addQuestion(q6); 
        quiz2.addQuestion(q8); 
        quiz2.addQuestion(q11); 
        quiz2.recalculateDifficulty();
        quizRepository.save(quiz2);
        
        Quiz quiz3 = new Quiz("10 Question Test", "Mixed test.", teacher);
        quiz3 = quizRepository.save(quiz3);
        quiz3.addQuestion(q14);
        quiz3.addQuestion(q1);
        quiz3.addQuestion(q2);
        quiz3.addQuestion(q3);
        quiz3.addQuestion(q4);
        quiz3.addQuestion(q5);
        quiz3.addQuestion(q6);
        quiz3.addQuestion(q7);
        quiz3.addQuestion(q8);
        quiz3.addQuestion(q9);
        quiz3.recalculateDifficulty();
        quizRepository.save(quiz3);
        
        Quiz quiz4 = new Quiz("Short General Quiz", "A quick 5-question challenge.", teacher1);
        quiz4.setPublic(true);
        quiz4 = quizRepository.save(quiz4);
        quiz4.addQuestion(q7);
        quiz4.addQuestion(q9);
        quiz4.addQuestion(q10);
        quiz4.addQuestion(q12);
        quiz4.addQuestion(q13);
        quiz4.recalculateDifficulty();
        quizRepository.save(quiz4);
        
        Test test1 = new Test();
        test1.setQuiz(quiz3);
        test1.setMaxDuration(Duration.ofMinutes(10));
        test1.setMaxTries(3);
        testRepository.save(test1);
        
        QuizAttempt attempt1 = new QuizAttempt(student, quiz1);
        attempt1.setStatus(QuizAttemptStatus.COMPLETED);
        attempt1.setStartedAt(LocalDateTime.now().minusHours(2));
        attempt1.setFinishedAt(LocalDateTime.now().minusHours(1));
        attempt1 = quizAttemptsRepository.save(attempt1);
        
        saveAnswer(attempt1, q1, new ClosedAnswer(attempt1, q1, q1OptCorrect), true);
        saveAnswer(attempt1, q3, new OpenAnswer(attempt1, q3, answerDante), true);
        saveAnswer(attempt1, q5, new OpenAnswer(attempt1, q5, "1492"), true);
        saveAnswer(attempt1, q7, new OpenAnswer(attempt1, q7, "Po"), true);
        saveAnswer(attempt1, q9, new OpenAnswer(attempt1, q9, answerDaVinci), true);
        saveAnswer(attempt1, q12, new ClosedAnswer(attempt1, q12, q12OptCorrect), true);
        saveAnswer(attempt1, q10, new ClosedAnswer(attempt1, q10, q10.getOptions().get(0)), false);
        saveAnswer(attempt1, q13, new OpenAnswer(attempt1, q13, "Elvis Presley"), false);
        attempt1.setScore(6);
        attempt1.setMaxScore(8);
        quizAttemptsRepository.save(attempt1);
        
        updateStudentStats(student, 6, 8, 6);

        QuizAttempt attempt2 = new QuizAttempt(student, quiz2);
        attempt2.setStatus(QuizAttemptStatus.COMPLETED);
        attempt2.setStartedAt(LocalDateTime.now().minusDays(1));
        attempt2.setFinishedAt(LocalDateTime.now().minusDays(1).plusMinutes(15));
        attempt2 = quizAttemptsRepository.save(attempt2);
        
        saveAnswer(attempt2, q2, new ClosedAnswer(attempt2, q2, q2OptCorrect), true);
        saveAnswer(attempt2, q4, new ClosedAnswer(attempt2, q4, q4OptCorrect), true);
        saveAnswer(attempt2, q6, new ClosedAnswer(attempt2, q6, q6OptCorrect), true);
        saveAnswer(attempt2, q8, new ClosedAnswer(attempt2, q8, q8OptCorrect), true);
        saveAnswer(attempt2, q11, new OpenAnswer(attempt2, q11, "H2O"), true);
        attempt2.setScore(5);
        attempt2.setMaxScore(5);
        quizAttemptsRepository.save(attempt2);

        updateStudentStats(student, 5, 5, 5);

        QuizAttempt attempt3 = new QuizAttempt(student, quiz4);
        attempt3.setStatus(QuizAttemptStatus.COMPLETED);
        attempt3.setStartedAt(LocalDateTime.now().minusDays(2));
        attempt3.setFinishedAt(LocalDateTime.now().minusDays(2).plusMinutes(10));
        attempt3 = quizAttemptsRepository.save(attempt3);
        
        saveAnswer(attempt3, q7, new OpenAnswer(attempt3, q7, "Po"), true);
        saveAnswer(attempt3, q9, new OpenAnswer(attempt3, q9, answerLeonardo), true);
        saveAnswer(attempt3, q10, new ClosedAnswer(attempt3, q10, q10OptCorrect), true);
        saveAnswer(attempt3, q12, new ClosedAnswer(attempt3, q12, q12OptCorrect), true);
        saveAnswer(attempt3, q13, new OpenAnswer(attempt3, q13, "Madonna"), false);
        attempt3.setScore(4);
        attempt3.setMaxScore(5);
        quizAttemptsRepository.save(attempt3);

        updateStudentStats(student, 4, 5, 4);

        QuizAttempt attempt4 = new QuizAttempt(student1, quiz1);
        attempt4.setStatus(QuizAttemptStatus.COMPLETED);
        attempt4.setStartedAt(LocalDateTime.now().minusHours(5));
        attempt4.setFinishedAt(LocalDateTime.now().minusHours(4));
        attempt4 = quizAttemptsRepository.save(attempt4);
        
        saveAnswer(attempt4, q1, new ClosedAnswer(attempt4, q1, q1OptCorrect), true);
        saveAnswer(attempt4, q3, new OpenAnswer(attempt4, q3, "Dante"), true);
        saveAnswer(attempt4, q7, new OpenAnswer(attempt4, q7, "Il Po"), true);
        saveAnswer(attempt4, q10, new ClosedAnswer(attempt4, q10, q10OptCorrect), true);
        saveAnswer(attempt4, q13, new OpenAnswer(attempt4, q13, answerJackson), true);
        saveAnswer(attempt4, q5, new OpenAnswer(attempt4, q5, "1942"), false);
        saveAnswer(attempt4, q9, new OpenAnswer(attempt4, q9, "Raffaello"), false);
        saveAnswer(attempt4, q12, new ClosedAnswer(attempt4, q12, q12.getOptions().get(1)), false);
        attempt4.setScore(5);
        attempt4.setMaxScore(8);
        quizAttemptsRepository.save(attempt4);

        updateStudentStats(student1, 5, 8, 5);

        QuizAttempt attempt5 = new QuizAttempt(student1, quiz4);
        attempt5.setStatus(QuizAttemptStatus.COMPLETED);
        attempt5.setStartedAt(LocalDateTime.now().minusDays(3));
        attempt5.setFinishedAt(LocalDateTime.now().minusDays(3).plusMinutes(20));
        attempt5 = quizAttemptsRepository.save(attempt5);
        
        saveAnswer(attempt5, q7, new OpenAnswer(attempt5, q7, "Po"), true);
        saveAnswer(attempt5, q9, new OpenAnswer(attempt5, q9, "Da Vinci"), true);
        saveAnswer(attempt5, q10, new ClosedAnswer(attempt5, q10, q10OptCorrect), true);
        saveAnswer(attempt5, q12, new ClosedAnswer(attempt5, q12, q12OptCorrect), true);
        saveAnswer(attempt5, q13, new OpenAnswer(attempt5, q13, "Jackson"), true);
        attempt5.setScore(5);
        attempt5.setMaxScore(5);
        quizAttemptsRepository.save(attempt5);

        updateStudentStats(student1, 5, 5, 5);
        
        QuizAttempt attempt6 = new QuizAttempt(student2, quiz1);
        attempt6.setStatus(QuizAttemptStatus.COMPLETED);
        attempt6.setStartedAt(LocalDateTime.now().minusDays(5));
        attempt6.setFinishedAt(LocalDateTime.now().minusDays(5).plusMinutes(30));
        attempt6 = quizAttemptsRepository.save(attempt6);
        
        saveAnswer(attempt6, q1, new ClosedAnswer(attempt6, q1, q1OptCorrect), true);
        saveAnswer(attempt6, q3, new OpenAnswer(attempt6, q3, answerDante), true);
        saveAnswer(attempt6, q5, new OpenAnswer(attempt6, q5, "1492"), true);
        saveAnswer(attempt6, q7, new OpenAnswer(attempt6, q7, "Po"), true);
        saveAnswer(attempt6, q9, new OpenAnswer(attempt6, q9, answerDaVinci), true);
        saveAnswer(attempt6, q10, new ClosedAnswer(attempt6, q10, q10OptCorrect), true);
        saveAnswer(attempt6, q12, new ClosedAnswer(attempt6, q12, q12OptCorrect), true);
        saveAnswer(attempt6, q13, new OpenAnswer(attempt6, q13, answerJackson), true);
        
        attempt6.setScore(8);
        attempt6.setMaxScore(8);
        quizAttemptsRepository.save(attempt6);
        
        updateStudentStats(student2, 8, 8, 8);

        QuizAttempt attempt7 = new QuizAttempt(student2, quiz2);
        attempt7.setStatus(QuizAttemptStatus.COMPLETED);
        attempt7.setStartedAt(LocalDateTime.now().minusDays(4));
        attempt7.setFinishedAt(LocalDateTime.now().minusDays(4).plusMinutes(20));
        attempt7 = quizAttemptsRepository.save(attempt7);
        
        saveAnswer(attempt7, q2, new ClosedAnswer(attempt7, q2, q2OptCorrect), true);
        saveAnswer(attempt7, q4, new ClosedAnswer(attempt7, q4, q4OptCorrect), true);
        saveAnswer(attempt7, q6, new ClosedAnswer(attempt7, q6, q6OptCorrect), true);
        saveAnswer(attempt7, q8, new ClosedAnswer(attempt7, q8, q8OptCorrect), true);
        saveAnswer(attempt7, q11, new OpenAnswer(attempt7, q11, "H2O"), true);
        
        attempt7.setScore(5);
        attempt7.setMaxScore(5);
        quizAttemptsRepository.save(attempt7);
        
        updateStudentStats(student2, 5, 5, 5);

        QuizAttempt attempt8 = new QuizAttempt(student2, quiz4);
        attempt8.setStatus(QuizAttemptStatus.COMPLETED);
        attempt8.setStartedAt(LocalDateTime.now().minusDays(4).plusHours(2));
        attempt8.setFinishedAt(LocalDateTime.now().minusDays(4).plusHours(2).plusMinutes(10));
        attempt8 = quizAttemptsRepository.save(attempt8);
        
        saveAnswer(attempt8, q7, new OpenAnswer(attempt8, q7, "Po"), true);
        saveAnswer(attempt8, q9, new OpenAnswer(attempt8, q9, answerLeonardo), true); 
        saveAnswer(attempt8, q10, new ClosedAnswer(attempt8, q10, q10OptCorrect), true);
        saveAnswer(attempt8, q12, new ClosedAnswer(attempt8, q12, q12OptCorrect), true);
        saveAnswer(attempt8, q13, new OpenAnswer(attempt8, q13, answerJackson), true);
        
        attempt8.setScore(5);
        attempt8.setMaxScore(5);
        quizAttemptsRepository.save(attempt8);
        
        updateStudentStats(student2, 5, 5, 5);

        QuizAttempt attempt9 = new QuizAttempt(student, quiz4);
        attempt9.setStatus(QuizAttemptStatus.COMPLETED);
        attempt9.setStartedAt(LocalDateTime.now().minusMinutes(45));
        attempt9.setFinishedAt(LocalDateTime.now().minusMinutes(30));
        attempt9 = quizAttemptsRepository.save(attempt9);
        
        saveAnswer(attempt9, q7, new OpenAnswer(attempt9, q7, "Tevere"), false);
        saveAnswer(attempt9, q9, new OpenAnswer(attempt9, q9, "Raffaello"), false);
        saveAnswer(attempt9, q10, new ClosedAnswer(attempt9, q10, q10.getOptions().get(0)), false);
        saveAnswer(attempt9, q12, new ClosedAnswer(attempt9, q12, q12OptCorrect), true);
        saveAnswer(attempt9, q13, new OpenAnswer(attempt9, q13, answerJackson), true);
        
        attempt9.setScore(2);
        attempt9.setMaxScore(5);
        quizAttemptsRepository.save(attempt9);
        
        updateStudentStats(student, 2, 5, 2);

        Mission m1 = new QuizzesNumberMission(1); missionsRepository.save(m1);
        Mission m2 = new QuizzesNumberMission(5); missionsRepository.save(m2);
        Mission m3 = new NoErrorQuizMission(1); missionsRepository.save(m3);
        Mission m4 = new NoErrorQuizMission(5); missionsRepository.save(m4);
        Mission m5 = new ChallengeNumberMission(1); missionsRepository.save(m5);
        Mission m6 = new ChallengeNumberMission(5); missionsRepository.save(m6);
        Mission m7 = new CorrectedAnswerNumberMission(5); missionsRepository.save(m7);
        Mission m8 = new CorrectedAnswerNumberMission(25); missionsRepository.save(m8);

        createMissionProgress(student, m1, 1, true); assignBadge(student, m1);
        createMissionProgress(student, m4, 1, false);
        createMissionProgress(student, m7, 15, true); assignBadge(student, m7);
        createMissionProgress(student, m8, 17, false);

        createMissionProgress(student1, m1, 2, true); assignBadge(student1, m1);
        createMissionProgress(student1, m2, 2, false);
        createMissionProgress(student1, m3, 1, true); assignBadge(student1, m3);
        createMissionProgress(student1, m8, 10, false);
        
        createMissionProgress(student2, m2, 3, false);
        createMissionProgress(student2, m3, 1, true); assignBadge(student2, m3);
        createMissionProgress(student2, m4, 3, false);
        createMissionProgress(student2, m7, 18, true); assignBadge(student2, m7);
        
        logger.info("Quizzes, Tests, Attempts, Missions and Badges created and populated!");
        logger.info("The database is ready!");
    }

    private void saveAnswer(QuizAttempt attempt, Question question, Answer answer, boolean isCorrect) {
        answer.setQuizAttempt(attempt); 
        answer.setQuestion(question);
        answer.setCorrect(isCorrect);
        
        if (answer instanceof ClosedAnswer) {
             ((ClosedAnswer) answer).setCorrect(isCorrect);
        } else if (answer instanceof OpenAnswer) {
             ((OpenAnswer) answer).setCorrect(isCorrect);
        }
        
        answer = answersRepository.save(answer);
        attempt.addAnswer(answer); 
        
        QuestionStats globalStats = question.getStats();
        if (globalStats == null) {
            globalStats = new QuestionStats();
            question.setStats(globalStats);
        }
        globalStats.updateStats(isCorrect);
        questionsRepository.save(question);

        Quiz quiz = attempt.getQuiz();
        QuizStats quizStats = quiz.getStats();
        if (quizStats == null) {
            quizStats = new QuizStats();
            quiz.setStats(quizStats);
        }
        Map<Long, QuestionStats> statsPerQuestionMap = quizStats.getStatsPerQuestion();
        
        QuestionStats specificStats = statsPerQuestionMap.get(question.getId());
        if (specificStats == null) {
            specificStats = new QuestionStats();
            statsPerQuestionMap.put(question.getId(), specificStats);
        }
        
        specificStats.updateStats(isCorrect);
        quizRepository.save(quiz); 
    }
    
    private void updateStudentStats(User user, double score, int totalQuestions, int correctAnswers) {
        if (user instanceof Student) {
            Student s = (Student) user;
            s.updateTotalScore(score); 
            if (s.getStats() != null) {
                s.getStats().updateStats(score, totalQuestions, correctAnswers); 
            }
            usersRepository.save(s); 
        }
    }
    
    private void createMissionProgress(Student student, Mission mission, int currentCount, boolean isCompleted) {
        MissionProgress progress = new MissionProgress(mission, student, mission.getGoal());
        progress.setCurrentCount(currentCount);
        progress.setCompleted(isCompleted);
        missionsProgressesRepository.save(progress);
    }

    private void assignBadge(Student student, Mission mission) {
        Badge badge = new Badge(mission, student);
        badgeRepository.save(badge);
    }
}