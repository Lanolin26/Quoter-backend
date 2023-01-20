package ru.lanolin.quoter.backend.domain.view;

/**
 * A Projection for the {@link ru.lanolin.quoter.backend.domain.QuoteEntity} entity
 */
public interface QuoteEntityIdsInfo {
    Integer getId();

    Integer getSourceId();

    Integer getSourceTypeId();

    Integer getAuthorId();

    String getText();
}