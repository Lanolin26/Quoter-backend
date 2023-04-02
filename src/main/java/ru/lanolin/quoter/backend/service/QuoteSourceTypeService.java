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
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.domain.validators.QuoteSourceTypeValidator;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.repo.QuoteSourceTypeRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class QuoteSourceTypeService {

    private final QuoteSourceTypeRepository repo;
    private final QuoteSourceTypeValidator validator;

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

    public QuoteSourceType create(@NonNull QuoteSourceType entity) throws IncorrectField {
        checkCorrect(entity);
        entity.setId(null);
        return repo.save(entity);
    }

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

    private void checkCorrect(QuoteSourceType entity) throws IncorrectField {
        DataBinder dataBinder = new DataBinder(entity, "type");
        dataBinder.addValidators(validator);
        dataBinder.validate();
        BindingResult bindingResult = dataBinder.getBindingResult();
        if (bindingResult.hasErrors()) {
            throw new IncorrectField(new BindException(bindingResult));
        }
    }

    public void copyProperties(QuoteSourceType entity, QuoteSourceType inDbEntity) {
        inDbEntity.setType(entity.getType());
    }

    public Optional<QuoteSourceType> search(String query) {
        return repo.searchQuoteSourceTypeByType("%" + query + "%");
    }
}
