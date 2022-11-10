package it.ru.lanolin.quoter.backend.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import it.ru.lanolin.quoter.util.DbTestUtil;
import it.ru.lanolin.quoter.util.DbUnitConfiguration;
import it.ru.lanolin.quoter.util.Utils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
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
import ru.lanolin.quoter.backend.service.QuoteSourceTypeService;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

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

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private QuoteSourceTypeService quoteSourceTypeService;

	@BeforeAll
	static void beforeAll() {
		rnd = new Random(32492347234234L);
	}

	@BeforeEach
	void setUp() throws SQLException {
		DbTestUtil.resetAutoIncrementColumns(applicationContext,
				new DbTestUtil.SequenceInfo("quote_source_type_entity", Utils.MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1)
		);
	}

	// TODO: Сделать тесты, на поверку ввода параметров: несущ. элемента и т.д.

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

		Assertions.assertEquals(Utils.MAX_QUOTE_SOURCE_TYPE_ENTITIES, all.size());
		assertAll(IntStream.range(0, Utils.MAX_QUOTE_SOURCE_TYPE_ENTITIES).boxed()
				.map(id -> (Executable) () -> assertEquals(id + 1, all.get(id).getId(), "Checked id=" + id))
				.toList()
		);
	}

	@Test
	@Tag("add_entry")
	@DisplayName("Добавление нового элемента в БД")
	void _02_addNewValue() {
		// GIVEN
		String type = Utils.randomStringWithLength(8);
		QuoteSourceType quoteSourceType = new QuoteSourceType(null, type);

		// WHEN
		int oldSize = quoteSourceTypeService.findAll().size();
		QuoteSourceType createdEntity = quoteSourceTypeService.create(quoteSourceType);
		int newSize = quoteSourceTypeService.findAll().size();

		// THEN
		assertEquals(1, Math.abs(oldSize - newSize));
		assertNotNull(createdEntity.getId());
		assertEquals(type, createdEntity.getType());
	}

	//TODO: new entry with different vars

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля type")
	void _03_updateTypeField() {
		// GIVEN
		int id = rnd.nextInt(1, Utils.MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1);
		String additionType = Utils.randomStringWithLength(8);

		// WHEN
		Optional<QuoteSourceType> one = quoteSourceTypeService.getOne(id);
		// THEN
		Assumptions.assumeTrue(one.isPresent());

		// WHEN
		QuoteSourceType entityInDb = one.get();
		entityInDb.setType(entityInDb.getType() + additionType);
		QuoteSourceType update = quoteSourceTypeService.update(id, entityInDb);
		// THEN
		assertNotNull(update);
		assertAll(
				() -> assertEquals(id, update.getId()),
				() -> assertEquals(update.getType(), entityInDb.getType()),
				() -> assertTrue(update.getType().endsWith(additionType))
		);
	}

	@Test
	@Tag("delete_entry")
	@DisplayName("Удаление entity по id")
	void _04_deleteEntityById() {
		// GIVEN
		int id = rnd.nextInt(1, Utils.MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1);
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
		int id = rnd.nextInt(1, Utils.MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1);
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
}