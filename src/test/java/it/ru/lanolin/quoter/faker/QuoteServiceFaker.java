package it.ru.lanolin.quoter.faker;

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

	public UserEntityFaker userEntity() {
		return this.getProvider(UserEntityFaker.class, () -> new UserEntityFaker(this));
	}

	public QuoteSourceTypeFaker quoteSourceType() {
		return this.getProvider(QuoteSourceTypeFaker.class, () -> new QuoteSourceTypeFaker(this));
	}

	public QuoteSourceFaker quoteSource() {
		return this.getProvider(QuoteSourceFaker.class, () -> new QuoteSourceFaker(this));
	}

	public QuoteFaker quote() {
		return this.getProvider(QuoteFaker.class, () -> new QuoteFaker(this));
	}

}
