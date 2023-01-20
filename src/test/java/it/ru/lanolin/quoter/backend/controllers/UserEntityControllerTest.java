package it.ru.lanolin.quoter.backend.controllers;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import faker.QuoteServiceFaker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.MockMvc;
import ru.lanolin.quoter.QuotersLibraryApplication;
import ru.lanolin.quoter.backend.repo.UserEntityRepository;
import util.DbTestUtil;
import util.DbUnitConfiguration;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.Utils.MAX_USER_ENTITIES;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {QuotersLibraryApplication.class, DbUnitConfiguration.class})
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
@DatabaseSetups(value = {
        @DatabaseSetup("classpath:user-data.xml"),
        @DatabaseSetup("classpath:quote-source-type.xml"),
        @DatabaseSetup("classpath:quote-source.xml"),
        @DatabaseSetup("classpath:quote-entity.xml"),
})
@DisplayName("Integration test UserEntityController class")
@Tags({
        @Tag("integration_test"),
        @Tag("QuoteEntityService"),
})
@Slf4j
@Disabled
class UserEntityControllerTest {

    private static Random rnd;
    private static QuoteServiceFaker faker;

    @BeforeAll
    static void beforeAll() {
        rnd = new Random(8475634875L);
        faker = new QuoteServiceFaker(new Locale("ru"), rnd);
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserEntityRepository repository;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() throws SQLException {
        DbTestUtil.resetAutoIncrementColumns(applicationContext,
                new DbTestUtil.SequenceInfo("user_entity", MAX_USER_ENTITIES + 1)
        );
    }

    @Test
    @DisplayName("_001_view_user")
    void _001_view_user() throws Exception {
        int id = faker.random().nextInt(1, MAX_USER_ENTITIES + 1);

        mvc.perform(get("/api/user/{id}", id))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.login", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.password", notNullValue()))
                .andExpect(jsonPath("$.roles", hasSize(greaterThan(0))));
    }
}