package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.service.QuoteSourceService;

@RestController
@RequestMapping("/api/quote-source")
@CrossOrigin(origins = "*")
public class QuoteSourceController implements RestApiController<QuoteSource, Integer> {

	private final QuoteSourceService quoteSourceService;

	@Autowired
	public QuoteSourceController(QuoteSourceService quoteSourceService) {
		this.quoteSourceService = quoteSourceService;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public QuoteSourceService getService() {
		return this.quoteSourceService;
	}
}
