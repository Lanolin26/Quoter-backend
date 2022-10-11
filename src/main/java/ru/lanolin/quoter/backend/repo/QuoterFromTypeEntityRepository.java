package ru.lanolin.quoter.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanolin.quoter.backend.domain.QuoterFromTypeEntity;

public interface QuoterFromTypeEntityRepository extends JpaRepository<QuoterFromTypeEntity, Integer> {
}