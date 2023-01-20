package faker;

import net.datafaker.AbstractProvider;
import net.datafaker.Password;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.UserRoles;

import java.util.Set;

import static util.Utils.ALL_LETTERS;

public class UserEntityFake extends AbstractProvider {

	private final Password.PasswordRuleConfig passwordRuleConfig;

	protected UserEntityFake(QuoteServiceFaker faker) {
		super(faker);
		passwordRuleConfig = Password.PasswordSymbolsBuilder.builder()
				.withMaxLength(16)
				.withMinLength(6)
				.with(String.join("", ALL_LETTERS), 5)
				.build(faker);
	}


	public String name() {
		return faker.name().fullName();
	}

	public String login() {
		return faker.name().username();
	}

	public String password() {
		return faker.password().password(passwordRuleConfig);
	}

	public Set<UserRoles> roles() {
		UserRoles[] values = UserRoles.values();
		Integer size = faker.random().nextInt(1, values.length);
		return faker.options().subset(size, values);
	}

	public UserEntity userEntity() {
		return new UserEntity(login(), name(), password(), null, roles());
	}

	public UserEntity userEntity(Integer id) {
		return new UserEntity(id, login(), name(), password(), null, roles());
	}

	public String img() {
		return null;
	}
}
