package ru.lanolin.quoter.backend.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.IdentificationClass;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.UserRoles;
import ru.lanolin.quoter.backend.exceptions.domain.CheckArgs;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.repo.UserEntityRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserEntityService {

    private final List<CheckArgs<UserEntity>> ARGS_LIST = List.of(
            new CheckArgs<>(Objects::isNull, "class", "null", "Entity mustn't be a null"),
            new CheckArgs<>(e -> Objects.isNull(e.getName()) || e.getName().isEmpty() || e.getName().isBlank(),
                    "name", "empty", "Not available empty value"),
            new CheckArgs<>(e -> Objects.isNull(e.getLogin()) || e.getLogin().isEmpty() || e.getLogin().isBlank(),
                    "login", "empty", "Not available empty value"),
            new CheckArgs<>(e -> Objects.isNull(e.getPassword()) || e.getPassword().isEmpty() || e.getPassword().isBlank(),
                    "password", "empty", "Not available empty value"),
            new CheckArgs<>(e -> Objects.isNull(e.getRoles()),
                    "roles", "empty", "Not available empty value"),
            new CheckArgs<>(e -> Optional.ofNullable(e).map(UserEntity::getPassword).filter(s -> s.length() >= 6).isEmpty(),
                    "password", "string", "Minimal length required 6 symbols")
    );

    private final UserEntityRepository repo;

    @Autowired
    public UserEntityService(UserEntityRepository repo) {
        this.repo = repo;
    }

    public void copyProperties(UserEntity entity, UserEntity inDbEntity) {
        inDbEntity.setName(entity.getName());
        inDbEntity.setLogin(entity.getLogin());
        inDbEntity.setPassword(entity.getPassword());
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

    /**
     * <p>Сохраняет элемент в базу данных.</p>
     * <p>
     * <b>!При наличии вложенных entity - возвращается объект с заполненным полем {@link IdentificationClass#id}!</b>
     * </p>
     *
     * @param entity {@link UserEntity} объект сохранения
     * @return сохраненный объект с выданным {@link IdentificationClass#id}.
     */
    public UserEntity create(@NonNull UserEntity entity) throws IncorrectField {
        checkCorrect(entity);
        entity.setId(null);
        return repo.save(entity);
    }

    /**
     * Обновляет элемент по его Integer.
     *
     * <p>
     * <b>!При наличии вложенных entity - возвращается объект с заполненным полем {@link IdentificationClass#id}!</b>
     * </p>
     *
     * @param id     {@link Integer}
     * @param entity {@link UserEntity}
     * @return обновленный элемента
     */
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

    @SuppressWarnings({"unchecked"})
    public void checkCorrect(UserEntity entity) throws IncorrectField {
        for (CheckArgs<UserEntity> eCheckArgs : ARGS_LIST) {
            if (eCheckArgs.func().test(entity)) {
                throw eCheckArgs.createException((Class<UserEntity>) entity.getClass());
            }
        }
    }

}
