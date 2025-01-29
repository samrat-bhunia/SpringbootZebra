package com.poc.poc.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DataSourceConfig {

	private String dbType = System.getProperty("db.type");

	@Value("${client.name}")
	private String clientName;

	private DataSource dataSource;
	private Properties properties = new Properties();

	@Value("${property.file.path}")
	private String propertyFilePath;

	private void loadProperties(String filePath) {
		try (InputStream input = new FileInputStream(filePath)) {
			properties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Bean
	public DataSource dataSource() {
		if (dataSource == null) {
			loadProperties(propertyFilePath);
			dataSource = configDataSource();
		}
		return dataSource;
	}

	private DataSource configDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		String url = String.format("%s.spring.datasource.%s.url", clientName, dbType);
		String username = String.format("%s.spring.datasource.%s.username", clientName, dbType);
		String password = String.format("%s.spring.datasource.%s.password", clientName, dbType);
		String driverClassName = String.format("%s.spring.datasource.%s.driverClassName", clientName, dbType);

		if (properties.containsKey(url) && properties.containsKey(username) && properties.containsKey(password)
				&& properties.containsKey(driverClassName)) {
			dataSource.setUrl(properties.getProperty(url));
			dataSource.setUsername(properties.getProperty(username));
			dataSource.setPassword(properties.getProperty(password));
			dataSource.setDriverClassName(properties.getProperty(driverClassName));
		} else {
			throw new IllegalArgumentException(
					"Missing required database properties: URL, username, driverClassName or " + "password.");
		}
		return dataSource;
	}

	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource());
		factoryBean
				.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
		return factoryBean.getObject();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(dataSource);
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
		factoryBean.setPackagesToScan("com.poc.poc.model");

		String ddlAuto = String.format("%s.spring.datasource.%s.jpa.hibernate.ddl-auto", clientName, dbType);
		if (properties.containsKey(ddlAuto)) {
			factoryBean.getJpaPropertyMap().put("hibernate.hbm2ddl.auto", properties.getProperty(ddlAuto));
		}

		String dialect = String.format("%s.spring.datasource.%s.jpa.properties.hibernate.dialect", clientName, dbType);
		if (properties.containsKey(dialect)) {
			factoryBean.getJpaPropertyMap().put("hibernate.dialect", properties.getProperty(dialect));
		}

		return factoryBean;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

}
