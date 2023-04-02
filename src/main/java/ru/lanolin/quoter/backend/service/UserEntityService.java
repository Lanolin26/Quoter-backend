package ru.lanolin.quoter.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.UserRoles;
import ru.lanolin.quoter.backend.domain.validators.UserValidator;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.repo.UserEntityRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserEntityService {

    private final UserEntityRepository repo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserValidator validator;

    private void setPasswordInEntity(UserEntity entity, String password) {
        try{
            bCryptPasswordEncoder.upgradeEncoding(password);
        }catch (IllegalArgumentException ignored) {
            cryptPassword(entity, password);
        }
    }

    private void cryptPassword(UserEntity entity, String password) {
        String encode = bCryptPasswordEncoder.encode(password);
        entity.setPassword(encode);
    }

    public void copyProperties(UserEntity entity, UserEntity inDbEntity) {
        inDbEntity.setName(entity.getName());
        inDbEntity.setLogin(entity.getLogin());
        setPasswordInEntity(inDbEntity, entity.getPassword());
        inDbEntity.setImg(entity.getImg());
        inDbEntity.getRoles().clear();
        inDbEntity.getRoles().addAll(entity.getRoles());

        if (inDbEntity.getRoles().size() == 0) {
            inDbEntity.getRoles().add(UserRoles.GUEST);
        }
    }


    public Page<UserEntity> findAll(Pageable page) {
        return repo.findAll(page);
    }

    public List<UserEntity> findAll() {
        return repo.findAll();
    }

    public Optional<UserEntity> findByLogin(String login) {
        return repo.findByLogin(login);
    }

    public long count() {
        return repo.count();
    }

    public Optional<UserEntity> getOne(Integer id) {
        return repo.findById(id);
    }

    public boolean existById(Integer id) {
        return repo.existsById(id);
    }

    public boolean exist(UserEntity e) {
        return Objects.nonNull(e) && Objects.nonNull(e.getId()) && existById(e.getId());
    }

    public UserEntity create(@NonNull UserEntity entity) throws IncorrectField {
        checkCorrect(entity);
        entity.setId(null);
        setPasswordInEntity(entity, entity.getPassword());
        return repo.save(entity);
    }

    public UserEntity update(Integer id, @NonNull UserEntity entity) throws IncorrectField {
        checkCorrect(entity);
        Optional<UserEntity> inDb = repo.findById(id);
        if (inDb.isEmpty()) {
            return create(entity);
        } else {
            UserEntity inDbEntity = inDb.get();
            copyProperties(entity, inDbEntity);
            return repo.save(inDbEntity);
        }
    }

    public void delete(UserEntity entity) {
        repo.delete(entity);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    private void checkCorrect(UserEntity entity) throws IncorrectField {
        DataBinder dataBinder = new DataBinder(entity, "user");
        dataBinder.addValidators(validator);
        dataBinder.setDisallowedFields("password");
        dataBinder.validate();
        BindingResult bindingResult = dataBinder.getBindingResult();
        if(bindingResult.hasErrors()) {
            throw new IncorrectField(new BindException(bindingResult));
        }
    }

}
