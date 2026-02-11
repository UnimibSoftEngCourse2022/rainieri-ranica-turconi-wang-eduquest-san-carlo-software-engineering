package it.bicocca.eduquest.dto.user;

import it.bicocca.eduquest.domain.users.Role;

public class UserLoginResponseDTO {
    private String token;
    private long userId;
    private Role role;

    public UserLoginResponseDTO(String token, long userId, Role role) {
        this.token = token;
        this.userId = userId;
        this.role = role;
    }

    public String getToken() {
    	return token;
    }
    
    public void setToken(String token) {
    	this.token = token;
    }

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}