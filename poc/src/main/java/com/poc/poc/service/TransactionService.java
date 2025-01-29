package com.poc.poc.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.poc.poc.mapper.TransactionMapper;
import com.poc.poc.model.Transaction;

@Service
public class TransactionService {

	@Autowired
	@Qualifier(value = "sqlSessionFactory")
	private SqlSessionFactory h2SqlSessionFactory;

	//SELECT-VARIABLE
	@Autowired
	@Qualifier(value = "sqlSessionFactory")
	private SqlSessionFactory postgresSqlSessionFactory;

	//// -- Get transaction by ID from PostgreSQL
	public Transaction getTransactionByIdPostgreSQL(Long id) {
		//SELECT-VARIABLE
		Transaction transaction = null;
		try (SqlSession sqlSession = postgresSqlSessionFactory.openSession()) {
			//SELECT-VARIABLE
			TransactionMapper transactionMapper = sqlSession.getMapper(TransactionMapper.class);
			transaction = transactionMapper.selectTransactionById(id);
		} catch (Exception e) {
			System.err.println("Error fetching transaction from PostgreSQL: " + e.getMessage());
		}
		return transaction;
	}

	//// -- Get transaction by ID from H2
	public Transaction getTransactionByIdFromH2(Long id) {
		Transaction transaction = null;
		try (SqlSession sqlSession = h2SqlSessionFactory.openSession()) {
			//SELECT-VARIABLE
			TransactionMapper transactionMapper = sqlSession.getMapper(TransactionMapper.class);
			transaction = transactionMapper.selectTransactionById(id);
		} catch (Exception e) {
			System.err.println("Error fetching transaction from H2: " + e.getMessage());
		}
		return transaction;
	}

	//// -- Save transaction to H2
	public Long saveTransactionToH2(Transaction transaction) {
		try (SqlSession sqlSession = h2SqlSessionFactory.openSession()) {
			TransactionMapper transactionMapper = sqlSession.getMapper(TransactionMapper.class);
			transactionMapper.insertTransaction(transaction);
			sqlSession.commit();
			return transaction.getId();
		} catch (Exception e) {
			System.err.println("Error saving transaction to H2: " + e.getMessage());
		}
		return -1L;
	}

	//// -- Batch insert transactions to PostgreSQL
	public void batchInsertToPostgres(List<Transaction> transactions) {
		try (SqlSession sqlSession = postgresSqlSessionFactory.openSession()) {
			TransactionMapper transactionMapper = sqlSession.getMapper(TransactionMapper.class);
			for (Transaction transaction : transactions) {
				transactionMapper.insertTransaction(transaction);
			}
			sqlSession.commit();
		} catch (Exception e) {
			System.err.println("Error batch inserting transactions to PostgreSQL: " + e.getMessage());
		}
	}
	
	public List<Transaction> getAllTransactionsFromPostgreSQL() {
	    List<Transaction> transactions = new ArrayList<>();
	    String sqlQuery = "SELECT id, description, amount, transaction_date FROM transaction";
	    
	    try (SqlSession sqlSession = postgresSqlSessionFactory.openSession();
	         Connection connection = sqlSession.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
	         ResultSet resultSet = preparedStatement.executeQuery()) {

	        while (resultSet.next()) {
	            Transaction transaction = new Transaction();
	            transaction.setId(resultSet.getLong("id"));
	            transaction.setDescription(resultSet.getString("description"));
	            transaction.setAmount(resultSet.getBigDecimal("amount"));
	            transaction.setTransactionDate(resultSet.getTimestamp("transaction_date").toLocalDateTime());
	            transactions.add(transaction);
	        }
	    } catch (Exception e) {
	        System.err.println("Error executing query: " + e.getMessage());
	    }

	    return transactions;
	}
	
	@SuppressWarnings("unused")
	public List<Transaction> getAllTransactions() {
	    List<Transaction> transactions = new ArrayList<>();
	    String sqlQuery = "SELECT id, description, amount, transaction_date FROM transaction WHERE amount > 100";
	    String sqlQuery1 = "INSERT INTO transaction (description, amount, transaction_date)\r\n"
	    		+ "        VALUES (:description, :amount, :transactionDate)";
	    String sqlQuery2 = "SELECT id, description, amount, transaction_date\r\n"
	    		+ "        FROM transaction\r\n"
	    		+ "        WHERE id = :id";
	    
	    try (SqlSession sqlSession = postgresSqlSessionFactory.openSession();
	         Connection connection = sqlSession.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
	         ResultSet resultSet = preparedStatement.executeQuery()) {

	        while (resultSet.next()) {
	            Transaction transaction = new Transaction();
	            transaction.setId(resultSet.getLong("id"));
	            transaction.setDescription(resultSet.getString("description"));
	            transaction.setAmount(resultSet.getBigDecimal("amount"));
	            transaction.setTransactionDate(resultSet.getTimestamp("transaction_date").toLocalDateTime());
	            transactions.add(transaction);
	        }
	    } catch (Exception e) {
	        System.err.println("Error executing query: " + e.getMessage());
	    }

	    return transactions;
	}

}
