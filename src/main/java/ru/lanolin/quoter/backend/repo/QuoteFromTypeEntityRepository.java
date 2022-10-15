package ru.lanolin.quoter.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanolin.quoter.backend.domain.QuoteFromTypeEntity;

public interface QuoteFromTypeEntityRepository extends JpaRepository<QuoteFromTypeEntity, Integer> {
}