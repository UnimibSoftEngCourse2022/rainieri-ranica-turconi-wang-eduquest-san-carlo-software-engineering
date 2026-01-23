package it.bicocca.eduquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.bicocca.eduquest.domain.users.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    // Qui Spring creerà automaticamente i metodi per salvare, cercare e cancellare gli utenti.
}