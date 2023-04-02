package ru.lanolin.quoter.backend.domain.validators;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.service.UserEntityService;

import static java.util.Objects.isNull;

@Component
public class UserValidator implements CustomValidator {

    private static final Class<UserEntity> USER_ENTITY_CLASS = UserEntity.class;

    private UserEntityService userEntityService;


    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return USER_ENTITY_CLASS.isAssignableFrom(clazz);
    }

    @Override
    public void validateTransient(@NonNull Object target, @NonNull Errors errors) {
        UserEntity userEntity = (UserEntity) target;
        checkId(errors, userEntity);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        UserEntity userEntity = (UserEntity) target;
        checkName(errors, userEntity);
        checkLogin(errors, userEntity);
        checkPassword(errors, userEntity);
        checkRoles(errors, userEntity);
    }

    @Autowired
    @Lazy
    public void setUserEntityService(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    private void checkId(Errors errors, UserEntity userEntity) {
        Integer id = userEntity.getId();
        CustomValidatorUtils.rejectIntegerIfLessZero(errors, id, "id", "null", "Incorrect value");
        if (!userEntityService.existById(id)) {
            errors.rejectValue("id", "exist", "Not exist");
        }
    }

    private void checkRoles(Errors errors, UserEntity userEntity) {
        if(isNull(userEntity.getRoles())){
            errors.rejectValue("roles", "null", "Roles is not empty collections");
        }
    }

    private void checkPassword(Errors errors, UserEntity userEntity) {
        String password = userEntity.getPassword();
        if(isNull(password)){
            errors.rejectValue("password", "null", "Password is required field");
            return;
        }
        CustomValidatorUtils.rejectIfEmptyOrWhitespace(errors, password, "password", "empty", "Not available empty value");
        if(password.length() <= 6) {
            errors.rejectValue("password", "length", "Password length must be minimum 6 symbols");
        }
    }

    private void checkLogin(Errors errors, UserEntity userEntity) {
        String login = userEntity.getLogin();
        if (isNull(login)) {
            errors.rejectValue("login", "null", "Login is required field");
            return;
        }
        CustomValidatorUtils.rejectIfEmptyOrWhitespace(errors, login, "login", "empty", "Not available empty value");
    }

    private void checkName(Errors errors, UserEntity userEntity) {
        String name = userEntity.getName();
        if (isNull(name)) {
            errors.rejectValue("name", "null", "Name is required field");
            return;
        }
        CustomValidatorUtils.rejectIfEmptyOrWhitespace(errors, name, "name", "empty", "Not available empty value");
    }
}
