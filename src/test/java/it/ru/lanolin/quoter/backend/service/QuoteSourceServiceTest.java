package it.ru.lanolin.quoter.backend.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import it.ru.lanolin.quoter.faker.QuoteServiceFaker;
import it.ru.lanolin.quoter.util.DbTestUtil;
import it.ru.lanolin.quoter.util.DbUnitConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
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
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.service.QuoteSourceService;
import ru.lanolin.quoter.backend.service.QuoteSourceTypeService;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

import static it.ru.lanolin.quoter.util.Utils.MAX_QUOTE_SOURCE_ENTITIES;
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
@DatabaseSetups(value = {
		@DatabaseSetup("classpath:quote-source-type.xml"),
		@DatabaseSetup("classpath:quote-source.xml"),
})
@DisplayName("Integration test QuoteSourceService class")
@Tags({
		@Tag("integration_test"),
		@Tag("QuoteSourceService"),
})
@Slf4j
class QuoteSourceServiceTest {

	private static Random rnd;
	private static QuoteServiceFaker faker;


	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private QuoteSourceService quoteSourceService;
	@Autowired
	private QuoteSourceTypeService quoteSourceTypeService;


	@BeforeAll
	static void beforeAll() {
		rnd = new Random(242423983094L);
		faker = new QuoteServiceFaker(new Locale("ru"), rnd);
	}

	@BeforeEach
	void setUp() throws SQLException {
		DbTestUtil.resetAutoIncrementColumns(applicationContext,
				new DbTestUtil.SequenceInfo("quote_source_type_entity", MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1),
				new DbTestUtil.SequenceInfo("quote_source_entity", MAX_QUOTE_SOURCE_ENTITIES + 1)
		);
	}

	private QuoteSource getQuoteSource(int id_source) {
		// WHEN
		Optional<QuoteSource> sourceOptional = quoteSourceService.getOne(id_source);
		// THEN
		Assumptions.assumeTrue(sourceOptional.isPresent());
		return sourceOptional.get();
	}

	@Test
	@Tag("exist_entries")
	@DisplayName("Проверка существования всех записей")
	void _01_checkAllEntitiesExists() {
		// GIVEN
		// WHEN
		List<QuoteSource> all = new ArrayList<>(quoteSourceService.findAll());
		all.sort(Comparator.comparing(QuoteSource::getId));
		// THEN
		assumeFalse(0 == all.size());

		assertEquals(MAX_QUOTE_SOURCE_ENTITIES, all.size());
		assertAll(IntStream.range(0, MAX_QUOTE_SOURCE_ENTITIES).boxed()
				.map(id -> List.of(
						() -> assertEquals(id + 1, all.get(id).getId(), "Checked id=" + id),
						(Executable) () -> assertNotNull(all.get(id).getType())
				))
				.flatMap(List::stream)
				.toList()
		);
	}

