package ru.lanolin.quoter.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.UserRoles;
import ru.lanolin.quoter.backend.exceptions.domain.CheckArgs;
import ru.lanolin.quoter.backend.repo.UserEntityRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserEntityService implements RestApi<UserEntity, Integer> {

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

	@Override
	@SuppressWarnings({ "unchecked" })
	public UserEntityRepository getRepo() {
		return repo;
	}

	@Override
	public void copyProperties(UserEntity entity, UserEntity inDbEntity) {
		inDbEntity.setName(entity.getName());
		inDbEntity.setLogin(entity.getLogin());
		inDbEntity.setPassword(entity.getPassword());
		inDbEntity.setImg(entity.getImg());
		inDbEntity.getRoles().clear();
		inDbEntity.getRoles().addAll(inDbEntity.getRoles());

		if (entity.getRoles().size()==0) {
			entity.getRoles().add(UserRoles.GUEST);
		}
	}

	@Override
	public List<CheckArgs<UserEntity>> checks() {
		return ARGS_LIST;
	}
}
