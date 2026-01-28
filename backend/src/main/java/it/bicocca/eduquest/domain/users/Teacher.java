package it.bicocca.eduquest.domain.users;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "teachers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Teacher extends User {

	public Teacher() {
		super();
		this.role = Role.TEACHER;
	}

	public Teacher(String name, String surname, String email, String password) {
		super(name, surname, email, password);
		this.role = Role.TEACHER;
	}
	
}
