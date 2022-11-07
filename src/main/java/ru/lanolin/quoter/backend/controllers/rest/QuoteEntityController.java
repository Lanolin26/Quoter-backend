package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.domain.dto.QuoteEntityDto;
import ru.lanolin.quoter.backend.service.QuoteEntityService;

@RestController
@RequestMapping("/api/quote")
@CrossOrigin(origins = "*")
public class QuoteEntityController implements RestApiController<QuoteEntity, QuoteEntityDto, Integer> {

	private final QuoteEntityService quoterEntityService;

	@Autowired
	public QuoteEntityController(QuoteEntityService quoterEntityService) {
		this.quoterEntityService = quoterEntityService;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public QuoteEntityService getService() {
		return this.quoterEntityService;
	}
}
