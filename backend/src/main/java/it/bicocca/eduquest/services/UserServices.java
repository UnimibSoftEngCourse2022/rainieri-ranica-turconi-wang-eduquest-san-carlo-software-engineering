package it.bicocca.eduquest.services;

import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.dto.UserDTO;
import it.bicocca.eduquest.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServices {

    @Autowired
    private UsersRepository usersRepository;

    public UserDTO createUser(String name, String email) {
        // Logica di business: creiamo l'utente
        User newUser = new User(name, "CognomeDefault", email, "passwordSegreta");
        usersRepository.save(newUser);
        
        // Ritorniamo il DTO
        return new UserDTO(newUser.getName(), newUser.getEmail());
    }
}