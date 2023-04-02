package util;

import faker.QuoteFake;
import faker.QuoteServiceFaker;
import faker.QuoteSourceFake;
import net.datafaker.service.RandomService;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.lanolin.quoter.backend.domain.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.IntStream;

import static util.Utils.*;

public final class DbTestUtil {

    public record SequenceInfo(String tableName, int start) {
    }

    private DbTestUtil() {
    }

    public static void updateSequence(Connection connection, String tableName, Integer value) throws SQLException {
        String SEQUENCE_UPDATE = "ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH ?";
        try (PreparedStatement updateSeq = connection.prepareStatement(SEQUENCE_UPDATE)) {
            updateSeq.setInt(1, value);
            updateSeq.execute();
        }
    }

    public static void fillUserEntities(ApplicationContext applicationContext, List<UserEntity> userEntities) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement addUser = connection.prepareStatement("insert into user_entity(id, login, name, password) values (?, ?, ?, ?)");
                 PreparedStatement addRoles = connection.prepareStatement("insert into user_entity_roles(user_entity_id, roles) values (?, ?)")) {
                for (UserEntity user : userEntities) {
                    addUser.setInt(1, user.getId());
                    addUser.setString(2, user.getLogin());
                    addUser.setString(3, user.getName());
                    addUser.setString(4, user.getPassword());
                    addUser.addBatch();
                    for (UserRoles role : user.getRoles()) {
                        addRoles.setInt(1, user.getId());
                        addRoles.setString(2, role.toString());
                        addRoles.addBatch();
                    }
                }
                addUser.executeBatch();
                addRoles.executeBatch();
            }
            updateSequence(connection, "user_entity", userEntities.size() + 1);
            connection.commit();
        }
    }

    public static void dropUserEntities(ApplicationContext applicationContext) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DELETE FROM user_entity_roles");
                statement.execute("DELETE FROM user_entity");
            }
        }
    }

    public static void fillQuoteSourceTypes(ApplicationContext applicationContext, List<QuoteSourceType> sourceTypes) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement addQST = connection.prepareStatement("insert into quote_source_type_entity(id, type) values (?, ?)")) {
                for (QuoteSourceType type : sourceTypes) {
                    addQST.setInt(1, type.getId());
                    addQST.setString(2, type.getType());
                    addQST.addBatch();
                }
                addQST.executeBatch();
            }
            updateSequence(connection, "quote_source_type_entity", sourceTypes.size() + 1);
            connection.commit();
        }
    }

    public static void dropQuoteSourceTypes(ApplicationContext applicationContext) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DELETE FROM quote_source_type_entity");
            }
        }
    }

    public static void fillQuoteSources(ApplicationContext applicationContext, List<QuoteSource> sources) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement addQS = connection.prepareStatement("insert into quote_source_entity(id, source_name, type_id) values (?, ?, ?)")) {
                for (QuoteSource source : sources) {
                    addQS.setInt(1, source.getId());
                    addQS.setString(2, source.getSourceName());
                    addQS.setInt(3, source.getType().getId());
                    addQS.addBatch();
                }
                addQS.executeBatch();
            }
            updateSequence(connection, "quote_source_entity", sources.size() + 1);
            connection.commit();
        }
    }

    public static void dropQuoteSources(ApplicationContext applicationContext) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("delete from quote_source_entity");
            }
        }
    }

    public static void fillQuoteEntity(ApplicationContext applicationContext, List<QuoteEntity> quoteEntities) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement addQS = connection.prepareStatement("insert into QUOTE_ENTITY(id, AUTHOR_ID, SOURCE_ID, TEXT) values (?, ?, ?, ?)")) {
            connection.setAutoCommit(false);
            for (QuoteEntity entity : quoteEntities) {
                addQS.setInt(1, entity.getId());
                addQS.setInt(2, entity.getAuthor().getId());
                addQS.setInt(3, entity.getSource().getId());
                addQS.setString(4, entity.getText());
                addQS.addBatch();
            }
            addQS.executeBatch();
            updateSequence(connection, "quote_entity", quoteEntities.size() + 1);
            connection.commit();
        }
    }

    public static void dropQuoteEntity(ApplicationContext applicationContext) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("delete from quote_entity");
            }
        }
    }

    public static void resetAutoIncrementColumns(ApplicationContext applicationContext, SequenceInfo... sequences) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        String resetSqlTemplate = getResetSqlTemplate(applicationContext);
        try (Connection dbConnection = dataSource.getConnection()) {
            for (SequenceInfo sequence : sequences) {
                try (Statement statement = dbConnection.createStatement()) {
                    String resetSql = String.format(resetSqlTemplate, sequence.tableName(), sequence.start());
                    statement.execute(resetSql);
                }
            }
        }
    }

    private static String getResetSqlTemplate(ApplicationContext applicationContext) {
        //Read the SQL template from the properties file
        Environment environment = applicationContext.getBean(Environment.class);
        return environment.getRequiredProperty("test.reset.sql.template");
    }

    public static void generateUserEntityInDb(ApplicationContext applicationContext, QuoteServiceFaker faker, BCryptPasswordEncoder encoder) throws SQLException {
        List<UserEntity> userEntities = IntStream.range(0, MAX_USER_ENTITIES)
                .mapToObj(i -> faker.userEntity().userEntity(i + 1))
                .peek(user -> user.setPassword(encoder.encode(user.getPassword())))
                .toList();
        DbTestUtil.fillUserEntities(applicationContext, userEntities);
    }

    public static void generateQuoteSourceTypeInDb(ApplicationContext applicationContext, QuoteServiceFaker faker) throws SQLException {
        List<QuoteSourceType> sourceTypes = IntStream.range(0, MAX_QUOTE_SOURCE_TYPE_ENTITIES)
                .mapToObj(i -> faker.quoteSourceType().quoteSourceType(i + 1))
                .toList();
        fillQuoteSourceTypes(applicationContext, sourceTypes);
    }

    public static void generateQuoteSourceInDb(ApplicationContext applicationContext, QuoteServiceFaker faker) throws SQLException {
        RandomService random = faker.random();
        QuoteSourceFake fake = faker.quoteSource();
        List<QuoteSource> quoteSources = IntStream.range(0, MAX_QUOTE_SOURCE_ENTITIES)
                .mapToObj(i -> fake.quoteSource(i + 1, random.nextInt(1, MAX_QUOTE_SOURCE_TYPE_ENTITIES)))
                .toList();
        fillQuoteSources(applicationContext, quoteSources);
    }

    public static void generateQuoteEntityInDb(ApplicationContext applicationContext, QuoteServiceFaker faker) throws SQLException {
        RandomService random = faker.random();
        QuoteFake quoteFake = faker.quote();
        List<QuoteEntity> quoteEntities = IntStream.range(0, MAX_QUOTE_ENTITIES)
                .mapToObj(i -> quoteFake.quoteEntity(
                        i + 1,
                        random.nextInt(1, MAX_USER_ENTITIES),
                        random.nextInt(1, MAX_QUOTE_SOURCE_TYPE_ENTITIES))
                )
                .toList();
        fillQuoteEntity(applicationContext, quoteEntities);
    }

}
