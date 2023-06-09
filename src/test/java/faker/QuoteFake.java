package faker;

import net.datafaker.AbstractProvider;
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.UserEntity;

public class QuoteFake extends AbstractProvider {

	private final QuoteServiceFaker localFaker;
	
	protected QuoteFake(QuoteServiceFaker faker) {
		super(faker);
		localFaker = faker;
	}

	public UserEntity author() {
		return localFaker.userEntity().userEntity();
	}

	public QuoteSource source() {
		return localFaker.quoteSource().quoteSource();
	}

	public String text() {
		return localFaker.lorem().characters(5, 512);
	}

	public QuoteEntity quoteEntity() {
		return new QuoteEntity(text(), author(), source());
	}

	public QuoteEntity quoteEntity(int authorId) {
		return new QuoteEntity(text(), new UserEntity(authorId), source());
	}

	public QuoteEntity quoteEntity(int authorId, int sourceId) {
		return new QuoteEntity(text(), new UserEntity(authorId), new QuoteSource(sourceId));
	}

	public QuoteEntity quoteEntity(int id, int authorId, int sourceId) {
		return new QuoteEntity(id, text(), new UserEntity(authorId), new QuoteSource(sourceId));
	}
}
