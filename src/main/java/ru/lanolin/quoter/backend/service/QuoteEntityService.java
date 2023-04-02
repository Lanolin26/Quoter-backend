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
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.validators.QuoteEntityValidator;
import ru.lanolin.quoter.backend.domain.view.QuoteEntityIdsInfo;
import ru.lanolin.quoter.backend.domain.view.QuoteEntityInfo;
import ru.lanolin.quoter.backend.exceptions.NotFoundException;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.repo.QuoteEntityRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class QuoteEntityService {

    private final QuoteEntityRepository repo;
    private final QuoteSourceService quoteSourceService;
    private final UserEntityService userService;
    private final QuoteEntityValidator validator;

    public void copyProperties(QuoteEntity entity, QuoteEntity inDbEntity) {
        inDbEntity.setSource(new QuoteSource(entity.getSource().getId()));
        inDbEntity.setAuthor(new UserEntity(entity.getAuthor().getId()));
        inDbEntity.setText(
                Optional.ofNullable(entity.getText())
                        .map(text -> text.replaceAll("&" + "nbsp;", " "))
                        .map(text -> text.replaceAll(String.valueOf((char) 160), " "))
                        .orElse(null)
        );
    }

    public List<QuoteEntityInfo> search(String query) {
        return repo.searchQuoteEntityByText("%" + query + "%");
    }

    public Page<QuoteEntity> findAll(Pageable page) {
        return repo.findAll(page);
    }

    public List<QuoteEntity> findAll() {
        return repo.findAll();
    }

    public long count() {
        return repo.count();
    }

    public Optional<QuoteEntity> getOne(Integer id) {
        return repo.findById(id);
    }

    public Page<QuoteEntityInfo> findInfoAll(Pageable page) {
        return repo.getQuoteEntityInfo(page);
    }

    public List<QuoteEntityInfo> findInfoAll() {
        return repo.getQuoteEntityInfo();
    }

    public Optional<QuoteEntityInfo> getOneInfo(Integer id) {
        return repo.getQuoteEntityInfoById(id);
    }

    public Page<QuoteEntityIdsInfo> findIdsInfoAll(Pageable page) {
        return repo.getQuoteEntityIdsInfo(page);
    }

    public List<QuoteEntityIdsInfo> findIdsInfoAll() {
        return repo.getQuoteEntityIdsInfo();
    }

    public Optional<QuoteEntityIdsInfo> getOneIdsInfo(Integer id) {
        return repo.getQuoteEntityIdsInfoById(id);
    }

    public boolean existById(Integer id) {
        return repo.existsById(id);
    }

    public boolean exist(QuoteEntity e) {
        return Objects.nonNull(e) && Objects.nonNull(e.getId()) && existById(e.getId());
    }

    public QuoteEntityIdsInfo create(QuoteEntityIdsInfo entity) throws IncorrectField {
        QuoteEntity toCreate = createEntityById(entity);
        checkCorrect(toCreate);
        QuoteEntity save = repo.save(toCreate);
        return getOneIdsInfo(save.getId()).orElseThrow(NotFoundException::new);
    }

    public QuoteEntityIdsInfo update(Integer id, @NonNull QuoteEntityIdsInfo entity) throws IncorrectField {
        QuoteEntity toUpdate = createEntityById(entity);
        checkCorrect(toUpdate);
        Optional<QuoteEntity> inDb = repo.findById(id);
        if (inDb.isEmpty()) {
            return create(entity);
        } else {
            QuoteEntity inDbEntity = inDb.get();
            copyProperties(toUpdate, inDbEntity);
            QuoteEntity updated = repo.save(inDbEntity);
            return getOneIdsInfo(updated.getId()).orElseThrow(NotFoundException::new);
        }
    }

    private static QuoteEntity createEntityById(QuoteEntityIdsInfo entity) {
        QuoteEntity toUpdate = new QuoteEntity();
        toUpdate.setId(null);
        toUpdate.setText(entity.getText());
        toUpdate.setAuthor(new UserEntity(entity.getAuthorId()));
        toUpdate.setSource(new QuoteSource(entity.getSourceId()));
        return toUpdate;
    }

    public QuoteEntity create(@NonNull QuoteEntity entity) throws IncorrectField {
        checkCorrect(entity);
        entity.setId(null);
        QuoteEntity save = repo.save(entity);
        return getOne(save.getId()).orElseThrow(NotFoundException::new);
    }

    public QuoteEntity update(Integer id, @NonNull QuoteEntity entity) throws IncorrectField {
        checkCorrect(entity);
        Optional<QuoteEntity> inDb = repo.findById(id);
        if (inDb.isEmpty()) {
            return create(entity);
        } else {
            QuoteEntity inDbEntity = inDb.get();
            copyProperties(entity, inDbEntity);
            return repo.save(inDbEntity);
        }
    }

    public void delete(QuoteEntity entity) {
        repo.delete(entity);
    }

    public void delete(QuoteEntityIdsInfo entity) {
        deleteById(entity.getId());
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    public void checkCorrect(QuoteEntity entity) throws IncorrectField {
        DataBinder dataBinder = new DataBinder(entity, "quote");
        dataBinder.addValidators(validator);
        dataBinder.validate();
        BindingResult bindingResult = dataBinder.getBindingResult();
        if(bindingResult.hasErrors()) {
            throw new IncorrectField(new BindException(bindingResult));
        }
    }

}
