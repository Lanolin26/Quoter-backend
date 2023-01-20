package ru.lanolin.quoter.backend.domain.view;

import ru.lanolin.quoter.backend.domain.QuoteEntity;

/**
 * A Projection for the {@link QuoteEntity} entity
 */
public interface QuoteEntityInfo {

    Integer getId();
    String getText();
    String getSourceName();
    String getSourceType();
    String getAuthorName();
    String getAuthorLogin();

}