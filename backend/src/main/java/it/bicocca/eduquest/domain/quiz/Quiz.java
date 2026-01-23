package it.bicocca.eduquest.domain.quiz;

import jakarta.persistence.*;

@Entity
@Table(name = "quizzes") 
@Inheritance(strategy = InheritanceType.JOINED)
public class Quiz {
	
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	
	
	
}
