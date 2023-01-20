package faker;

import net.datafaker.AbstractProvider;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;

public class QuoteSourceFake extends AbstractProvider  {
	
	private final QuoteServiceFaker localFaker;
	
	protected QuoteSourceFake(QuoteServiceFaker faker) {
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

	public QuoteSource quoteSource(int id, int typeId) {
		return new QuoteSource(id, sourceName(), new QuoteSourceType(typeId));
	}
}
