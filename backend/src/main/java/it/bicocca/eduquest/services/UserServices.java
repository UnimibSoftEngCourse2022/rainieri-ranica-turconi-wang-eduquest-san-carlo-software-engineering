package it.bicocca.eduquest.services;

import org.springframework.stereotype.Service;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.repository.UsersRepository;
import java.util.List;

@Service
public class UserServices {

    private final UsersRepository usersRepository;

    public UserServices(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User registerUser(User newUser) {
        if (usersRepository.existsByEmail(newUser.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }
        return usersRepository.save(newUser);
    }
    
    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }
}