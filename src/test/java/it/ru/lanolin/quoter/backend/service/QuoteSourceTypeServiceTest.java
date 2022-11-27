package it.ru.lanolin.quoter.backend.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import it.ru.lanolin.quoter.faker.QuoteServiceFaker;
import it.ru.lanolin.quoter.util.DbTestUtil;
import it.ru.lanolin.quoter.util.DbUnitConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.service.QuoteSourceTypeService;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

import static it.ru.lanolin.quoter.util.Utils.MAX_QUOTE_SOURCE_TYPE_ENTITIES;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

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
@DatabaseSetup("classpath:quote-source-type.xml")
@DisplayName("Integration test QuoteSourceTypeService class")
@Tags({
		@Tag("integration_test"),
		@Tag("QuoteSourceTypeService"),
})
class QuoteSourceTypeServiceTest {

	private static Random rnd;
	private static QuoteServiceFaker faker;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private QuoteSourceTypeService quoteSourceTypeService;

	@BeforeAll
	static void beforeAll() {
		rnd = new Random(32492347234234L);
		faker = new QuoteServiceFaker(new Locale("ru"), rnd);
	}

	@BeforeEach
	void setUp() throws SQLException {
		DbTestUtil.resetAutoIncrementColumns(applicationContext,
				new DbTestUtil.SequenceInfo("quote_source_type_entity", MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1)
		);
	}

	@Test
	@Tag("exist_entries")
	@DisplayName("Проверка существования всех записей")
	void _01_checkAllEntitiesExists() {
		// GIVEN
		// WHEN
		List<QuoteSourceType> all = new ArrayList<>(quoteSourceTypeService.findAll());
		all.sort(Comparator.comparing(QuoteSourceType::getId));
		// THEN
		assumeFalse(0 == all.size());

		Assertions.assertEquals(MAX_QUOTE_SOURCE_TYPE_ENTITIES, all.size());
		assertAll(IntStream.range(0, MAX_QUOTE_SOURCE_TYPE_ENTITIES).boxed()
				.map(id -> (Executable) () -> assertEquals(id + 1, all.get(id).getId(), "Checked id=" + id))
				.toList()
		);
	}

	@Test
	@Tag("add_entry")
	@DisplayName("Добавление нового элемента в БД")
	void _02_addNewValue() {
		// GIVEN
		QuoteSourceType quoteSourceType = faker.quoteSourceType().quoteSourceType();

		// WHEN
		int oldSize = quoteSourceTypeService.findAll().size();
		QuoteSourceType createdEntity = quoteSourceTypeService.create(quoteSourceType);
		int newSize = quoteSourceTypeService.findAll().size();

		// THEN
		assertEquals(1, Math.abs(oldSize - newSize));
		assertNotNull(createdEntity.getId());
		assertEquals(quoteSourceType.getType(), createdEntity.getType());
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля type")
	void _03_updateTypeField() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1);
		String additionType = faker.quoteSourceType().type();

		// WHEN
		Optional<QuoteSourceType> one = quoteSourceTypeService.getOne(id);
		// THEN
		Assumptions.assumeTrue(one.isPresent());

		// WHEN
		QuoteSourceType entityInDb = one.get();
		entityInDb.setType(additionType);
		QuoteSourceType update = quoteSourceTypeService.update(id, entityInDb);
		// THEN
		assertNotNull(update);
		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertEquals(update.getType(), entityInDb.getType()),
				() -> assertEquals(update.getType(), additionType)
		);
	}

	@Test
	@Tag("delete_entry")
	@DisplayName("Удаление entity по id")
	void _04_deleteEntityById() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1);
		assumeTrue(quoteSourceTypeService.getOne(id).isPresent());
		// WHEN
		int oldSize = quoteSourceTypeService.findAll().size();
		quoteSourceTypeService.deleteById(id);
		int newSize = quoteSourceTypeService.findAll().size();
		// THEN
		assertEquals(1, Math.abs(oldSize - newSize));
		assertFalse(quoteSourceTypeService.getOne(id).isPresent());
	}

	@Test
	@Tag("delete_entry")
	@DisplayName("Удаление entity по объекту")
	void _05_deleteEntityByEntity() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1);
		assumeTrue(quoteSourceTypeService.getOne(id).isPresent());
		// WHEN
		QuoteSourceType qst = new QuoteSourceType(id, null);
		int oldSize = quoteSourceTypeService.findAll().size();
		quoteSourceTypeService.delete(qst);
		int newSize = quoteSourceTypeService.findAll().size();
		// THEN
		assertEquals(1, Math.abs(oldSize - newSize));
		assertFalse(quoteSourceTypeService.getOne(id).isPresent());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("_06_updateTypeWithError")
	void _06_updateTypeWithError(String newValue) {
		// GIVEN
		int id = rnd.nextInt(1, MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1);

		// WHEN
		Optional<QuoteSourceType> one = quoteSourceTypeService.getOne(id);
		// THEN
		Assumptions.assumeTrue(one.isPresent());

		// WHEN
		QuoteSourceType editValue = one.get();
		// THEN
		Assumptions.assumeTrue(editValue.getId() == id);

		// WHEN
		editValue.setType(newValue);
		// THEN
		assertThrows(IncorrectField.class, () -> quoteSourceTypeService.update(id, editValue));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("_07_createEntityWithError")
	void _07_createEntityWithError(String value) {
		// GIVEN
		QuoteSourceType created = new QuoteSourceType(value);
		// WHEN
		// THEN
		int oldSize = quoteSourceTypeService.findAll().size();
		assertThrows(IncorrectField.class, () -> quoteSourceTypeService.create(created));
		int newSize = quoteSourceTypeService.findAll().size();
		assertEquals(oldSize, newSize);
	}
}