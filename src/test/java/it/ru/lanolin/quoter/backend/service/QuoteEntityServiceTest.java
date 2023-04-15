package it.ru.lanolin.quoter.backend.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import faker.QuoteServiceFaker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import ru.lanolin.quoter.QuotersLibraryApplication;
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.view.QuoteEntityIdsInfo;
import ru.lanolin.quoter.backend.domain.view.QuoteEntityIdsInfoImpl;
import ru.lanolin.quoter.backend.domain.view.QuoteEntityInfo;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.service.QuoteEntityService;
import ru.lanolin.quoter.backend.service.QuoteSourceService;
import ru.lanolin.quoter.backend.service.UserEntityService;
import util.DbTestUtil;
import util.DbUnitConfiguration;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static util.Utils.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {QuotersLibraryApplication.class, DbUnitConfiguration.class})
@TestPropertySource(locations = {
        "classpath:application-integrationtest.properties"
})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
@DisplayName("Integration test QuoteEntityService class")
@Tags({
        @Tag("integration_test"),
        @Tag("QuoteEntityService"),
})
@Slf4j
class QuoteEntityServiceTest {

    private static Random rnd;
    private static QuoteServiceFaker faker;

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private QuoteEntityService quoteEntityService;
    @Autowired
    private QuoteSourceService quoteSourceService;
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    static void beforeAll() {
        rnd = new Random(53945739L);
        faker = new QuoteServiceFaker(new Locale("ru"), rnd);
    }

    @BeforeEach
    void setUp() throws SQLException {
        DbTestUtil.generateUserEntityInDb(applicationContext, faker, passwordEncoder);
        DbTestUtil.generateQuoteSourceTypeInDb(applicationContext, faker);
        DbTestUtil.generateQuoteSourceInDb(applicationContext, faker);
        DbTestUtil.generateQuoteEntityInDb(applicationContext, faker);
    }

    @AfterEach
    void tearDown() throws SQLException {
        DbTestUtil.dropQuoteEntity(applicationContext);
        DbTestUtil.dropQuoteSources(applicationContext);
        DbTestUtil.dropQuoteSourceTypes(applicationContext);
        DbTestUtil.dropUserEntities(applicationContext);
    }

    private QuoteEntity getQuoteEntity(int quote_id) {
        // WHEN
        Optional<QuoteEntity> quoteEntityOptional = quoteEntityService.getOne(quote_id);
        // THEN
        assertTrue(quoteEntityOptional.isPresent());
        return quoteEntityOptional.get();
    }

    private QuoteSource getQuoteSource(int source_id) {
        Optional<QuoteSource> quoteSourceOptional = quoteSourceService.getOne(source_id);
        assertTrue(quoteSourceOptional.isPresent());
        return quoteSourceOptional.get();
    }

    private UserEntity getUserEntity(int author_id) {
        Optional<UserEntity> userEntityOptional = userEntityService.getOne(author_id);
        assertTrue(userEntityOptional.isPresent());
        return userEntityOptional.get();
    }

    @Test
    @Tag("exist_entries")
    @DisplayName("Проверка существования всех записей")
    void _01_checkAllEntitiesExists() {
        // GIVEN
        // WHEN
        List<QuoteEntity> all = new ArrayList<>(quoteEntityService.findAll());
        all.sort(Comparator.comparing(QuoteEntity::getId));
        // THEN
        assertNotEquals(0, all.size());

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

        // WHEN
        long oldSize = quoteEntityService.count();
        QuoteEntity qe = faker.quote().quoteEntity(author_id, source_id);
        QuoteEntity createdEntity = assertDoesNotThrow(() -> quoteEntityService.create(qe));
        long newSize = quoteEntityService.count();

        // THEN
        assertAll(
                () -> assertEquals(1, Math.abs(oldSize - newSize)),
                () -> assertNotNull(createdEntity.getId()),
                () -> assertNotNull(createdEntity.getAuthor()),
                () -> assertNotNull(createdEntity.getAuthor().getId()),
                () -> assertNotNull(createdEntity.getSource()),
                () -> assertNotNull(createdEntity.getSource().getId()),
                () -> assertNotNull(createdEntity.getSource().getType()),
                () -> assertNotNull(createdEntity.getSource().getType().getId()),
                () -> assertNotNull(createdEntity.getText()),
                () -> assertEquals(qe.getText(), createdEntity.getText()),
                () -> assertEquals(author_id, createdEntity.getAuthor().getId()),
                () -> assertEquals(source_id, createdEntity.getSource().getId())
        );
    }

