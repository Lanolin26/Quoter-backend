package ru.lanolin.quoter.backend.service;

import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.repo.UserEntityRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@Service
public class UserEntityService implements RestApi<UserEntity, Integer> {

	private final UserEntityRepository repo;

	@Autowired
	public UserEntityService(UserEntityRepository repo) {
		this.repo = repo;
	}

	@Override
	public List<UserEntity> findAll() {
		return repo.findAll();
	}

	@Override
	public Optional<UserEntity> getOne(Integer id) {
		return repo.findById(id);
	}

	@Override
	public UserEntity create(@NonNull UserEntity entity) {
		repo.save(entity);
		return entity;
	}

	@Override
	public UserEntity update(Integer id, UserEntity entity) {
		Optional<UserEntity> inDb = getOne(id);
		if (inDb.isEmpty()) {
			return create(entity);
		} else {
			UserEntity inDbEntity = inDb.get();
			BeanUtils.copyProperties(entity, inDbEntity, "id");
			return inDbEntity;
		}
	}

	@Override
	public void delete(UserEntity entity) {
		deleteById(entity.getId());
	}

	@Override
	public void deleteById(Integer id) {
		repo.deleteById(id);
	}

}
