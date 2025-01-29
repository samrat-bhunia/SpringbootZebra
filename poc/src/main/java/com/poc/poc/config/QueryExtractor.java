package com.poc.poc.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class QueryExtractor {

	@Autowired
	@Qualifier(value = "sqlSessionFactory")
	private SqlSessionFactory postgresSqlSessionFactory;

	@PostConstruct
	public void logMapperQueriesAndWriteToExcel() throws JsonProcessingException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("SQL Queries");

		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("SQL Query");
		headerRow.createCell(1).setCellValue("Parameters");

		CellStyle wrapTextStyle = workbook.createCellStyle();
		wrapTextStyle.setWrapText(true);
		wrapTextStyle.setShrinkToFit(false);

		Configuration configuration = postgresSqlSessionFactory.getConfiguration();
		Iterator<MappedStatement> mappedStatements = configuration.getMappedStatements().iterator();

		Set<String> uniqueQueries = new HashSet<>();

		int rowIndex = 1;
		while (mappedStatements.hasNext()) {
			MappedStatement mappedStatement = mappedStatements.next();
			BoundSql boundSql = mappedStatement.getBoundSql(null);
			String sqlQuery = boundSql.getSql();

			if (uniqueQueries.contains(sqlQuery)) {
				continue;
			}

			uniqueQueries.add(sqlQuery);
			
			String modifiedSqlQuery = sqlQuery;
	        for (int i = 0; i < boundSql.getParameterMappings().size(); i++) {
	            String paramName = boundSql.getParameterMappings().get(i).getProperty();
	            modifiedSqlQuery = modifiedSqlQuery.replaceFirst("\\?", ":"+ paramName);
	        }
	        System.err.println(modifiedSqlQuery);
	        
	        Row row = sheet.createRow(rowIndex++);
			Cell sqlQueryCell = row.createCell(0);
			sqlQueryCell.setCellValue(modifiedSqlQuery);
			sqlQueryCell.setCellStyle(wrapTextStyle);

			Cell paramsCell = row.createCell(1);
			paramsCell.setCellValue(convertParameterMappingsToJson(boundSql));
			paramsCell.setCellStyle(wrapTextStyle);
		}

		sheet.setColumnWidth(0, 25000);
		sheet.setColumnWidth(1, 10000);

		try (FileOutputStream fileOut = new FileOutputStream("queries.xlsx")) {
			workbook.write(fileOut);
		} catch (IOException e) {
			System.err.println("Error writing to Excel file: " + e.getMessage());
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				System.err.println("Error closing workbook: " + e.getMessage());
			}
		}
	}

	private static String convertParameterMappingsToJson(BoundSql boundSql) {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> parameterMap = new HashMap<>();

		for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
			String property = parameterMapping.getProperty();
			parameterMap.put(property, String.format("[:%s]", property));
		}

		try {
			return objectMapper.writeValueAsString(parameterMap);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}"; // Return empty JSON if error occurs
		}
	}

	// @PostConstruct
	public void logMapperQueries() {
		Configuration configuration = postgresSqlSessionFactory.getConfiguration();
		configuration.getMappedStatements().forEach(mappedStatement -> {
			System.out.println("Loaded query: " + mappedStatement.getId());
			BoundSql boundSql = mappedStatement.getBoundSql(null);
			String sqlQuery = boundSql.getSql();

			System.out.println("SQL Query: " + sqlQuery);
			if (mappedStatement.getId().contains("selectAllTransaction")) {
				try (Connection connection = postgresSqlSessionFactory.openSession().getConnection();
						PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
						ResultSet resultSet = preparedStatement.executeQuery()) {

					ResultSetMetaData metaData = resultSet.getMetaData();
					int columnCount = metaData.getColumnCount();

					while (resultSet.next()) {
						for (int i = 1; i <= columnCount; i++) {
							System.out.print(resultSet.getString(i) + " ");
						}
						System.out.println();
					}
					System.out.println();
				} catch (SQLException e) {
					System.err.println("Error executing query: " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

}
