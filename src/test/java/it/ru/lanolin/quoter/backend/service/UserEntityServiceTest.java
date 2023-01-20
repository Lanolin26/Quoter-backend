package it.ru.lanolin.quoter.backend.service;

import faker.QuoteServiceFaker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import ru.lanolin.quoter.QuotersLibraryApplication;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.UserRoles;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.service.UserEntityService;
import util.DbTestUtil;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static util.Utils.MAX_USER_ENTITIES;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {QuotersLibraryApplication.class})
@TestPropertySource(locations = {"classpath:application-integrationtest.properties"})
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class
})
@DisplayName("Integration test UserEntityService class")
@Tags({
		@Tag("integration_test"),
		@Tag("UserEntityService"),
})
class UserEntityServiceTest {

	private static Random rnd;
	private static QuoteServiceFaker faker;

	@BeforeAll
	static void beforeAll() {
		rnd = new Random(8475634875L);
		faker = new QuoteServiceFaker(new Locale("ru"), rnd);
	}

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private UserEntityService userEntityService;

	@BeforeEach
	void setUp() throws SQLException {
		DbTestUtil.generateUserEntityInDb(applicationContext, faker);
	}

	@AfterEach
	void tearDown() throws SQLException {
		DbTestUtil.dropUserEntities(applicationContext);
	}

	private UserEntity getUserEntity(int id) {
		// WHEN
		Optional<UserEntity> one = userEntityService.getOne(id);
		// THEN
		assertTrue(one.isPresent());

		// WHEN
		UserEntity userEntity = one.get();
		// THEN
		assertEquals(id, (int) userEntity.getId());
		return userEntity;
	}

	@Test
	@Tag("exist_entries")
	@DisplayName("Проверка, всех записей в БД")
	void _01_checkAllEntitiesExists() {
		// GIVEN
		// WHEN
		List<UserEntity> all = new ArrayList<>(userEntityService.findAll());
		all.sort(Comparator.comparing(UserEntity::getId));
		//THEN
		assertNotEquals(0, all.size());

		assertEquals(MAX_USER_ENTITIES, all.size());
		assertAll(IntStream.range(0, MAX_USER_ENTITIES)
				.boxed()
				.map(id -> (Executable) () -> assertEquals(id + 1, all.get(id).getId(), "Checked id=" + id))
				.toList()
		);
	}


	@Test
	@Tag("add_entry")
	@DisplayName("Добавление нового элемента в БД")
	void _02_addNewValue() {
		// GIVEN
		UserEntity newValue = faker.userEntity().userEntity();
		// WHEN
		int size = userEntityService.findAll().size();
		UserEntity savedValue = Assertions.assertDoesNotThrow(() -> userEntityService.create(newValue));
		int newSize = userEntityService.findAll().size();
		// THEN
		assertEquals(1, Math.abs(size - newSize));
		assertNotNull(savedValue.getId());
		assertAll(
				() -> assertEquals(newSize, savedValue.getId()),
				() -> assertEquals(newValue.getName(), savedValue.getName()),
				() -> assertEquals(newValue.getLogin(), savedValue.getLogin()),
				() -> assertEquals(newValue.getPassword(), savedValue.getPassword()),
				() -> assertEquals(newValue.getRoles(), savedValue.getRoles())
		);
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля name")
	void _03_updateNameField() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		String newName = faker.userEntity().name();
		// WHEN
		UserEntity userEntity = getUserEntity(id);
		userEntity.setName(newName);
		UserEntity update = Assertions.assertDoesNotThrow(() -> userEntityService.update(id, userEntity));
		// THEN
		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertEquals(update.getName(), newName),
				() -> assertEquals(userEntity.getName(), update.getName()),
				() -> assertEquals(userEntity.getLogin(), update.getLogin()),
				() -> assertEquals(userEntity.getPassword(), update.getPassword()),
				() -> assertEquals(userEntity.getImg(), update.getImg()),
				() -> assertEquals(userEntity.getRoles(), update.getRoles())
		);
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля login")
	void _04_updateLoginField() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		String newLogin = faker.userEntity().login();

