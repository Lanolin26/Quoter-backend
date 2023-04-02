package ru.lanolin.quoter.backend.domain.validators;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.service.QuoteSourceTypeService;

@Component
public class QuoteSourceTypeValidator implements CustomValidator {

    private static final Class<QuoteSourceType> QUOTE_SOURCE_TYPE_CLASS = QuoteSourceType.class;

    private QuoteSourceTypeService quoteSourceTypeService;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return QUOTE_SOURCE_TYPE_CLASS.isAssignableFrom(clazz);
    }

    @Override
    public void validateTransient(@NonNull Object target, @NonNull Errors errors) {
        QuoteSourceType type = (QuoteSourceType) target;
        checkId(errors, type);
    }

    private void checkId(Errors errors, QuoteSourceType type) {
        Integer id = type.getId();
        CustomValidatorUtils.rejectIntegerIfLessZero(errors, id, "id", "null", "Incorrect value");
        if (!quoteSourceTypeService.existById(id)) {
            errors.rejectValue("id", "exist", "Not exist");
        }
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "empty", "Empty value");
    }

    @Autowired
    @Lazy
    public void setQuoteSourceTypeService(QuoteSourceTypeService quoteSourceTypeService) {
        this.quoteSourceTypeService = quoteSourceTypeService;
    }
}
