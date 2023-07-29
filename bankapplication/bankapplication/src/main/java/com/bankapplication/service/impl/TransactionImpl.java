package com.bankapplication.service.impl;

import com.bankapplication.dto.TransactionDto;
import com.bankapplication.entity.Transaction;
import com.bankapplication.repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionImpl implements TransactionService{

    @Autowired
    TransactionRepo transactionRepo;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {

    Transaction transaction = Transaction.builder()
            .transactionType(transactionDto.getTransactionType())
            .accountNumber(transactionDto.getAccountNumber())
            .amount(transactionDto.getAmount())
            .status("SUCCESS")
            .build();

    transactionRepo.save(transaction);
        System.out.println("Transaction Save Successfully");

    }
}
