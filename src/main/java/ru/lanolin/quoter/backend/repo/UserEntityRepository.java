package ru.lanolin.quoter.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanolin.quoter.backend.domain.UserEntity;

public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {
}