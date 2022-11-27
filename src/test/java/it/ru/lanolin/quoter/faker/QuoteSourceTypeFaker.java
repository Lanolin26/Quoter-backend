package it.ru.lanolin.quoter.faker;

import net.datafaker.AbstractProvider;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;

public class QuoteSourceTypeFaker extends AbstractProvider {

	protected QuoteSourceTypeFaker(QuoteServiceFaker faker) {
		super(faker);
	}

	public String type() {
		return faker.app().name();
	}

	public QuoteSourceType quoteSourceType() {
		return new QuoteSourceType(type());
	}

}
