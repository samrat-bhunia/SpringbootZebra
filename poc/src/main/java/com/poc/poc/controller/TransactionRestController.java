package com.poc.poc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.poc.poc.model.Transaction;
import com.poc.poc.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionRestController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/h2")
    public ResponseEntity<Transaction> createTransactionToH2(@RequestBody Transaction transaction) {
        Long transactionId = transactionService.saveTransactionToH2(transaction);
        if (transactionId != -1L) {
            return ResponseEntity.ok(transaction);
        } else {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/h2/{id}")
    public ResponseEntity<Transaction> getTransactionFromH2(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionByIdFromH2(id);
        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/postgres/{id}")
    public ResponseEntity<Transaction> getTransactionFromPostgres(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionByIdPostgreSQL(id);
        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PostMapping("/batch/postgres")
    public ResponseEntity<String> processBatchTransactionsToPostgres(@RequestBody List<Transaction> transactions) {
        try {
            transactionService.batchInsertToPostgres(transactions);
            return ResponseEntity.ok("Batch transactions processed successfully to PostgreSQL.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing batch transactions to PostgreSQL: " + e.getMessage());
        }
    }
}
