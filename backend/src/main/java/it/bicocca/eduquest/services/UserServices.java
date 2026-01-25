package it.bicocca.eduquest.services;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; 
import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.dto.user.*;
import it.bicocca.eduquest.repository.UsersRepository;
import it.bicocca.eduquest.security.JwtUtils;

import java.util.Optional;

@Service
public class UserServices {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder; 
    private final JwtUtils jwtUtils; 

    // Adding passwordEncoder and jwtUtils  
    public UserServices(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // registration
    public User registerUser(UserRegistrationDTO dto) {
        if (usersRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }

        // role
        User newUser;
        if (dto.getRole() == Role.TEACHER) {
            newUser = new Teacher(dto.getName(), dto.getSurname(), dto.getEmail(), dto.getPassword());
        } else {
        	newUser = new Student(dto.getName(), dto.getSurname(), dto.getEmail(), dto.getPassword());
        }

        // password crypting
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        return usersRepository.save(newUser);
    }
    
    // login
    public UserLoginResponseDTO loginUser(UserLoginDTO dto) {
        User user = usersRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password errata");
        }

        // Token JwtUtils
        String token = jwtUtils.generateToken(user.getId());
        
        return new UserLoginResponseDTO(token, user.getId(), user.getRole());
    }
    
}

