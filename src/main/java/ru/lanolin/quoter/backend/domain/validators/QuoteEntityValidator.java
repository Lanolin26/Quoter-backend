package ru.lanolin.quoter.backend.domain.validators;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.service.QuoteEntityService;

import java.util.Objects;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class QuoteEntityValidator implements CustomValidator {

    private static final Class<QuoteEntity> QUOTE_ENTITY_CLASS = QuoteEntity.class;

    private final QuoteSourceValidator quoteSourceValidator;
    private final UserValidator userValidator;
    private QuoteEntityService quoteEntityService;


    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return QUOTE_ENTITY_CLASS.isAssignableFrom(clazz);
    }

    @Override
    public void validateTransient(@NonNull Object target, @NonNull Errors errors) {
        QuoteEntity quoteEntity = (QuoteEntity) target;
        checkId(errors, quoteEntity);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        QuoteEntity quoteEntity = (QuoteEntity) target;

        CustomValidatorUtils.rejectIfEmptyOrWhitespace(errors, quoteEntity.getText(), "text", "empty", "Test is empty");

        checkAuthor(errors, quoteEntity);
        checkSource(errors, quoteEntity);
    }

    private void checkSource(Errors errors, QuoteEntity quoteEntity) {
        QuoteSource source = quoteEntity.getSource();
        if(Objects.isNull(source)) {
            errors.rejectValue("source", "null", "QuoteSource is null");
            return;
        }
        CustomValidatorUtils.invokeTransitValidator(quoteSourceValidator, source, errors);
    }

    private void checkAuthor(Errors errors, QuoteEntity quoteEntity) {
        UserEntity author = quoteEntity.getAuthor();
        if(Objects.isNull(author)) {
            errors.rejectValue("author", "null", "Author is null");
            return;
        }
        CustomValidatorUtils.invokeTransitValidator(userValidator, author, errors);
    }

    private void checkId(Errors errors, @NonNull QuoteEntity quoteEntity) {
        Integer id = quoteEntity.getId();
        CustomValidatorUtils.rejectIntegerIfLessZero(errors, id, "id", "null", "Incorrect value");
        if (!quoteEntityService.existById(id)) {
            errors.rejectValue("id", "exist", "Not exist");
        }
    }

    @Autowired
    @Lazy
    public void setQuoteEntityService(QuoteEntityService quoteEntityService) {
        this.quoteEntityService = quoteEntityService;
    }
}
