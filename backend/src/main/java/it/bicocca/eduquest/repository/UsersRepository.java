package it.bicocca.eduquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.bicocca.eduquest.domain.users.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    //Automatic creation of method for save, create and delete users by Spring
}