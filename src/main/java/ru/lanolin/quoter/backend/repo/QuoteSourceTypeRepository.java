package ru.lanolin.quoter.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;

import java.util.Optional;

public interface QuoteSourceTypeRepository extends JpaRepository<QuoteSourceType, Integer> {

    Optional<QuoteSourceType> searchQuoteSourceTypeByType(String query);

}