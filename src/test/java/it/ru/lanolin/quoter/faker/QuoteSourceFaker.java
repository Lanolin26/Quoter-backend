package it.ru.lanolin.quoter.faker;

import net.datafaker.AbstractProvider;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;

public class QuoteSourceFaker extends AbstractProvider  {
	
	private final QuoteServiceFaker localFaker;
	
	protected QuoteSourceFaker(QuoteServiceFaker faker) {
		super(faker);
		localFaker = faker;
	}

	public QuoteSourceType type() {
		return localFaker.quoteSourceType().quoteSourceType();
	}

	public String sourceName() {
		return localFaker.book().genre();
	}

	public QuoteSource quoteSource() {
		return new QuoteSource(sourceName(), type());
	}

	public QuoteSource quoteSource(int typeId) {
		return new QuoteSource(sourceName(), new QuoteSourceType(typeId));
	}

}
