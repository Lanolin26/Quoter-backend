package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.service.UserEntityService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserEntityController implements RestApiController<UserEntity, Integer> {

	private final UserEntityService userEntityService;

	@Autowired
	public UserEntityController(UserEntityService userEntityService) {
		this.userEntityService = userEntityService;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public UserEntityService getService() {
		return this.userEntityService;
	}
}
