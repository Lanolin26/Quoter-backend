package it.ru.lanolin.quoter.backend.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import it.ru.lanolin.quoter.util.DbTestUtil;
import it.ru.lanolin.quoter.util.DbUnitConfiguration;
import it.ru.lanolin.quoter.util.Utils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import ru.lanolin.quoter.QuotersLibraryApplication;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.UserRoles;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.service.UserEntityService;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

import static it.ru.lanolin.quoter.util.Utils.MAX_USER_ENTITIES;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static ru.lanolin.quoter.backend.domain.UserRoles.GUEST;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { QuotersLibraryApplication.class, DbUnitConfiguration.class })
@AutoConfigureMockMvc
@TestPropertySource(locations = {
		"classpath:application-integrationtest.properties"
})
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class
})
@Transactional
@DatabaseSetup("classpath:user-data.xml")
@DisplayName("Integration test UserEntityService class")
@Tags({
		@Tag("integration_test"),
		@Tag("UserEntityService"),
})
class UserEntityServiceTest {

	private static Random rnd;

	@BeforeAll
	static void beforeAll() {
		rnd = new Random(8475634875L);
	}

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private UserEntityService userEntityService;

	@BeforeEach
	void setUp() throws SQLException {
		DbTestUtil.resetAutoIncrementColumns(applicationContext,
				new DbTestUtil.SequenceInfo("user_entity", MAX_USER_ENTITIES + 1)
		);
	}

	private UserEntity getUserEntity(int id) {
		// WHEN
		Optional<UserEntity> one = userEntityService.getOne(id);
		// THEN
		assumeTrue(one.isPresent());

		// WHEN
		UserEntity userEntity = one.get();
		// THEN
		assumeTrue(id == userEntity.getId());
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
		assumeFalse(0 == all.size());

		assertEquals(4, all.size());
		assertAll(IntStream.range(0, MAX_USER_ENTITIES).boxed()
				.map(id -> (Executable) () -> assertEquals(id + 1, all.get(id).getId(), "Checked id=" + id))
				.toList()
		);
	}


	@Test
	@Tag("add_entry")
	@DisplayName("Добавление нового элемента в БД")
	void _02_addNewValue() {
		// GIVEN
		String name = Utils.randomStringWithLength(10);
		String login = Utils.randomStringWithLength(15);
		String password = Utils.randomStringWithLength(22);
		Set<UserRoles> roles = Set.of(GUEST);
		UserEntity newValue = new UserEntity(null, login, name, password, null, roles);
		// WHEN
		int size = userEntityService.findAll().size();
		UserEntity savedValue = Assertions.assertDoesNotThrow(() -> userEntityService.create(newValue));
		int newSize = userEntityService.findAll().size();
		// THEN
		assertEquals(1, Math.abs(size - newSize));
		assertNotNull(savedValue.getId());
		assertAll(
				() -> assertEquals(newSize, savedValue.getId()),
				() -> assertEquals(name, savedValue.getName()),
				() -> assertEquals(login, savedValue.getLogin()),
				() -> assertEquals(password, savedValue.getPassword()),
				() -> assertEquals(roles, savedValue.getRoles())
		);
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля name")
	void _03_updateNameField() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		String appendValue = Utils.randomStringWithLength(5);
		// WHEN
		UserEntity userEntity = getUserEntity(id);
		userEntity.setName(userEntity.getName() + appendValue);
		UserEntity update = Assertions.assertDoesNotThrow(() -> userEntityService.update(id, userEntity));
		// THEN
		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertTrue(update.getName().endsWith(appendValue)),
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
		String appendValue = Utils.randomStringWithLength(5);

		// WHEN
		UserEntity userEntity = getUserEntity(id);
		userEntity.setLogin(userEntity.getLogin() + appendValue);
		UserEntity update = Assertions.assertDoesNotThrow(() -> userEntityService.update(id, userEntity));
		// THEN
		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertTrue(update.getLogin().endsWith(appendValue)),
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
		String appendValue = Utils.randomStringWithLength(5);

		// WHEN
		UserEntity userEntity = getUserEntity(id);
		userEntity.setPassword(userEntity.getPassword() + appendValue);
		UserEntity update = Assertions.assertDoesNotThrow(() -> userEntityService.update(id, userEntity));
		// THEN
		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertTrue(update.getPassword().endsWith(appendValue)),
				() -> assertEquals(userEntity.getName(), update.getName()),
				() -> assertEquals(userEntity.getLogin(), update.getLogin()),
				() -> assertEquals(userEntity.getPassword(), update.getPassword()),
				() -> assertEquals(userEntity.getImg(), update.getImg()),
				() -> assertEquals(userEntity.getRoles(), update.getRoles())
		);
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля img")
	void _06_updateImgField() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		String value = Utils.randomStringWithLength(5);
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
		UserRoles roleAdd = UserRoles.ADMIN;
		// WHEN
		UserEntity userEntity = getUserEntity(id);
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
	@Tag("update_entry")
	@DisplayName("Обновление поля roles")
	void _08_updateRoleField_2() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		UserRoles roleDel = UserRoles.EDITOR;
		// WHEN
		UserEntity userEntity = getUserEntity(id);
		userEntity.getRoles().remove(roleDel);
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

		assumeTrue(userEntityService.getOne(id).isPresent());

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
		assumeTrue(userEntityService.getOne(id).isPresent());

		// WHEN
		int beforeDel = userEntityService.findAll().size();
		Assertions.assertDoesNotThrow(() -> userEntityService.delete(deleteEntity));
		int afterDel = userEntityService.findAll().size();
		Optional<UserEntity> deletedEntity = userEntityService.getOne(id);
		// THEN
		assertEquals(1, Math.abs(beforeDel - afterDel));
		assertFalse(deletedEntity.isPresent());
	}

	// TODO: Сделать тесты, на поверку ввода параметров: несущ. элемента и т.д.


	@ParameterizedTest
	@Tag("update_entry")
	@NullAndEmptySource
	@DisplayName("_12_updateNameWithErrorValue")
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
	@DisplayName("_12_updateNameWithErrorValue")
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
	@ValueSource(strings = {"1", "12", "123", "1234", "12345"})
	@DisplayName("_13_updatePasswordWithErrorValue")
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
	@DisplayName("_14_updateImgWithErrorValue")
	void _14_updateImgWithErrorValue() {
		Assertions.assertTrue(true);
		// GIVEN
		// WHEN
		// THEN
	}

	@Test
	@Tag("update_entry")
	@DisplayName("_15_updateRoleWithErrorValue")
	void _15_updateRoleWithErrorValue() {
		Assertions.assertTrue(true);
		// GIVEN
		// WHEN
		// THEN
	}

}
