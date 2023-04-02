package ru.lanolin.quoter.backend.domain.validators;

import lombok.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public interface CustomValidator extends Validator {

    void validateTransient(@NonNull Object target, @NonNull Errors errors);

}
