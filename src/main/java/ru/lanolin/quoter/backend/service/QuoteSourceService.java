package ru.lanolin.quoter.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.domain.validators.QuoteSourceValidator;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.repo.QuoteSourceRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class QuoteSourceService {

    private final QuoteSourceRepository repo;
    private final QuoteSourceTypeService sourceTypeService;
    private final QuoteSourceValidator validator;

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

    public void checkCorrect(QuoteSource entity) throws IncorrectField {
        DataBinder dataBinder = new DataBinder(entity, "source");
        dataBinder.addValidators(validator);
        dataBinder.validate();
        BindingResult bindingResult = dataBinder.getBindingResult();
        if(bindingResult.hasErrors()) {
            throw new IncorrectField(new BindException(bindingResult));
        }
    }

    public Optional<QuoteSource> search(String query) {
        return repo.searchQuoteSourceBySourceName("%" + query + "%");
    }
}
