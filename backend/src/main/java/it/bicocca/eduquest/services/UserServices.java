package it.bicocca.eduquest.services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.bicocca.eduquest.domain.users.Role;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.users.Teacher;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.dto.user.UserInfoDTO;
import it.bicocca.eduquest.dto.user.UserLoginDTO;
import it.bicocca.eduquest.dto.user.UserLoginResponseDTO;
import it.bicocca.eduquest.dto.user.UserRegistrationDTO;
import it.bicocca.eduquest.repository.UsersRepository;
import it.bicocca.eduquest.security.JwtUtils;

@Service
public class UserServices {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder; 
    private final JwtUtils jwtUtils; 

    public UserServices(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public UserInfoDTO registerUser(UserRegistrationDTO dto) {
        if (usersRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered!");
        }

        User newUser;
        if (dto.getRole() == Role.TEACHER) {
            newUser = new Teacher(dto.getName(), dto.getSurname(), dto.getEmail(), dto.getPassword());
        } else {
        	newUser = new Student(dto.getName(), dto.getSurname(), dto.getEmail(), dto.getPassword());
        }

        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = usersRepository.save(newUser);
        
        return new UserInfoDTO(
        		savedUser.getId(),
        		savedUser.getName(),
        		savedUser.getSurname(),
        		savedUser.getEmail(),
        		savedUser.getRole()
        	);
    }
    
    public UserLoginResponseDTO loginUser(UserLoginDTO dto) {
        User user = usersRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password errata");
        }

        String token = jwtUtils.generateToken(user.getId());
        
        return new UserLoginResponseDTO(token, user.getId(), user.getRole());
    }
    
    public UserInfoDTO getUserInfo(long id) {
    	User user = usersRepository.findById(id)
    			.orElseThrow(() -> new IllegalArgumentException("User not found"));
    	
    	return new UserInfoDTO(
        		user.getId(),
        		user.getName(),
        		user.getSurname(),
        		user.getEmail(),
        		user.getRole()
        	);
    }
    
    public UserInfoDTO getUserInfoFromJwt(String jwt) {
    	long userId = jwtUtils.getUserIdFromToken(jwt);
    	return getUserInfo(userId);
    }

	public List<UserInfoDTO> getAllUsers() {
		return usersRepository.findAll()
				.stream()
				.map(user -> new UserInfoDTO(
								user.getId(),
								user.getName(),
								user.getSurname(),
								user.getEmail(),
								user.getRole()
								))
				.toList();
	}
}