    @Test
    @Tag("add_entry")
    @DisplayName("Добавление нового элемента в БД с зависимыми полями")
    void _03_addNewEntityWithAnotherEntity() {
        // GIVEN
        int source_id = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES + 1);
        int author_id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
        String text = faker.quote().text();

        QuoteSource quoteSource = getQuoteSource(source_id);
        UserEntity userEntity = getUserEntity(author_id);

        // WHEN
        long oldSize = quoteEntityService.count();
        QuoteEntity qe = new QuoteEntity(text, userEntity, quoteSource);
        QuoteEntity createdEntity = assertDoesNotThrow(() -> quoteEntityService.create(qe));
        long newSize = quoteEntityService.count();
        // THEN
        assertAll(
                () -> assertEquals(1, Math.abs(oldSize - newSize)),
                () -> assertNotNull(createdEntity.getId()),
                () -> assertNotNull(createdEntity.getAuthor()),
                () -> assertNotNull(createdEntity.getSource().getId()),
                () -> assertNotNull(createdEntity.getSource().getType()),
                () -> assertNotNull(createdEntity.getSource().getType().getId()),
                () -> assertNotNull(createdEntity.getText()),
                () -> assertEquals(text, createdEntity.getText()),
                () -> assertEquals(author_id, createdEntity.getAuthor().getId()),
                () -> assertEquals(source_id, createdEntity.getSource().getId())
        );
    }

    @Test
    @Tag("update_entry")
    @DisplayName("Обновление поля source по ID")
    void _04_changeSourceFieldById() {
        // GIVEN
        int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
        int source_id = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES + 1);
        long oldSize = quoteEntityService.count();

        QuoteEntity quoteEntity = getQuoteEntity(quote_id);
        QuoteSource qs = new QuoteSource(source_id);
        quoteEntity.setSource(qs);
        QuoteEntity updatedEntity = assertDoesNotThrow(() -> quoteEntityService.update(quote_id, quoteEntity));
        // THEN
        assertAll(
                () -> assertNotNull(updatedEntity.getId()),
                () -> assertNotNull(updatedEntity.getAuthor()),
                () -> assertNotNull(updatedEntity.getSource().getId()),
                () -> assertNotNull(updatedEntity.getSource().getType()),
                () -> assertNotNull(updatedEntity.getSource().getType().getId()),
                () -> assertNotNull(updatedEntity.getText()),
                () -> assertEquals(quote_id, updatedEntity.getId()),
                () -> assertEquals(source_id, updatedEntity.getSource().getId())
        );

        // WHEN
        long newSize = quoteEntityService.count();
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
        long oldSize = quoteEntityService.count();

        // WHEN
        QuoteSource qs = getQuoteSource(source_id);
        QuoteEntity quoteEntity = getQuoteEntity(quote_id);
        quoteEntity.setSource(qs);
        QuoteEntity updatedEntity = assertDoesNotThrow(() -> quoteEntityService.update(quote_id, quoteEntity));
        // THEN
        assertAll(
                () -> assertNotNull(updatedEntity.getId()),
                () -> assertNotNull(updatedEntity.getAuthor()),
                () -> assertNotNull(updatedEntity.getSource().getId()),
                () -> assertNotNull(updatedEntity.getSource().getType()),
                () -> assertNotNull(updatedEntity.getSource().getType().getId()),
                () -> assertNotNull(updatedEntity.getText()),
                () -> assertEquals(quote_id, updatedEntity.getId()),
                () -> assertEquals(source_id, updatedEntity.getSource().getId())
        );

        // WHEN
        long newSize = quoteEntityService.count();
        // THEN
        assertEquals(0, Math.abs(oldSize - newSize));
    }

    @Test
    @Tag("update_entry")
    @DisplayName("Обновление поля text")
    void _06_changeTextField() {
        // GIVEN
        int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
        String text = faker.quote().text();
        long oldSize = quoteEntityService.count();

        // WHEN
        QuoteEntity quoteEntity = getQuoteEntity(quote_id);
        quoteEntity.setText(text);
        QuoteEntity updatedEntity = assertDoesNotThrow(() -> quoteEntityService.update(quote_id, quoteEntity));
        // THEN
        assertAll(
                () -> assertNotNull(updatedEntity),
                () -> assertNotNull(updatedEntity.getId()),
                () -> assertNotNull(updatedEntity.getAuthor()),
                () -> assertNotNull(updatedEntity.getSource().getId()),
                () -> assertNotNull(updatedEntity.getSource().getType()),
                () -> assertNotNull(updatedEntity.getSource().getType().getId()),
                () -> assertNotNull(updatedEntity.getText()),
                () -> assertEquals(quote_id, updatedEntity.getId()),
                () -> assertEquals(text, updatedEntity.getText())
        );
        // WHEN
        long newSize = quoteEntityService.count();
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
        long oldSize = quoteEntityService.count();

        // WHEN
        UserEntity ue = new UserEntity(author_id);
        QuoteEntity quoteEntity = getQuoteEntity(quote_id);
        quoteEntity.setAuthor(ue);
        QuoteEntity updatedEntity = assertDoesNotThrow(() -> quoteEntityService.update(quote_id, quoteEntity));
        // THEN
        assertAll(
                () -> assertNotNull(updatedEntity.getId()),
                () -> assertNotNull(updatedEntity.getAuthor()),
                () -> assertNotNull(updatedEntity.getSource().getId()),
                () -> assertNotNull(updatedEntity.getSource().getType()),
                () -> assertNotNull(updatedEntity.getSource().getType().getId()),
                () -> assertNotNull(updatedEntity.getText()),
                () -> assertEquals(quote_id, updatedEntity.getId()),
                () -> assertEquals(author_id, updatedEntity.getAuthor().getId())
        );
        // WHEN
        long newSize = quoteEntityService.count();
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
        long oldSize = quoteEntityService.count();

        // WHEN
        UserEntity ue = getUserEntity(author_id);
        QuoteEntity quoteEntity = getQuoteEntity(quote_id);
        quoteEntity.setAuthor(ue);
        QuoteEntity updatedEntity = assertDoesNotThrow(() -> quoteEntityService.update(quote_id, quoteEntity));
        // THEN
        assertNotNull(updatedEntity.getId());
        assertNotNull(updatedEntity.getAuthor());
        assertNotNull(updatedEntity.getSource().getId());
        assertNotNull(updatedEntity.getSource().getType());
        assertNotNull(updatedEntity.getSource().getType().getId());
        assertNotNull(updatedEntity.getText());
        assertEquals(quote_id, updatedEntity.getId());
        assertEquals(author_id, updatedEntity.getAuthor().getId());

        // WHEN
        long newSize = quoteEntityService.count();
        // THEN
        assertEquals(0, Math.abs(oldSize - newSize));
    }

    @Test
    @Tag("delete_entry")
    @DisplayName("Удаление entity по id")
    void _09_deleteEntityById() {
        // GIVEN
        int id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
        assertTrue(quoteEntityService.getOne(id).isPresent());
        // WHEN
        long oldSize = quoteEntityService.count();
        assertDoesNotThrow(() -> quoteEntityService.deleteById(id));
        long newSize = quoteEntityService.count();
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
        assertTrue(quoteEntityService.getOne(id).isPresent());
        // WHEN
        QuoteEntity qe = new QuoteEntity(id);
        long oldSize = quoteEntityService.count();
        assertDoesNotThrow(() -> quoteEntityService.delete(qe));
        long newSize = quoteEntityService.count();
        // THEN
        assertEquals(1, Math.abs(oldSize - newSize));
        assertFalse(quoteEntityService.getOne(id).isPresent());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("_11_createEntityWithIncorrectText")
    void _11_createEntityWithIncorrectText(String text) {
        // GIVEN
        int source_id = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES + 1);
        int author_id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
        // WHEN
        long oldSize = quoteEntityService.count();
        UserEntity userEntity = new UserEntity(author_id);
        QuoteSource quoteSource = new QuoteSource(source_id);
        QuoteEntity qe = new QuoteEntity(text, userEntity, quoteSource);
        assertThrows(IncorrectField.class, () -> quoteEntityService.create(qe));
        long newSize = quoteEntityService.count();

        // THEN
        assertEquals(oldSize, newSize);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0, MAX_QUOTE_SOURCE_ENTITIES + 1, -1 * MAX_QUOTE_SOURCE_ENTITIES})
    @DisplayName("_12_createEntityWithIncorrectSource")
    void _12_createEntityWithIncorrectSource(Integer source_id) {
        // GIVEN
        int author_id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
        String text = faker.quote().text();
        // WHEN
        long oldSize = quoteEntityService.count();
        UserEntity userEntity = new UserEntity(author_id);
        QuoteSource quoteSource = new QuoteSource(source_id);
        QuoteEntity qe = new QuoteEntity(text, userEntity, quoteSource);
        assertThrows(IncorrectField.class, () -> quoteEntityService.create(qe));
        long newSize = quoteEntityService.count();

        // THEN
        assertEquals(oldSize, newSize);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0, MAX_USER_ENTITIES + 1, -1 * MAX_USER_ENTITIES})
    @DisplayName("_13_createEntityWithIncorrectAuthor")
    void _13_createEntityWithIncorrectAuthor(Integer author_id) {
        // GIVEN
        int source_id = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES + 1);
        String text = faker.quote().text();
        // WHEN
        long oldSize = quoteEntityService.count();
        UserEntity userEntity = new UserEntity(author_id);
        QuoteSource quoteSource = new QuoteSource(source_id);
        QuoteEntity qe = new QuoteEntity(text, userEntity, quoteSource);
        assertThrows(IncorrectField.class, () -> quoteEntityService.create(qe));
        long newSize = quoteEntityService.count();

        // THEN
        assertEquals(oldSize, newSize);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("_14_updateEntityWithIncorrectText")
    void _14_updateEntityWithIncorrectText(String text) {
        // GIVEN
        int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
        long oldSize = quoteEntityService.count();

        // WHEN
        QuoteEntity quoteEntity = getQuoteEntity(quote_id);
        quoteEntity.setText(text);
        // THEN
        assertThrows(IncorrectField.class, () -> quoteEntityService.update(quote_id, quoteEntity));

        // WHEN
        long newSize = quoteEntityService.count();
        // THEN
        assertEquals(oldSize, newSize);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0, MAX_QUOTE_SOURCE_ENTITIES + 1, -1 * MAX_QUOTE_SOURCE_ENTITIES})
    @DisplayName("_15_updateEntityWithIncorrectSource")
    void _15_updateEntityWithIncorrectSource(Integer source_id) {
        // GIVEN
        int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
        long oldSize = quoteEntityService.count();

        // WHEN
        QuoteSource qs = new QuoteSource(source_id);
        QuoteEntity quoteEntity = getQuoteEntity(quote_id);
        quoteEntity.setSource(qs);
        // THEN
        assertThrows(IncorrectField.class, () -> quoteEntityService.update(quote_id, quoteEntity));

        // WHEN
        long newSize = quoteEntityService.count();
        // THEN
        assertEquals(oldSize, newSize);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0, MAX_USER_ENTITIES + 1, -1 * MAX_USER_ENTITIES})
    @DisplayName("_16_updateEntityWithIncorrectAuthor")
    void _16_updateEntityWithIncorrectAuthor(Integer author_id) {
        // GIVEN
        int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
        long oldSize = quoteEntityService.count();

        // WHEN
        UserEntity ue = new UserEntity(author_id);
        QuoteEntity quoteEntity = getQuoteEntity(quote_id);
        quoteEntity.setAuthor(ue);
        // THEN
        assertThrows(IncorrectField.class, () -> quoteEntityService.update(quote_id, quoteEntity));

        // WHEN
        long newSize = quoteEntityService.count();
        // THEN
        assertEquals(oldSize, newSize);
    }

    @Test
    @DisplayName("Get View value")
    void _17_getView() {
        // GIVEN
        int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
        // WHEN
        Optional<QuoteEntity> quoteEntityOptional = quoteEntityService.getOne(quote_id);
        Optional<QuoteEntityInfo> quoteEntityInfoOptional = quoteEntityService.getOneInfo(quote_id);

        assertTrue(quoteEntityOptional.isPresent());
        QuoteEntity quoteEntity = quoteEntityOptional.get();

        assertTrue(quoteEntityInfoOptional.isPresent());
        QuoteEntityInfo quoteEntityInfo = quoteEntityInfoOptional.get();
        // THEN
        assertNotNull(quoteEntity.getAuthor());
        assertNotNull(quoteEntity.getSource());
        assertNotNull(quoteEntity.getSource().getType());
        assertAll(
                () -> assertEquals(quoteEntity.getId(), quoteEntityInfo.getId()),
                () -> assertEquals(quoteEntity.getText(), quoteEntityInfo.getText()),
                () -> assertEquals(quoteEntity.getAuthor().getLogin(), quoteEntityInfo.getAuthorLogin()),
                () -> assertEquals(quoteEntity.getAuthor().getName(), quoteEntityInfo.getAuthorName()),
                () -> assertEquals(quoteEntity.getSource().getSourceName(), quoteEntityInfo.getSourceName()),
                () -> assertEquals(quoteEntity.getSource().getType().getType(), quoteEntityInfo.getSourceType())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, MAX_QUOTE_ENTITIES + 1})
    @DisplayName("Get View value with incorrect id")
    void _18_getViewIncorrect(Integer quote_id) {
        // GIVEN
        // WHEN
        Optional<QuoteEntity> quoteEntityOptional = quoteEntityService.getOne(quote_id);
        Optional<QuoteEntityInfo> quoteEntityInfoOptional = quoteEntityService.getOneInfo(quote_id);
        // THEN
        assertFalse(quoteEntityOptional.isPresent());
        assertFalse(quoteEntityInfoOptional.isPresent());
    }

    @Test
    @DisplayName("Get View value paged")
    void _19_getViewPaged() {
        // GIVEN
        int size = 5;
        Pageable pageable = PageRequest.of(0, size, Sort.Direction.ASC, "id");

        // WHEN
        Page<QuoteEntity> quoteEntityPage = quoteEntityService.findAll(pageable);
        Page<QuoteEntityInfo> quoteEntityInfoPage = quoteEntityService.findInfoAll(pageable);
        List<QuoteEntity> quoteEntities = quoteEntityPage.get().toList();
        List<QuoteEntityInfo> quoteEntityInfos = quoteEntityInfoPage.get().toList();

        // THEN
        assertFalse(quoteEntityPage.isEmpty());
        assertEquals(quoteEntityPage.getSize(), size);
        assertFalse(quoteEntityInfoPage.isEmpty());
        assertEquals(quoteEntityInfoPage.getSize(), size);

        for (int i = 0; i < size; i++) {
            QuoteEntity quoteEntity = quoteEntities.get(i);
            QuoteEntityInfo quoteEntityInfo = quoteEntityInfos.get(i);
            assertNotNull(quoteEntity.getAuthor());
            assertNotNull(quoteEntity.getSource());
            assertNotNull(quoteEntity.getSource().getType());
            assertAll(
                    () -> assertEquals(quoteEntity.getId(), quoteEntityInfo.getId()),
                    () -> assertEquals(quoteEntity.getText(), quoteEntityInfo.getText()),
                    () -> assertEquals(quoteEntity.getAuthor().getLogin(), quoteEntityInfo.getAuthorLogin()),
                    () -> assertEquals(quoteEntity.getAuthor().getName(), quoteEntityInfo.getAuthorName()),
                    () -> assertEquals(quoteEntity.getSource().getSourceName(), quoteEntityInfo.getSourceName()),
                    () -> assertEquals(quoteEntity.getSource().getType().getType(), quoteEntityInfo.getSourceType())
            );
        }
    }

    @Test
    @DisplayName("_20_getViewIdsById")
    void _20_getViewIdsById() {
        // GIVEN
        int quote_id = rnd.nextInt(1, MAX_QUOTE_ENTITIES + 1);
        // WHEN
        Optional<QuoteEntity> quoteEntityOptional = quoteEntityService.getOne(quote_id);
        Optional<QuoteEntityIdsInfo> quoteEntityIdsInfoOptional = quoteEntityService.getOneIdsInfo(quote_id);
        // THEN
        assertTrue(quoteEntityOptional.isPresent());
        assertTrue(quoteEntityIdsInfoOptional.isPresent());

        // WHEN
        QuoteEntity quoteEntity = quoteEntityOptional.get();
        QuoteEntityIdsInfo quoteEntityInfo = quoteEntityIdsInfoOptional.get();
        // THEN
        assertNotNull(quoteEntity.getAuthor());
        assertNotNull(quoteEntity.getSource());
        assertNotNull(quoteEntity.getSource().getType());

        assertAll(
                () -> assertEquals(quoteEntity.getId(), quoteEntityInfo.getId()),
                () -> assertEquals(quoteEntity.getAuthor().getId(), quoteEntityInfo.getAuthorId()),
                () -> assertEquals(quoteEntity.getSource().getId(), quoteEntityInfo.getSourceId()),
                () -> assertEquals(quoteEntity.getSource().getType().getId(), quoteEntityInfo.getSourceTypeId())
        );
    }

    @Test
    @DisplayName("_20_getViewIdsById")
    void _21_getViewIdsByPage() {
        // GIVEN
        int size = 5;
        Pageable pageable = PageRequest.of(0, size, Sort.Direction.ASC, "id");
        // WHEN
        Page<QuoteEntity> quoteEntityPage = quoteEntityService.findAll(pageable);
        Page<QuoteEntityIdsInfo> quoteEntityInfoPage = quoteEntityService.findIdsInfoAll(pageable);
        List<QuoteEntity> quoteEntities = quoteEntityPage.get().toList();
        List<QuoteEntityIdsInfo> quoteEntityInfos = quoteEntityInfoPage.get().toList();
        // THEN
        assertFalse(quoteEntityPage.isEmpty());
        assertEquals(quoteEntityPage.getSize(), size);
        assertFalse(quoteEntityInfoPage.isEmpty());
        assertEquals(quoteEntityInfoPage.getSize(), size);

        for (int i = 0; i < size; i++) {
            QuoteEntity quoteEntity = quoteEntities.get(i);
            QuoteEntityIdsInfo quoteEntityInfo = quoteEntityInfos.get(i);
            assertNotNull(quoteEntity.getAuthor());
            assertNotNull(quoteEntity.getSource());
            assertNotNull(quoteEntity.getSource().getType());
            assertAll(
                    () -> assertEquals(quoteEntity.getId(), quoteEntityInfo.getId()),
                    () -> assertEquals(quoteEntity.getAuthor().getId(), quoteEntityInfo.getAuthorId()),
                    () -> assertEquals(quoteEntity.getSource().getId(), quoteEntityInfo.getSourceId()),
                    () -> assertEquals(quoteEntity.getSource().getType().getId(), quoteEntityInfo.getSourceTypeId())
            );
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, MAX_QUOTE_ENTITIES + 1})
    @DisplayName("_20_getViewIdsById")
    void _22_getViewIdsByWrongId(int quote_id) {
        // GIVEN
        // WHEN
        Optional<QuoteEntity> quoteEntityOptional = quoteEntityService.getOne(quote_id);
        Optional<QuoteEntityIdsInfo> quoteEntityInfoOptional = quoteEntityService.getOneIdsInfo(quote_id);
        // THEN
        assertFalse(quoteEntityOptional.isPresent());
        assertFalse(quoteEntityInfoOptional.isPresent());
    }

    @Test
    @Tag("add_entry")
    @DisplayName("Добавление нового элемента в БД")
    void _23_addNewValueV2() {
        // GIVEN
        int source_id = rnd.nextInt(1, MAX_QUOTE_SOURCE_ENTITIES + 1);
        int author_id = rnd.nextInt(1, MAX_USER_ENTITIES + 1);
        String text = faker.lorem().characters(6, 32);

        QuoteEntityIdsInfo entityIdsInfo = new QuoteEntityIdsInfoImpl(null, text, source_id, author_id);

        // WHEN
        long oldSize = quoteEntityService.count();
        QuoteEntityIdsInfo createdEntity = assertDoesNotThrow(() -> quoteEntityService.create(entityIdsInfo));
        long newSize = quoteEntityService.count();

        // THEN
        assertEquals(1, Math.abs(oldSize - newSize));
        assertAll(
                () -> assertNotNull(createdEntity.getId()),
                () -> assertNotNull(createdEntity.getSourceId()),
                () -> assertNotNull(createdEntity.getSourceTypeId()),
                () -> assertNotNull(createdEntity.getAuthorId()),
                () -> assertNotNull(createdEntity.getText()),
                () -> assertEquals(text, createdEntity.getText()),
                () -> assertEquals(author_id, createdEntity.getAuthorId()),
                () -> assertEquals(source_id, createdEntity.getSourceId())
        );
    }
}