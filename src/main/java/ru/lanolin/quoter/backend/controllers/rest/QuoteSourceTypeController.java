package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.service.QuoteSourceTypeService;

@RestController
@RequestMapping("/api/quote-source-type")
@CrossOrigin(origins = "*")
public class QuoteSourceTypeController implements RestApiController<QuoteSourceType, Integer> {

	private final QuoteSourceTypeService quoteSourceTypeService;

	@Autowired
	public QuoteSourceTypeController(QuoteSourceTypeService quoteSourceTypeService) {
		this.quoteSourceTypeService = quoteSourceTypeService;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public QuoteSourceTypeService getService() {
		return this.quoteSourceTypeService;
	}
}