		// WHEN
		UserEntity userEntity = getUserEntity(id);
		userEntity.setLogin(newLogin);
		UserEntity update = Assertions.assertDoesNotThrow(() -> userEntityService.update(id, userEntity));
		// THEN
		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertEquals(update.getLogin(), newLogin),
				() -> assertEquals(userEntity.getName(), update.getName()),
				() -> assertEquals(userEntity.getLogin(), update.getLogin()),
				() -> assertEquals(userEntity.getPassword(), update.getPassword()),
				() -> assertEquals(userEntity.getImg(), update.getImg()),
				() -> assertEquals(userEntity.getRoles(), update.getRoles())
		);
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля password")
	void _05_updatePasswordField() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		String newPassword = faker.userEntity().password();

		// WHEN
		UserEntity userEntity = getUserEntity(id);
		userEntity.setPassword(newPassword);
		UserEntity update = Assertions.assertDoesNotThrow(() -> userEntityService.update(id, userEntity));
		// THEN
		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertTrue(update.getPassword().endsWith(newPassword)),
				() -> assertEquals(userEntity.getName(), update.getName()),
				() -> assertEquals(userEntity.getLogin(), update.getLogin()),
				() -> assertEquals(userEntity.getPassword(), update.getPassword()),
				() -> assertEquals(userEntity.getImg(), update.getImg()),
				() -> assertEquals(userEntity.getRoles(), update.getRoles())
		);
	}

	@Test
	@Tag("update_entry")
	@Disabled("Not released img")
	@DisplayName("Обновление поля img")
	void _06_updateImgField() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		String value = faker.userEntity().img();
		// WHEN
		UserEntity userEntity = getUserEntity(id);
		userEntity.setImg(value);
		UserEntity update = Assertions.assertDoesNotThrow(() -> userEntityService.update(id, userEntity));
		// THEN
		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertNotNull(update.getImg()),
				() -> assertEquals(value, update.getImg()),
				() -> assertEquals(userEntity.getName(), update.getName()),
				() -> assertEquals(userEntity.getLogin(), update.getLogin()),
				() -> assertEquals(userEntity.getPassword(), update.getPassword()),
				() -> assertEquals(userEntity.getImg(), update.getImg()),
				() -> assertEquals(userEntity.getRoles(), update.getRoles())
		);
	}


	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля roles")
	void _07_updateRoleField() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		Set<UserRoles> newRoles = faker.userEntity().roles();
		// WHEN
		UserEntity userEntity = getUserEntity(id);
		userEntity.setRoles(newRoles);
		UserEntity update = Assertions.assertDoesNotThrow(() -> userEntityService.update(id, userEntity));

		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertNotNull(update.getRoles()),
				() -> assertEquals(userEntity.getRoles().size(), update.getRoles().size()),
				() -> assertEquals(userEntity.getName(), update.getName()),
				() -> assertEquals(userEntity.getLogin(), update.getLogin()),
				() -> assertEquals(userEntity.getPassword(), update.getPassword()),
				() -> assertEquals(userEntity.getImg(), update.getImg()),
				() -> assertEquals(userEntity.getRoles(), update.getRoles())
		);
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля roles")
	void _08_updateRoleField_2() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		// WHEN
		UserEntity userEntity = getUserEntity(id);
		UserRoles roleDel = faker.options().nextElement(new ArrayList<>(userEntity.getRoles()));
		userEntity.getRoles().remove(roleDel);
		if(userEntity.getRoles().size() == 0) {userEntity.getRoles().add(UserRoles.GUEST);}
		UserEntity update = Assertions.assertDoesNotThrow(() -> userEntityService.update(id, userEntity));

		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertNotNull(update.getRoles()),
				() -> assertEquals(userEntity.getName(), update.getName()),
				() -> assertEquals(userEntity.getLogin(), update.getLogin()),
				() -> assertEquals(userEntity.getPassword(), update.getPassword()),
				() -> assertEquals(userEntity.getImg(), update.getImg()),
				() -> assertEquals(userEntity.getRoles().size(), update.getRoles().size()),
				() -> assertEquals(userEntity.getRoles(), update.getRoles())
		);
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля roles")
	void _08_updateRoleField_3() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		// WHEN
		UserEntity userEntity = getUserEntity(id);
		UserRoles roleAdd = faker.options().nextElement(new ArrayList<>(userEntity.getRoles()));
		userEntity.getRoles().add(roleAdd);
		UserEntity update = Assertions.assertDoesNotThrow(() -> userEntityService.update(id, userEntity));

		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertNotNull(update.getRoles()),
				() -> assertEquals(userEntity.getRoles().size(), update.getRoles().size()),
				() -> assertEquals(userEntity.getName(), update.getName()),
				() -> assertEquals(userEntity.getLogin(), update.getLogin()),
				() -> assertEquals(userEntity.getPassword(), update.getPassword()),
				() -> assertEquals(userEntity.getImg(), update.getImg()),
				() -> assertEquals(userEntity.getRoles(), update.getRoles())
		);
	}

	@Test
	@Tag("delete_entry")
	@DisplayName("Удаление entity по id")
	void _09_deleteEntityById() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);

		assertTrue(userEntityService.getOne(id).isPresent());

		// WHEN
		int beforeDel = userEntityService.findAll().size();
		Assertions.assertDoesNotThrow(() -> userEntityService.deleteById(id));
		int afterDel = userEntityService.findAll().size();

		Optional<UserEntity> deletedEntity = userEntityService.getOne(id);

		// THEN
		assertEquals(1, Math.abs(beforeDel - afterDel));
		assertFalse(deletedEntity.isPresent());
	}

	@Test
	@Tag("delete_entry")
	@DisplayName("Удаление entity по объекту")
	void _10_deleteEntityByEntity() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		UserEntity deleteEntity = new UserEntity(id, null, null, null, null, null);
		assertTrue(userEntityService.getOne(id).isPresent());

		// WHEN
		int beforeDel = userEntityService.findAll().size();
		Assertions.assertDoesNotThrow(() -> userEntityService.delete(deleteEntity));
		int afterDel = userEntityService.findAll().size();
		Optional<UserEntity> deletedEntity = userEntityService.getOne(id);
		// THEN
		assertEquals(1, Math.abs(beforeDel - afterDel));
		assertFalse(deletedEntity.isPresent());
	}

	@ParameterizedTest
	@Tag("update_entry")
	@NullAndEmptySource
	@DisplayName("Обновление поля Login с некорректным значением")
	void _11_updateLoginWithErrorValue(String newValue) {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		UserEntity userEntity = getUserEntity(id);
		// WHEN
		userEntity.setLogin(newValue);
		// THEN
		assertThrows(IncorrectField.class, () -> userEntityService.update(id, userEntity));
	}

	@ParameterizedTest
	@Tag("update_entry")
	@NullAndEmptySource
	@DisplayName("Обновление поля Name с некорректным значением")
	void _12_updateNameWithErrorValue(String newValue) {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		UserEntity userEntity = getUserEntity(id);
		// WHEN
		userEntity.setName(newValue);
		// THEN
		assertThrows(IncorrectField.class, () -> userEntityService.update(id, userEntity));
	}

	@ParameterizedTest
	@Tag("update_entry")
	@NullAndEmptySource
	@ValueSource(strings = { "1", "12", "123", "1234", "12345" })
	@DisplayName("Обновление поля Password с некорректным значением")
	void _13_updatePasswordWithErrorValue(String newValue) {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		UserEntity userEntity = getUserEntity(id);
		// WHEN
		userEntity.setPassword(newValue);
		// THEN
		assertThrows(IncorrectField.class, () -> userEntityService.update(id, userEntity));
	}

	@Test
	@Tag("update_entry")
	@Disabled("Not released img")
	@DisplayName("Обновление поля Img с некорректным значением")
	void _14_updateImgWithErrorValue() {
		fail();
		// GIVEN
		// WHEN
		// THEN
	}

	@Test
	@Tag("update_entry")
	@Disabled("Not find role incorrect value")
	@DisplayName("Обновление поля Role с некорректным значением")
	void _15_updateRoleWithErrorValue() {
		fail();
		// GIVEN
		// WHEN
		// THEN
	}

	@ParameterizedTest
	@Tag("add_entry")
	@NullAndEmptySource
	@DisplayName("Добавление объекта с некорректным значением Name")
	void _16_addNewValueWithErrorName(String name) {
		// GIVEN
		UserEntity newValue = faker.userEntity().userEntity();
		newValue.setName(name);
		// WHEN
		int oldSize = userEntityService.findAll().size();
		Assertions.assertThrows(IncorrectField.class, () -> userEntityService.create(newValue));
		int newSize = userEntityService.findAll().size();
		// THEN
		assertEquals(oldSize, newSize);
	}

	@ParameterizedTest
	@Tag("add_entry")
	@NullAndEmptySource
	@DisplayName("Добавление объекта с некорректным значением Login")
	void _17_addNewValueWithErrorLogin(String login) {
		// GIVEN
		UserEntity newValue = faker.userEntity().userEntity();
		newValue.setLogin(login);
		// WHEN
		int oldSize = userEntityService.findAll().size();
		Assertions.assertThrows(IncorrectField.class, () -> userEntityService.create(newValue));
		int newSize = userEntityService.findAll().size();
		// THEN
		assertEquals(oldSize, newSize);
	}

	@ParameterizedTest
	@Tag("add_entry")
	@NullAndEmptySource
	@ValueSource(strings = { "1", "12", "123", "1234", "12345" })
	@DisplayName("Добавление объекта с некорректным значением Password")
	void _17_addNewValueWithErrorPassword(String password) {
		// GIVEN
		UserEntity newValue = faker.userEntity().userEntity();
		newValue.setPassword(password);
		// WHEN
		int oldSize = userEntityService.findAll().size();
		Assertions.assertThrows(IncorrectField.class, () -> userEntityService.create(newValue));
		int newSize = userEntityService.findAll().size();
		// THEN
		assertEquals(oldSize, newSize);
	}

}
