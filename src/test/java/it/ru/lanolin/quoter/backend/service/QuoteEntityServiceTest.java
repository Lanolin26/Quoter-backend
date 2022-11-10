package it.ru.lanolin.quoter.backend.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import it.ru.lanolin.quoter.util.DbTestUtil;
import it.ru.lanolin.quoter.util.DbUnitConfiguration;
import lombok.extern.slf4j.Slf4j;
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
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.service.QuoteEntityService;
import ru.lanolin.quoter.backend.service.QuoteSourceService;
import ru.lanolin.quoter.backend.service.UserEntityService;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

import static it.ru.lanolin.quoter.util.Utils.*;
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
		@DatabaseSetup("classpath:user-data.xml"),
		@DatabaseSetup("classpath:quote-source-type.xml"),
		@DatabaseSetup("classpath:quote-source.xml"),
		@DatabaseSetup("classpath:quote-entity.xml"),
})
@DisplayName("Integration test QuoteEntityService class")
@Tags({
		@Tag("integration_test"),
		@Tag("QuoteEntityService"),
})
@Slf4j
class QuoteEntityServiceTest {

	private static Random rnd;

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private QuoteEntityService quoteEntityService;
	@Autowired
	private QuoteSourceService quoteSourceService;
	@Autowired
	private UserEntityService userEntityService;


	@BeforeAll
	static void beforeAll() {
		rnd = new Random(53945739L);
	}

	@BeforeEach
	void setUp() throws SQLException {
		DbTestUtil.resetAutoIncrementColumns(applicationContext,
				new DbTestUtil.SequenceInfo("user_entity", MAX_USER_ENTITIES + 1),
				new DbTestUtil.SequenceInfo("quote_source_type_entity", MAX_QUOTE_SOURCE_TYPE_ENTITIES + 1),
				new DbTestUtil.SequenceInfo("quote_source_entity", MAX_QUOTE_SOURCE_ENTITIES + 1),
				new DbTestUtil.SequenceInfo("quote_entity", MAX_QUOTE_ENTITIES + 1)
		);
	}

	// TODO: Сделать тесты, на поверку ввода параметров: несущ. элемента и т.д.

	@Test
	@Tag("exist_entries")
	@DisplayName("Проверка существования всех записей")
	void _01_checkAllEntitiesExists() {
		// GIVEN
		// WHEN
		List<QuoteEntity> all = new ArrayList<>(quoteEntityService.findAll());
		all.sort(Comparator.comparing(QuoteEntity::getId));
		// THEN
		assumeFalse(0 == all.size());

		assertEquals(MAX_QUOTE_ENTITIES, all.size());
		assertAll(IntStream.range(0, MAX_QUOTE_ENTITIES).boxed()
				.map(id -> (Executable) () -> assertEquals(id + 1, all.get(id).getId(), "Checked id=" + id))
				.toList()
		);
	}

	@Test
	@Tag("add_entry")
	@DisplayName("Добавление нового элемента в БД")
	void _02_addNewValue() {
		// GIVEN
		int source_id = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES + 1);
		int author_id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		String text = randomStringWithLength(100);

		// WHEN
		int oldSize = quoteEntityService.findAll().size();
		UserEntity userEntity = new UserEntity(author_id);
		QuoteSource quoteSource = new QuoteSource(source_id);
		QuoteEntity qe = new QuoteEntity(text, userEntity, quoteSource);
		QuoteEntity createdEntity = quoteEntityService.create(qe);
		int newSize = quoteEntityService.findAll().size();

