package it.bicocca.eduquest.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final TestRepository testRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default.password}")
    private String defaultPassword;

    public DatabaseLoader(UsersRepository usersRepository, QuizRepository quizRepository, 
                          QuestionsRepository questionsRepository, PasswordEncoder passwordEncoder, 
                          MissionsRepository missionsRepository, TestRepository testRepository) {
        this.usersRepository = usersRepository;
        this.quizRepository = quizRepository;
        this.questionsRepository = questionsRepository;
        this.passwordEncoder = passwordEncoder;
        this.missionsRepository = missionsRepository;
        this.testRepository = testRepository;
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
        
        logger.info("Users created: Teacher ID={}, Student ID={}", teacher.getId(), student.getId());

        ClosedQuestion q1 = new ClosedQuestion("Qual è la capitale d'Italia?", "Geografia", teacher, Difficulty.EASY);
        q1.addOption(new ClosedQuestionOption("Milano", false));
        q1.addOption(new ClosedQuestionOption("Roma", true));
        q1.addOption(new ClosedQuestionOption("Torino", false));
        questionsRepository.save(q1);

        ClosedQuestion q2 = new ClosedQuestion("Quanto fa 2 + 2?", "Matematica", teacher, Difficulty.EASY);
        q2.addOption(new ClosedQuestionOption("3", false));
        q2.addOption(new ClosedQuestionOption("4", true));
        questionsRepository.save(q2);

        OpenQuestion q3 = new OpenQuestion("Chi ha scritto la Divina Commedia?", "Letteratura", student, Difficulty.MEDIUM);
        q3.addAnswer(new OpenQuestionAcceptedAnswer("Dante"));
        q3.addAnswer(new OpenQuestionAcceptedAnswer("Dante Alighieri"));
        q3.addAnswer(new OpenQuestionAcceptedAnswer("Alighieri"));
        questionsRepository.save(q3);
        
        ClosedQuestion q4 = new ClosedQuestion("Qual è il simbolo chimico dell'Ossigeno?", "Chimica", teacher, Difficulty.EASY);
        q4.addOption(new ClosedQuestionOption("O", true));
        q4.addOption(new ClosedQuestionOption("Ox", false));
        q4.addOption(new ClosedQuestionOption("Os", false));
        questionsRepository.save(q4);

        OpenQuestion q5 = new OpenQuestion("In che anno è stata scoperta l'America?", "Storia", teacher, Difficulty.MEDIUM);
        q5.addAnswer(new OpenQuestionAcceptedAnswer("1492"));
        questionsRepository.save(q5);

        ClosedQuestion q6 = new ClosedQuestion("Quale pianeta è noto come il Pianeta Rosso?", "Astronomia", teacher, Difficulty.EASY);
        q6.addOption(new ClosedQuestionOption("Marte", true));
        q6.addOption(new ClosedQuestionOption("Giove", false));
        q6.addOption(new ClosedQuestionOption("Venere", false));
        questionsRepository.save(q6);

        OpenQuestion q7 = new OpenQuestion("Qual è il fiume più lungo d'Italia?", "Geografia", teacher1, Difficulty.MEDIUM);
        q7.addAnswer(new OpenQuestionAcceptedAnswer("Po"));
        q7.addAnswer(new OpenQuestionAcceptedAnswer("Il Po"));
        questionsRepository.save(q7);

        ClosedQuestion q8 = new ClosedQuestion("Qual è la formulazione corretta della Seconda Legge della Dinamica?", "Fisica", teacher1, Difficulty.HARD);
        q8.addOption(new ClosedQuestionOption("F = m * a", true));
        q8.addOption(new ClosedQuestionOption("F = m / a", false));
        q8.addOption(new ClosedQuestionOption("F = m * v", false));
        q8.addOption(new ClosedQuestionOption("F = m * a^2", false));
        questionsRepository.save(q8);

        OpenQuestion q9 = new OpenQuestion("Chi ha dipinto la Gioconda?", "Arte", teacher1, Difficulty.MEDIUM);
        q9.addAnswer(new OpenQuestionAcceptedAnswer("Leonardo"));
        q9.addAnswer(new OpenQuestionAcceptedAnswer("Leonardo da Vinci"));
        q9.addAnswer(new OpenQuestionAcceptedAnswer("Da Vinci"));
        questionsRepository.save(q9);

        ClosedQuestion q10 = new ClosedQuestion("Qual è il participio passato di 'Go'?", "Inglese", teacher1, Difficulty.EASY);
        q10.addOption(new ClosedQuestionOption("Went", false));
        q10.addOption(new ClosedQuestionOption("Gone", true));
        q10.addOption(new ClosedQuestionOption("Goed", false));
        questionsRepository.save(q10);

        OpenQuestion q11 = new OpenQuestion("Qual è la formula chimica dell'acqua?", "Chimica", teacher1, Difficulty.EASY);
        q11.addAnswer(new OpenQuestionAcceptedAnswer("H2O"));
        q11.addAnswer(new OpenQuestionAcceptedAnswer("h2o"));
        questionsRepository.save(q11);

        ClosedQuestion q12 = new ClosedQuestion("Chi ha scolpito la famosa statua del David?", "Arte", student1, Difficulty.EASY);
        q12.addOption(new ClosedQuestionOption("Michelangelo Buonarroti", true));
        q12.addOption(new ClosedQuestionOption("Donatello", false));
        q12.addOption(new ClosedQuestionOption("Leonardo da Vinci", false));
        questionsRepository.save(q12);

        OpenQuestion q13 = new OpenQuestion("Chi è soprannominato il Re del Pop?", "Musica", student1, Difficulty.MEDIUM);
        q13.addAnswer(new OpenQuestionAcceptedAnswer("Michael Jackson"));
        q13.addAnswer(new OpenQuestionAcceptedAnswer("Jackson"));
        questionsRepository.save(q13);
        
        ClosedQuestion q14 = new ClosedQuestion("Quanti giocatori ci sono in una squadra di calcio in campo?", "Sport", student, Difficulty.EASY);
        q14.addOption(new ClosedQuestionOption("11", true));
        q14.addOption(new ClosedQuestionOption("7", false));
        q14.addOption(new ClosedQuestionOption("5", false));
        questionsRepository.save(q14);

        logger.info("Questions created.");

        Quiz quiz1 = new Quiz("General Knowledge Quiz", "Test yourself with questions about culture.", teacher);
        quiz1 = quizRepository.save(quiz1);
        quiz1.addQuestion(q1); 
        quiz1.addQuestion(q3); 
        quiz1.addQuestion(q5); 
        quiz1.addQuestion(q7); 
        quiz1.addQuestion(q9); 
        quiz1.addQuestion(q10); 
        quiz1.addQuestion(q12); 
        quiz1.addQuestion(q13); 
        quizRepository.save(quiz1);

        Quiz quiz2 = new Quiz("Science Quiz", "Challenge your scientific knowledge.", teacher1);
        quiz2 = quizRepository.save(quiz2);
        quiz2.addQuestion(q2); 
        quiz2.addQuestion(q4); 
        quiz2.addQuestion(q6); 
        quiz2.addQuestion(q8); 
        quiz2.addQuestion(q11); 
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
        quizRepository.save(quiz3);
        
        Quiz quiz4 = new Quiz("Short General Quiz", "A quick 5-question challenge.", teacher1);
        quiz4 = quizRepository.save(quiz4);
        quiz4.addQuestion(q7);
        quiz4.addQuestion(q9);
        quiz4.addQuestion(q10);
        quiz4.addQuestion(q12);
        quiz4.addQuestion(q13);
        quizRepository.save(quiz4);
        
        Test test1 = new Test();
        test1.setQuiz(quiz3);
        test1.setMaxDuration(Duration.ofMinutes(10));
        test1.setMaxTries(3);
        testRepository.save(test1);
        
        Mission mission1 = new QuizzesNumberMission(1);
        missionsRepository.save(mission1);

        Mission mission2 = new QuizzesNumberMission(5);
        missionsRepository.save(mission2);

        Mission mission3 = new NoErrorQuizMission(1);
        missionsRepository.save(mission3);
        
        Mission mission4 = new NoErrorQuizMission(5);
        missionsRepository.save(mission4);
        
        Mission mission5 = new ChallengeNumberMission(1);
        missionsRepository.save(mission5);
        
        Mission mission6 = new ChallengeNumberMission(5);
        missionsRepository.save(mission6);
        
        Mission mission7 = new CorrectedAnswerNumberMission(5);
        missionsRepository.save(mission7);
        
        Mission mission8 = new CorrectedAnswerNumberMission(25);
        missionsRepository.save(mission8);
        
        logger.info("Quizzes created and populated!");
        logger.info("The database is ready!");
    }
}