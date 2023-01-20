package ru.lanolin.quoter.backend.domain.view;

import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.domain.UserEntity;

import java.util.Optional;

public class QuoteEntityIdsInfoImpl implements QuoteEntityIdsInfo {

    private final QuoteEntity quoteEntity;

    public QuoteEntityIdsInfoImpl(QuoteEntity quoteEntity) {
        this.quoteEntity = quoteEntity;
    }

    public QuoteEntityIdsInfoImpl(Integer id, String text, Integer sourceId, Integer authorId) {
        this.quoteEntity = new QuoteEntity(id, text, new UserEntity(authorId), new QuoteSource(sourceId));
    }

    @Override
    public Integer getId() {
        return quoteEntity.getId();
    }

    @Override
    public Integer getSourceId() {
        return Optional.ofNullable(quoteEntity)
                .map(QuoteEntity::getSource)
                .map(QuoteSource::getId)
                .orElse(null);
    }

    @Override
    public Integer getSourceTypeId() {
        return Optional.ofNullable(quoteEntity)
                .map(QuoteEntity::getSource)
                .map(QuoteSource::getType)
                .map(QuoteSourceType::getId)
                .orElse(null);
    }

    @Override
    public Integer getAuthorId() {
        return Optional.ofNullable(quoteEntity)
                .map(QuoteEntity::getAuthor)
                .map(UserEntity::getId)
                .orElse(null);
    }

    @Override
    public String getText() {
        return quoteEntity.getText();
    }
}
