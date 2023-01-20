package faker;

import net.datafaker.Faker;
import net.datafaker.service.FakeValuesService;
import net.datafaker.service.RandomService;

import java.util.Locale;
import java.util.Random;

public class QuoteServiceFaker extends Faker {

	public QuoteServiceFaker() {
		super();
	}

	public QuoteServiceFaker(Locale locale) {
		super(locale);
	}

	public QuoteServiceFaker(Random random) {
		super(random);
	}

	public QuoteServiceFaker(Locale locale, Random random) {
		super(locale, random);
	}

	public QuoteServiceFaker(Locale locale, RandomService randomService) {
		super(locale, randomService);
	}

	public QuoteServiceFaker(FakeValuesService fakeValuesService, RandomService random) {
		super(fakeValuesService, random);
	}

	public UserEntityFake userEntity() {
		return this.getProvider(UserEntityFake.class, () -> new UserEntityFake(this));
	}

	public QuoteSourceTypeFake quoteSourceType() {
		return this.getProvider(QuoteSourceTypeFake.class, () -> new QuoteSourceTypeFake(this));
	}

	public QuoteSourceFake quoteSource() {
		return this.getProvider(QuoteSourceFake.class, () -> new QuoteSourceFake(this));
	}

	public QuoteFake quote() {
		return this.getProvider(QuoteFake.class, () -> new QuoteFake(this));
	}

}
