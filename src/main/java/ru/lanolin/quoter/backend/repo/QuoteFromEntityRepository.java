package ru.lanolin.quoter.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanolin.quoter.backend.domain.QuoteFromEntity;

public interface QuoteFromEntityRepository extends JpaRepository<QuoteFromEntity, Integer> {
}