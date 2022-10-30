package ru.lanolin.quoter.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;

public interface QuoteSourceTypeRepository extends JpaRepository<QuoteSourceType, Integer> {
}