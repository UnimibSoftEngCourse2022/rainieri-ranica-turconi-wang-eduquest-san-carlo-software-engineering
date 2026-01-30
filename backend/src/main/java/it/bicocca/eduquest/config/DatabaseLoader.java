package it.bicocca.eduquest.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.bicocca.eduquest.domain.quiz.*;
import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.repository.*;

@Component
public class DatabaseLoader implements CommandLineRunner {

	private final UsersRepository usersRepository;
    private final QuizRepository quizRepository;
    private final QuestionsRepository questionsRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseLoader(UsersRepository usersRepository, QuizRepository quizRepository, QuestionsRepository questionsRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.quizRepository = quizRepository;
        this.questionsRepository = questionsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if the DB is already full (useful if we change the configuration in the future)
        if (usersRepository.count() > 0) return;

        System.out.println("Test database population in progress...");
        
        String criptedPassword = passwordEncoder.encode("0000");

        // Users
        Teacher teacher = new Teacher("Mario", "Rossi", "mario.rossi@unimib.it", criptedPassword);
        usersRepository.save(teacher);

        Student student = new Student("Luigi", "Bianchi", "l.bianchi@campus.unimib.it", criptedPassword);
        usersRepository.save(student);

        System.out.println("Users created: Teacher ID=" + teacher.getId() + ", Student ID=" + student.getId());

        // Questions:
        
        // Closed question 1
        ClosedQuestion q1 = new ClosedQuestion("Qual è la capitale d'Italia?", "Geografia", teacher, Difficulty.EASY);
        q1.addOption(new ClosedQuestionOption("Milano", false));
        q1.addOption(new ClosedQuestionOption("Roma", true));
        q1.addOption(new ClosedQuestionOption("Torino", false));
        questionsRepository.save(q1);

        // Closed question 2
        ClosedQuestion q2 = new ClosedQuestion("Quanto fa 2 + 2?", "Matematica", teacher, Difficulty.EASY);
        q2.addOption(new ClosedQuestionOption("3", false));
        q2.addOption(new ClosedQuestionOption("4", true));
        questionsRepository.save(q2);

        // Open question 1
        OpenQuestion q3 = new OpenQuestion("Chi ha scritto la Divina Commedia?", "Letteratura", student, Difficulty.MEDIUM);
        q3.addAnswer(new OpenQuestionAcceptedAnswer("Dante"));
        q3.addAnswer(new OpenQuestionAcceptedAnswer("Dante Alighieri"));
        q3.addAnswer(new OpenQuestionAcceptedAnswer("Alighieri"));
        questionsRepository.save(q3);

        System.out.println("Questions created.");

        // Quiz

        // Quiz 1
        Quiz quiz1 = new Quiz("General knowledge Test", "Simple quiz for beginners", teacher);
        quiz1 = quizRepository.save(quiz1);
        
        quiz1.addQuestion(q1);
        quiz1.addQuestion(q3); 
        quizRepository.save(quiz1);

        // Quiz 2
        Quiz quiz2 = new Quiz("Maths Test", "Only for true experts", teacher);
        quiz2 = quizRepository.save(quiz2);
        
        quiz2.addQuestion(q2);
        quizRepository.save(quiz2);

        System.out.println("Quizzes created and populated!");
        System.out.println("The database is ready!");
    }
}
