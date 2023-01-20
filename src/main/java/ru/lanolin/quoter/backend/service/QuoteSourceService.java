package ru.lanolin.quoter.backend.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.exceptions.domain.CheckArgs;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.repo.QuoteSourceRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class QuoteSourceService {

    private final List<CheckArgs<QuoteSource>> ARGS_LIST = List.of(
            new CheckArgs<>(Objects::isNull,
                    "class", "null", "Entity mustn't be a null"),
            new CheckArgs<>(e -> Objects.isNull(e.getSourceName()) || e.getSourceName().isEmpty() || e.getSourceName().isBlank(),
                    "Source_Name", "empty", "Not available empty value"),
            new CheckArgs<>(e -> Objects.isNull(e.getType()) || Objects.isNull(e.getType().getId()),
                    "type", "empty", "Type is empty"),
            new CheckArgs<>(Predicate.not(this::existType),
                    "type", "not_found", "Valid type not found")
    );

    private final QuoteSourceRepository repo;
    private final QuoteSourceTypeService sourceTypeService;

    @Autowired
    public QuoteSourceService(QuoteSourceRepository repo, QuoteSourceTypeService sourceTypeService) {
        this.repo = repo;
        this.sourceTypeService = sourceTypeService;
    }

    private boolean existType(QuoteSource e) {
        return this.sourceTypeService.exist(e.getType());
    }

    public void copyProperties(QuoteSource entity, QuoteSource inDbEntity) {
        inDbEntity.setSourceName(entity.getSourceName());
        inDbEntity.setType(new QuoteSourceType(entity.getType().getId()));
    }

    public Page<QuoteSource> findAll(Pageable page) {
        return repo.findAll(page);
    }

    public List<QuoteSource> findAll() {
        return repo.findAll();
    }

    public long count() {
        return repo.count();
    }

    public Optional<QuoteSource> getOne(Integer id) {
        return repo.findById(id);
    }

    public boolean existById(Integer id) {
        return repo.existsById(id);
    }

    public boolean exist(QuoteSource e) {
        return Objects.nonNull(e) && Objects.nonNull(e.getId()) && existById(e.getId());
    }

    public QuoteSource create(@NonNull QuoteSource entity) throws IncorrectField {
        checkCorrect(entity);
        entity.setId(null);
        return repo.save(entity);
    }

    public QuoteSource update(Integer id, @NonNull QuoteSource entity) throws IncorrectField {
        checkCorrect(entity);
        Optional<QuoteSource> inDb = repo.findById(id);
        if (inDb.isEmpty()) {
            return create(entity);
        } else {
            QuoteSource inDbEntity = inDb.get();
            copyProperties(entity, inDbEntity);
            return repo.save(inDbEntity);
        }
    }

    public void delete(QuoteSource entity) {
        repo.delete(entity);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    @SuppressWarnings({"unchecked"})
    public void checkCorrect(QuoteSource entity) throws IncorrectField {
        for (CheckArgs<QuoteSource> eCheckArgs : ARGS_LIST) {
            if (eCheckArgs.func().test(entity)) {
                throw eCheckArgs.createException((Class<QuoteSource>) entity.getClass());
            }
        }
    }

}
