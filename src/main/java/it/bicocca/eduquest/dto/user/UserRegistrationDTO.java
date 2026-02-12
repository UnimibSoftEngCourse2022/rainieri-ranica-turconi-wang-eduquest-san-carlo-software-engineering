package it.bicocca.eduquest.dto.user;

import it.bicocca.eduquest.domain.users.Role;
import jakarta.validation.constraints.NotBlank;

public class UserRegistrationDTO {
    
	@NotBlank
	private String name;	
	@NotBlank
    private String surname;	
	
    private String email;
    private String password;

    private Role role; 

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}