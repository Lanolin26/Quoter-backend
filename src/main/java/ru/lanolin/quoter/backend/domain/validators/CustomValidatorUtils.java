package ru.lanolin.quoter.backend.domain.validators;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

@CommonsLog
public class CustomValidatorUtils extends ValidationUtils {

    public static void invokeTransitValidator(CustomValidator validator, Object target, Errors errors) {
        invokeTransitValidator(validator, target, errors, (Object[]) null);
    }

    public static void invokeTransitValidator(CustomValidator validator,
                                              Object target,
                                              Errors errors,
                                              @Nullable Object... validationHints) {

        Assert.notNull(validator, "Validator must not be null");
        Assert.notNull(target, "Target object must not be null");
        Assert.notNull(errors, "Errors object must not be null");

        if (log.isDebugEnabled()) {
            log.debug("Invoking validator [" + validator + "]");
        }
        if (!validator.supports(target.getClass())) {
            throw new IllegalArgumentException(
                    "Validator [" + validator.getClass() + "] does not support [" + target.getClass() + "]");
        }

        if (!ObjectUtils.isEmpty(validationHints) && validator instanceof SmartValidator) {
            ((SmartValidator) validator).validate(target, errors, validationHints);
        } else {
            validator.validateTransient(target, errors);
        }

        if (log.isDebugEnabled()) {
            if (errors.hasErrors()) {
                log.debug("Validator found " + errors.getErrorCount() + " errors");
            } else {
                log.debug("Validator found no errors");
            }
        }
    }

    public static void rejectIfEmptyOrWhitespace(Errors errors,
                                                 Object value,
                                                 String field,
                                                 String errorCode,
                                                 String defaultMessage) {
        Assert.notNull(errors, "Errors object must not be null");
        if (value == null ||!StringUtils.hasText(value.toString())) {
            errors.rejectValue(field, errorCode, defaultMessage);
        }
    }

    public static void rejectIntegerIfLessZero(Errors errors,
                                               Object target,
                                               String field,
                                               String errorCode,
                                               String defaultMessage) {
        Assert.notNull(errors, "Errors object must not be null");
        Integer intVal = (Integer) target;
        if (intVal == null || intVal <= 0) {
            errors.rejectValue(field, errorCode, defaultMessage);
        }
    }
}
