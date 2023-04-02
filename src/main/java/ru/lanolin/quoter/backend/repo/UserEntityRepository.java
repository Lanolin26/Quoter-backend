package ru.lanolin.quoter.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanolin.quoter.backend.domain.UserEntity;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByLogin(String login);

}