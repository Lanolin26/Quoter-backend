package faker;

import net.datafaker.AbstractProvider;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;

public class QuoteSourceTypeFake extends AbstractProvider {

	protected QuoteSourceTypeFake(QuoteServiceFaker faker) {
		super(faker);
	}

	public String type() {
		return faker.app().name();
	}

	public QuoteSourceType quoteSourceType() {
		return new QuoteSourceType(type());
	}

	public QuoteSourceType quoteSourceType(Integer id) {
		return new QuoteSourceType(id, type());
	}

}
