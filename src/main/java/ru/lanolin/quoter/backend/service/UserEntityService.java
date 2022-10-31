package ru.lanolin.quoter.backend.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.repo.UserEntityRepository;
import ru.lanolin.quoter.backend.util.RestApi;

@Service
public class UserEntityService implements RestApi<UserEntity, Integer> {

	private final UserEntityRepository repo;

	@Autowired
	public UserEntityService(UserEntityRepository repo) {
		this.repo = repo;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public UserEntityRepository getRepo() {
		return repo;
	}

	@Override
	public void copyProperties(UserEntity entity, UserEntity inDbEntity) {
		BeanUtils.copyProperties(entity, inDbEntity, "id");
	}

}
