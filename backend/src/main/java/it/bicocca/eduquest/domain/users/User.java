package it.bicocca.eduquest.domain.users;

import jakarta.persistence.*;

@Entity
@Table(name = "users") 
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id // Primary key in the DB table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // The DB generates automatically crescent numbers
    protected Long id;
    
    protected String name;
    protected String surname;
    protected String email;
    protected String password;
    
    @Enumerated(EnumType.STRING)
    protected Role role;

    // --- CONSTRUCTORS ---

    public User() {
    }

    public User(String name, String surname, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    // --- GETTER AND SETTER ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
    
}