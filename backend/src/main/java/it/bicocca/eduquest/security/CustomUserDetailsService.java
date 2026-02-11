package it.bicocca.eduquest.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.repository.UsersRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));

        Role roleEnum; 
        
        if (user instanceof Teacher) {
            roleEnum = Role.TEACHER;
        } else if (user instanceof Student) {
            roleEnum = Role.STUDENT;
        } else {
            throw new UsernameNotFoundException("Ruolo non definito per questo utente");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(roleEnum.name()) 
                .build();
    }
    
}