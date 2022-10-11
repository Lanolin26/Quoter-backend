package ru.lanolin.quoter.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanolin.quoter.backend.domain.QuoteEntity;

public interface QuoteEntityRepository extends JpaRepository<QuoteEntity, Integer> {
}