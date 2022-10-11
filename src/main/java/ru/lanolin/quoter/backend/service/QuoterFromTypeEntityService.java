package ru.lanolin.quoter.backend.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoterFromTypeEntity;
import ru.lanolin.quoter.backend.repo.QuoterFromTypeEntityRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@Service
public class QuoterFromTypeEntityService implements RestApi<QuoterFromTypeEntity, Integer> {

	private final QuoterFromTypeEntityRepository repo;

	@Autowired
	public QuoterFromTypeEntityService(QuoterFromTypeEntityRepository repo) {
		this.repo = repo;
	}


	@Override
	public List<QuoterFromTypeEntity> findAll() {
		return repo.findAll();
	}

	@Override
	public Optional<QuoterFromTypeEntity> getOne(Integer id) {
		return repo.findById(id);
	}

	@Override
	public QuoterFromTypeEntity create(QuoterFromTypeEntity entity) {
		repo.save(entity);
		return entity;
	}

	@Override
	public QuoterFromTypeEntity update(Integer id, QuoterFromTypeEntity entity) {
		Optional<QuoterFromTypeEntity> inDb = getOne(id);
		if (inDb.isEmpty()) {
			return create(entity);
		} else {
			QuoterFromTypeEntity inDbEntity = inDb.get();
			BeanUtils.copyProperties(entity, inDbEntity, "id");
			return inDbEntity;
		}
	}

	@Override
	public void delete(QuoterFromTypeEntity entity) {
		deleteById(entity.getId());
	}

	@Override
	public void deleteById(Integer id) {
		repo.deleteById(id);
	}
}