		// THEN
		assertEquals(1, Math.abs(oldSize - newSize));
		assertNotNull(createdEntity.getId());
		assertNotNull(createdEntity.getAuthor());
		assertNotNull(createdEntity.getSource());
		assertNotNull(createdEntity.getText());
		assertEquals(text, createdEntity.getText());
		assertEquals(author_id, createdEntity.getAuthor().getId());
		assertEquals(source_id, createdEntity.getSource().getId());
	}

	@Test
	@Tag("add_entry")
	@DisplayName("Добавление нового элемента в БД с зависимыми полями")
	void _11_addNewEntityWithAnotherEntity() {
		// GIVEN
		int source_id = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES + 1);
		int author_id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		String text = randomStringWithLength(100);

		// WHEN
		Optional<QuoteSource> quoteSourceOptional = quoteSourceService.getOne(source_id);
		Optional<UserEntity> userEntityOptional = userEntityService.getOne(author_id);
		// THEN
		assumeTrue(quoteSourceOptional.isPresent());
		assumeTrue(userEntityOptional.isPresent());

		// WHEN
		int oldSize = quoteEntityService.findAll().size();
		UserEntity userEntity = userEntityOptional.get();
		QuoteSource quoteSource = quoteSourceOptional.get();
		QuoteEntity qe = new QuoteEntity(text, userEntity, quoteSource);
		QuoteEntity createdEntity = quoteEntityService.create(qe);
		int newSize = quoteEntityService.findAll().size();
		// THEN
		assertEquals(1, Math.abs(oldSize - newSize));
		assertNotNull(createdEntity.getId());
		assertNotNull(createdEntity.getAuthor());
		assertNotNull(createdEntity.getSource());
		assertNotNull(createdEntity.getText());
		assertEquals(text, createdEntity.getText());
		assertEquals(author_id, createdEntity.getAuthor().getId());
		assertEquals(source_id, createdEntity.getSource().getId());
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля source по ID")
	void _04_changeSourceFieldById() {
		// GIVEN
		int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
		int source_id = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES + 1);
		int oldSize = quoteEntityService.findAll().size();

		// WHEN
		Optional<QuoteEntity> quoteEntityOptional = quoteEntityService.getOne(quote_id);
		// THEN
		assumeTrue(quoteEntityOptional.isPresent());

		// WHEN
		QuoteSource qs = new QuoteSource(source_id);
		QuoteEntity quoteEntity = quoteEntityOptional.get();
		quoteEntity.setSource(qs);
		QuoteEntity updatedEntity = quoteEntityService.update(quote_id, quoteEntity);
		// THEN
		assertNotNull(updatedEntity.getId());
		assertNotNull(updatedEntity.getAuthor());
		assertNotNull(updatedEntity.getSource());
		assertNotNull(updatedEntity.getText());
		assertEquals(quote_id, updatedEntity.getId());
		assertEquals(source_id, updatedEntity.getSource().getId());

		// WHEN
		int newSize = quoteEntityService.findAll().size();
		// THEN
		assertEquals(0, Math.abs(oldSize - newSize));

	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля source по объекту БД")
	void _05_changeSourceFieldObject() {
		// GIVEN
		int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
		int source_id = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES + 1);
		int oldSize = quoteEntityService.findAll().size();

		// WHEN
		Optional<QuoteEntity> quoteEntityOptional = quoteEntityService.getOne(quote_id);
		// THEN
		assumeTrue(quoteEntityOptional.isPresent());

		// WHEN
		Optional<QuoteSource> quoteSourceOptional = quoteSourceService.getOne(source_id);
		// THEN
		assumeTrue(quoteSourceOptional.isPresent());

		// WHEN
		QuoteSource qs = quoteSourceOptional.get();
		QuoteEntity quoteEntity = quoteEntityOptional.get();
		quoteEntity.setSource(qs);
		QuoteEntity updatedEntity = quoteEntityService.update(quote_id, quoteEntity);
		// THEN
		assertNotNull(updatedEntity.getId());
		assertNotNull(updatedEntity.getAuthor());
		assertNotNull(updatedEntity.getSource());
		assertNotNull(updatedEntity.getText());
		assertEquals(quote_id, updatedEntity.getId());
		assertEquals(source_id, updatedEntity.getSource().getId());


		// WHEN
		int newSize = quoteEntityService.findAll().size();
		// THEN
		assertEquals(0, Math.abs(oldSize - newSize));
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля text")
	void _06_changeTextField() {
		// GIVEN
		int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
		String text = randomStringWithLength(100);
		int oldSize = quoteEntityService.findAll().size();

		// WHEN
		Optional<QuoteEntity> quoteEntityOptional = quoteEntityService.getOne(quote_id);
		// THEN
		assumeTrue(quoteEntityOptional.isPresent());

		// WHEN
		QuoteEntity quoteEntity = quoteEntityOptional.get();
		quoteEntity.setText(text);
		QuoteEntity updatedEntity = quoteEntityService.update(quote_id, quoteEntity);
		// THEN
		assertNotNull(updatedEntity);
		assertNotNull(updatedEntity.getId());
		assertNotNull(updatedEntity.getAuthor());
		assertNotNull(updatedEntity.getSource());
		assertNotNull(updatedEntity.getText());
		assertEquals(quote_id, updatedEntity.getId());
		assertEquals(text, updatedEntity.getText());

		// WHEN
		int newSize = quoteEntityService.findAll().size();
		// THEN
		assertEquals(0, Math.abs(oldSize - newSize));
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля author по id")
	void _07_changeAuthorFieldById() {
		// GIVEN
		int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
		int author_id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		int oldSize = quoteEntityService.findAll().size();

		// WHEN
		Optional<QuoteEntity> quoteEntityOptional = quoteEntityService.getOne(quote_id);
		// THEN
		assumeTrue(quoteEntityOptional.isPresent());

		// WHEN
		UserEntity ue = new UserEntity(author_id);
		QuoteEntity quoteEntity = quoteEntityOptional.get();
		quoteEntity.setAuthor(ue);
		QuoteEntity updatedEntity = quoteEntityService.update(quote_id, quoteEntity);
		// THEN
		assertNotNull(updatedEntity.getId());
		assertNotNull(updatedEntity.getAuthor());
		assertNotNull(updatedEntity.getSource());
		assertNotNull(updatedEntity.getText());
		assertEquals(quote_id, updatedEntity.getId());
		assertEquals(author_id, updatedEntity.getAuthor().getId());

		// WHEN
		int newSize = quoteEntityService.findAll().size();
		// THEN
		assertEquals(0, Math.abs(oldSize - newSize));
	}

	@Test
	@Tag("update_entry")
	@DisplayName("Обновление поля author по объекту БД")
	void _08_changeAuthorFieldByObject() {
		// GIVEN
		int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
		int author_id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
		int oldSize = quoteEntityService.findAll().size();

		// WHEN
		Optional<QuoteEntity> quoteEntityOptional = quoteEntityService.getOne(quote_id);
		// THEN
		assumeTrue(quoteEntityOptional.isPresent());

		// WHEN
		Optional<UserEntity> userEntityOptional = userEntityService.getOne(author_id);
		// THEN
		assumeTrue(userEntityOptional.isPresent());

		// WHEN
		UserEntity ue = userEntityOptional.get();
		QuoteEntity quoteEntity = quoteEntityOptional.get();
		quoteEntity.setAuthor(ue);
		QuoteEntity updatedEntity = quoteEntityService.update(quote_id, quoteEntity);
		// THEN
		assertNotNull(updatedEntity.getId());
		assertNotNull(updatedEntity.getAuthor());
		assertNotNull(updatedEntity.getSource());
		assertNotNull(updatedEntity.getText());
		assertEquals(quote_id, updatedEntity.getId());
		assertEquals(author_id, updatedEntity.getAuthor().getId());

		// WHEN
		int newSize = quoteEntityService.findAll().size();
		// THEN
		assertEquals(0, Math.abs(oldSize - newSize));
	}

	@Test
	@Tag("delete_entry")
	@DisplayName("Удаление entity по id")
	void _09_deleteEntityById() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
		assumeTrue(quoteEntityService.getOne(id).isPresent());
		// WHEN
		int oldSize = quoteEntityService.findAll().size();
		quoteEntityService.deleteById(id);
		int newSize = quoteEntityService.findAll().size();
		// THEN
		assertEquals(1, Math.abs(oldSize - newSize));
		assertFalse(quoteEntityService.getOne(id).isPresent());
	}

	@Test
	@Tag("delete_entry")
	@DisplayName("Удаление entity по объекту")
	void _10_deleteEntityByEntity() {
		// GIVEN
		int id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
		assumeTrue(quoteEntityService.getOne(id).isPresent());
		// WHEN
		QuoteEntity qe = new QuoteEntity(id);
		int oldSize = quoteEntityService.findAll().size();
		quoteEntityService.delete(qe);
		int newSize = quoteEntityService.findAll().size();
		// THEN
		assertEquals(1, Math.abs(oldSize - newSize));
		assertFalse(quoteEntityService.getOne(id).isPresent());
	}

}