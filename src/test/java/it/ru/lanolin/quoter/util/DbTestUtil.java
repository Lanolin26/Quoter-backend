package it.ru.lanolin.quoter.util;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DbTestUtil {

	public record SequenceInfo(String tableName, int start) {}

	private DbTestUtil() {}

	public static void resetAutoIncrementColumns(ApplicationContext applicationContext, SequenceInfo... sequences) throws SQLException {
		DataSource dataSource = applicationContext.getBean(DataSource.class);
		String resetSqlTemplate = getResetSqlTemplate(applicationContext);
		try (Connection dbConnection = dataSource.getConnection()) {
			//Create SQL statements that reset the auto increment columns and invoke
			//the created SQL statements.
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
}