	@Test
	@Tag("add_entry")
	@DisplayName("Добавление нового элемента в БД")
	void _02_addNewValue() {
		// GIVEN
		int type_id = rnd.nextInt(1, MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1);
		QuoteSource quoteSource = faker.quoteSource().quoteSource(type_id);

		// WHEN
		int oldSize = quoteSourceService.findAll().size();
		QuoteSource createdEntity = Assertions.assertDoesNotThrow(() -> quoteSourceService.create(quoteSource));
		int newSize = quoteSourceService.findAll().size();

		// THEN
		assertEquals(1, Math.abs(oldSize - newSize));
		assertNotNull(createdEntity.getId());
		assertEquals(quoteSource.getSourceName(), createdEntity.getSourceName());
		assertNotNull(createdEntity.getType());
		assertEquals(type_id, createdEntity.getType().getId());
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля sourceName")
	void _03_changeSourceNameField() {
		// GIVEN
		int id_source = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES+1);
		String newSourceName = faker.quoteSource().sourceName();

		//WHEN
		QuoteSource quoteSource = getQuoteSource(id_source);
		quoteSource.setSourceName(newSourceName);
		QuoteSource update = quoteSourceService.update(id_source, quoteSource);
		// THEN
		assertNotNull(update);
		assertNotNull(update.getId());
		assertNotNull(update.getType());
		assertEquals(newSourceName, update.getSourceName());
		assertEquals(quoteSource.getType().getId(), update.getType().getId());

		assertEquals(newSourceName, getQuoteSource(id_source).getSourceName());
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля type по ID")
	void _04_changeTypeFieldById() {
		// GIVEN
		int id_source = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES+1);
		int id_source_type = rnd.nextInt(1, MAX_QUOTE_SOURCE_TYPE_ENTITIES+1);

		// WHEN
		QuoteSource quoteSource = getQuoteSource(id_source);
		quoteSource.setType(new QuoteSourceType(id_source_type));
		QuoteSource update = quoteSourceService.update(id_source, quoteSource);

		assertNotNull(update);
		assertNotNull(update.getId());
		assertNotNull(update.getType());
		assertEquals(id_source, update.getId());
		assertEquals(id_source_type, update.getType().getId());
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля type по объекту из бд")
	void _05_changeTypeFieldByObject() {
		// GIVEN
		int id_source = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES+1);
		int id_source_type = rnd.nextInt(1, MAX_QUOTE_SOURCE_TYPE_ENTITIES+1);

		// WHEN
		Optional<QuoteSourceType> sourceTypeOptional = quoteSourceTypeService.getOne(id_source_type);
		// THEN
		Assumptions.assumeTrue(sourceTypeOptional.isPresent());

		// WHEN
		QuoteSource quoteSource = getQuoteSource(id_source);
		QuoteSourceType newQuoteSourceType = sourceTypeOptional.get();
		quoteSource.setType(newQuoteSourceType);
		QuoteSource update = quoteSourceService.update(id_source, quoteSource);

		// THEN
		assertNotNull(update);
		assertNotNull(update.getId());
		assertNotNull(update.getType());
		assertEquals(id_source, update.getId());
		assertEquals(id_source_type, update.getType().getId());
	}

	@Test
	@Tag("delete_entry")
	@DisplayName("Удаление entity по id")
	void _06_deleteEntityById() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES + 1);
		assumeTrue(quoteSourceService.getOne(id).isPresent());
		// WHEN
		int oldSize = quoteSourceService.findAll().size();
		quoteSourceService.deleteById(id);
		int newSize = quoteSourceService.findAll().size();
		// THEN
		assertEquals(1, Math.abs(oldSize - newSize));
		assertFalse(quoteSourceService.getOne(id).isPresent());
	}

	@Test
	@Tag("delete_entry")
	@DisplayName("Удаление entity по объекту")
	void _07_deleteEntityByEntity() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES + 1);
		assumeTrue(quoteSourceService.getOne(id).isPresent());
		// WHEN
		QuoteSource qst = new QuoteSource(id);
		int oldSize = quoteSourceService.findAll().size();
		quoteSourceService.delete(qst);
		int newSize = quoteSourceService.findAll().size();
		// THEN
		assertEquals(1, Math.abs(oldSize - newSize));
		assertFalse(quoteSourceService.getOne(id).isPresent());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("_08_createObjectWithError")
	void _08_createObjectWithError(String type) {
		// GIVEN
		int type_id = rnd.nextInt(1, MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1);
		QuoteSource quoteSource = new QuoteSource(type, new QuoteSourceType(type_id));

		// WHEN
		int oldSize = quoteSourceService.findAll().size();
		assertThrows(IncorrectField.class, () -> quoteSourceService.create(quoteSource));
		int newSize = quoteSourceService.findAll().size();

		// THEN
		assertEquals(oldSize, newSize);
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(ints = { MAX_QUOTE_SOURCE_ENTITIES+2, -1, 0, -1*MAX_QUOTE_SOURCE_ENTITIES })
	@DisplayName("_09_createObjectWithError")
	void _09_createObjectWithError(Integer type_id) {
		// GIVEN
		String sourceName = faker.quoteSource().sourceName();
		QuoteSource quoteSource = new QuoteSource(sourceName, new QuoteSourceType(type_id));

		// WHEN
		int oldSize = quoteSourceService.findAll().size();
		assertThrows(IncorrectField.class, () -> quoteSourceService.create(quoteSource));
		int newSize = quoteSourceService.findAll().size();

		// THEN
		assertEquals(oldSize, newSize);
	}
}