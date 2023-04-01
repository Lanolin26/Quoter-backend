package ru.lanolin.quoter.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanolin.quoter.backend.domain.QuoteSource;

import java.util.Optional;

public interface QuoteSourceRepository extends JpaRepository<QuoteSource, Integer> {

    Optional<QuoteSource> searchQuoteSourceBySourceName(String query);

}