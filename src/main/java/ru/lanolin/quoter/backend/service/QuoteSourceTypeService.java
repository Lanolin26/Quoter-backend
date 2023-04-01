package ru.lanolin.quoter.backend.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.IdentificationClass;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.exceptions.domain.CheckArgs;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.repo.QuoteSourceTypeRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class QuoteSourceTypeService {

    private final List<CheckArgs<QuoteSourceType>> ARGS_LIST = List.of(
            new CheckArgs<>(Objects::isNull, "class", "null", "Entity mustn't be a null"),
            new CheckArgs<>(e -> Objects.isNull(e.getType()) || e.getType().isEmpty() || e.getType().isBlank(),
                    "type", "null", "Not valid null value")
    );
    private final QuoteSourceTypeRepository repo;

    @Autowired
    public QuoteSourceTypeService(QuoteSourceTypeRepository repo) {
        this.repo = repo;
    }

    public Page<QuoteSourceType> findAll(Pageable page) {
        return repo.findAll(page);
    }

    public List<QuoteSourceType> findAll() {
        return repo.findAll();
    }

    public long count() {
        return repo.count();
    }

    public Optional<QuoteSourceType> getOne(Integer id) {
        return repo.findById(id);
    }

    public boolean existById(Integer id) {
        return repo.existsById(id);
    }

    public boolean exist(QuoteSourceType e) {
        return Objects.nonNull(e) && Objects.nonNull(e.getId()) && existById(e.getId());
    }

    /**
     * <p>Сохраняет элемент в базу данных.</p>
     * <p>
     * <b>!При наличии вложенных entity - возвращается объект с заполненным полем {@link IdentificationClass#id}!</b>
     * </p>
     *
     * @param entity {@link QuoteSourceType} объект сохранения
     * @return сохраненный объект с выданным {@link IdentificationClass#id}.
     */
    public QuoteSourceType create(@NonNull QuoteSourceType entity) throws IncorrectField {
        checkCorrect(entity);
        entity.setId(null);
        return repo.save(entity);
    }

    /**
     * Обновляет элемент по его ID.
     *
     * <p>
     * <b>!При наличии вложенных entity - возвращается объект с заполненным полем {@link IdentificationClass#id}!</b>
     * </p>
     *
     * @param id     {@link Integer}
     * @param entity {@link QuoteSourceType}
     * @return обновленный элемента
     */
    public QuoteSourceType update(Integer id, @NonNull QuoteSourceType entity) throws IncorrectField {
        checkCorrect(entity);
        Optional<QuoteSourceType> inDb = repo.findById(id);
        if (inDb.isEmpty()) {
            return create(entity);
        } else {
            QuoteSourceType inDbEntity = inDb.get();
            copyProperties(entity, inDbEntity);
            return repo.save(inDbEntity);
        }
    }

    public void delete(QuoteSourceType entity) {
        repo.delete(entity);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    @SuppressWarnings({"unchecked"})
    public void checkCorrect(QuoteSourceType entity) throws IncorrectField {
        for (CheckArgs<QuoteSourceType> eCheckArgs : ARGS_LIST) {
            if (eCheckArgs.func().test(entity)) {
                throw eCheckArgs.createException((Class<QuoteSourceType>) entity.getClass());
            }
        }
    }

    public void copyProperties(QuoteSourceType entity, QuoteSourceType inDbEntity) {
        inDbEntity.setType(entity.getType());
    }

    public Optional<QuoteSourceType> search(String query) {
        return repo.searchQuoteSourceTypeByType("%" + query + "%");
    }
}
