package it.ru.lanolin.quoter.util;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@TestConfiguration
public class DbUnitConfiguration {

	@Bean
	public IDataTypeFactory dataTypeFactory() {
		return new H2DataTypeFactory();
	}

	@Bean
	public DatabaseConfigBean dbUnitDatabaseConfig() {
		DatabaseConfigBean databaseConfigBean = new DatabaseConfigBean();
		databaseConfigBean.setDatatypeFactory(dataTypeFactory());
		return databaseConfigBean;
	}

	@Bean
	public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection(DataSource dataSource) {
		DatabaseDataSourceConnectionFactoryBean databaseDataSourceConnectionFactoryBean =
				new DatabaseDataSourceConnectionFactoryBean(dataSource);
		databaseDataSourceConnectionFactoryBean.setDatabaseConfig(dbUnitDatabaseConfig());
		return databaseDataSourceConnectionFactoryBean;
	}

}
