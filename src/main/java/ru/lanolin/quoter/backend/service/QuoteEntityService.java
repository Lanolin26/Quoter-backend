package ru.lanolin.quoter.backend.service;

import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.repo.QuoteEntityRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@Service
public class QuoteEntityService implements RestApi<QuoteEntity, Integer> {

	private final QuoteEntityRepository repo;


	public QuoteEntityService(QuoteEntityRepository repo) {
		this.repo = repo;
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public QuoteEntityRepository getRepo() {
		return repo;
	}

	@Override
	public List<QuoteEntity> findAll() {
		return repo.findAll();
	}

	@Override
	public Optional<QuoteEntity> getOne(Integer id) {
		return repo.findById(id);
	}

	@Override
	public QuoteEntity create(@NonNull QuoteEntity entity) {
		repo.save(entity);
		return entity;
	}

	@Override
	public QuoteEntity update(Integer id, QuoteEntity entity) {
		Optional<QuoteEntity> inDb = getOne(id);
		if (inDb.isEmpty()) {
			return create(entity);
		} else {
			QuoteEntity inDbEntity = inDb.get();
			BeanUtils.copyProperties(entity, inDbEntity, "id");
			inDbEntity.setText(
					Optional.ofNullable(inDbEntity.getText())
							.map(text -> text.replaceAll("&" + "nbsp;", " "))
							.map(text -> text.replaceAll(String.valueOf((char) 160), " "))
							.orElse(null));
			repo.save(inDbEntity);
			return inDbEntity;
		}
	}

	@Override
	public void delete(QuoteEntity entity) {
		deleteById(entity.getId());
	}

	@Override
	public void deleteById(Integer id) {
		repo.deleteById(id);
	}

}
