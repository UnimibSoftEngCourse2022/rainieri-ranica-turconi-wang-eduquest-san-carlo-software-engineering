package it.bicocca.eduquest.dto.user;

import it.bicocca.eduquest.domain.users.Role;

public class UserInfoDTO {
	private final String name;
	private final String surname;
	private final String email;
	private final Role role;
	// FIXME add userStatsDTO
	
	public UserInfoDTO(String name, String surname, String email, Role role) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getEmail() {
		return email;
	}

	public Role getRole() {
		return role;
	}
}
