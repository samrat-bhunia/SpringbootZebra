package com.poc.poc.mapper;

import java.util.List;

import com.poc.poc.model.Transaction;

//@Mapper
public interface TransactionMapper {

    void insertTransaction(Transaction transaction);
    Transaction selectTransactionById(Long id);
    List<Transaction> selectAllTransaction();
}
