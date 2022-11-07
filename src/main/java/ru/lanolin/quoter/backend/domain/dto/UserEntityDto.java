package ru.lanolin.quoter.backend.domain.dto;

import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.UserRoles;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * A DTO for the {@link ru.lanolin.quoter.backend.domain.UserEntity} entity
 */
public record UserEntityDto(
		Integer id,
		String login,
		String name,
		String password,
		String img,
		Set<UserRoles> roles
) implements Serializable, ConverterDtoToEntity<UserEntity> {
	@Override
	public UserEntity entity() {
		UserEntity userEntity = new UserEntity();
		Optional.ofNullable(id).ifPresent(userEntity::setId);
		Optional.ofNullable(login).ifPresent(userEntity::setLogin);
		Optional.ofNullable(name).ifPresent(userEntity::setName);
		Optional.ofNullable(password).ifPresent(userEntity::setPassword);
		Optional.ofNullable(img).ifPresent(userEntity::setImg);
		Optional.ofNullable(roles).ifPresent(userEntity::setRoles);
		return userEntity;
	}
}