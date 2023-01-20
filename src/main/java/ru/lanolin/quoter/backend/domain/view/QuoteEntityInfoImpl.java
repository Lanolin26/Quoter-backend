package ru.lanolin.quoter.backend.domain.view;

import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.domain.UserEntity;

import java.util.Optional;

public class QuoteEntityInfoImpl implements QuoteEntityInfo {

    private final QuoteEntity quoteEntity;

    public QuoteEntityInfoImpl(QuoteEntity quoteEntity) {
        this.quoteEntity = quoteEntity;
    }

    @Override
    public Integer getId() {
        return quoteEntity.getId();
    }

    @Override
    public String getText() {
        return quoteEntity.getText();
    }

    @Override
    public String getSourceName() {
        return Optional.ofNullable(quoteEntity)
                .map(QuoteEntity::getSource)
                .map(QuoteSource::getSourceName)
                .orElse(null);
    }

    @Override
    public String getSourceType() {
        return Optional.ofNullable(quoteEntity)
                .map(QuoteEntity::getSource)
                .map(QuoteSource::getType)
                .map(QuoteSourceType::getType)
                .orElse(null);
    }

    @Override
    public String getAuthorName() {
        return Optional.ofNullable(quoteEntity)
                .map(QuoteEntity::getAuthor)
                .map(UserEntity::getName)
                .orElse(null);
    }

    @Override
    public String getAuthorLogin() {
        return Optional.ofNullable(quoteEntity)
                .map(QuoteEntity::getAuthor)
                .map(UserEntity::getLogin)
                .orElse(null);
    }
}
