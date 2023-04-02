package ru.lanolin.quoter.backend.domain.validators;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.service.QuoteSourceService;

import java.util.Objects;

@Component
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class QuoteSourceValidator implements CustomValidator {

    private static final Class<QuoteSource> QUOTE_SOURCE_CLASS = QuoteSource.class;

    private final QuoteSourceTypeValidator quoteSourceTypeValidator;
    private QuoteSourceService quoteSourceService;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return QUOTE_SOURCE_CLASS.isAssignableFrom(clazz);
    }

    @Override
    public void validateTransient(@NonNull Object target, @NonNull Errors errors) {
        QuoteSource source = (QuoteSource) target;
        checkId(errors, source);
    }

    private void checkId(Errors errors, QuoteSource type) {
        Integer id = type.getId();
        CustomValidatorUtils.rejectIntegerIfLessZero(errors, id, "id", "null", "Incorrect value");
        if (!quoteSourceService.existById(id)) {
            errors.rejectValue("id", "exist", "Not exist");
        }
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        QuoteSource source = (QuoteSource) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceName", "empty", "Empty value");

        checkType(errors, source);
    }

    private void checkType(Errors errors, QuoteSource source) {
        QuoteSourceType type = source.getType();
        if(Objects.isNull(type)) {
            errors.rejectValue("type", "null", "QuoteSourceType is null");
            return;
        }
        CustomValidatorUtils.invokeTransitValidator(quoteSourceTypeValidator, type, errors);
    }

    @Autowired
    @Lazy
    public void setQuoteSourceService(QuoteSourceService quoteSourceService) {
        this.quoteSourceService = quoteSourceService;
    }
}
