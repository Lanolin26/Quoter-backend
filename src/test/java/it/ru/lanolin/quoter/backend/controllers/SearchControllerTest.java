package it.ru.lanolin.quoter.backend.controllers;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import ru.lanolin.quoter.QuotersLibraryApplication;
import util.DbUnitConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {QuotersLibraryApplication.class, DbUnitConfiguration.class})
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
@DisplayName("Integration test QuoteEntityController class")
@Tags({
        @Tag("integration_test"),
        @Tag("SearchController"),
})
@Slf4j
@Disabled
class SearchControllerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }
}