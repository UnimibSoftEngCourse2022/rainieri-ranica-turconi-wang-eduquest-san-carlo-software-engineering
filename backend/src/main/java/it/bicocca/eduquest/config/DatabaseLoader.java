package it.bicocca.eduquest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.bicocca.eduquest.domain.gamification.Mission;
import it.bicocca.eduquest.domain.gamification.NoErrorQuizMission;
import it.bicocca.eduquest.domain.gamification.QuizzesNumberMission;
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
    private final PasswordEncoder passwordEncoder;

    public DatabaseLoader(UsersRepository usersRepository, QuizRepository quizRepository, QuestionsRepository questionsRepository, PasswordEncoder passwordEncoder, MissionsRepository missionsRepository) {
        this.usersRepository = usersRepository;
        this.quizRepository = quizRepository;
        this.questionsRepository = questionsRepository;
        this.passwordEncoder = passwordEncoder;
        this.missionsRepository = missionsRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usersRepository.count() > 0) return;

        logger.info("Test database population in progress...");
        
        String criptedPassword = passwordEncoder.encode("0000");

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

        logger.info("Questions created.");

        Quiz quiz1 = new Quiz("General knowledge Test", "Simple quiz for beginners", teacher);
        quiz1 = quizRepository.save(quiz1);
        
        quiz1.addQuestion(q1);
        quiz1.addQuestion(q2);
        quiz1.addQuestion(q3); 
        quizRepository.save(quiz1);

        Quiz quiz2 = new Quiz("Maths Test", "Only for true experts", teacher);
        quiz2 = quizRepository.save(quiz2);
        
        quiz2.addQuestion(q2);
        quizRepository.save(quiz2);
        
        Mission mission1 = new QuizzesNumberMission(1);
        missionsRepository.save(mission1);

        Mission mission2 = new QuizzesNumberMission(5);
        missionsRepository.save(mission2);

        Mission mission3 = new NoErrorQuizMission(1);
        missionsRepository.save(mission3);
        
        Mission mission4 = new NoErrorQuizMission(5);
        missionsRepository.save(mission4);
        
        // TODO aggiungere missioni tipo "completa 4 quiz difficili/facili/... rispondendo correttamente ad almeno 80% delle domande"
        
        // TODO aggiungere missioni tipo "rispondi correttamente a n* domande difficili/facili/..."
        
        logger.info("Quizzes created and populated!");
        logger.info("The database is ready!");
    }
}