package org.example.service;
import org.example.exception.InsufficientFundsException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Account;
import org.example.model.Transaction;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TransactionService {

     @Autowired
     private TransactionRepository transactionRepository;
     @Autowired private AccountRepository accountRepository;
     @Transactional
     public Transaction createTransaction(Long accountId, Double amount, String type, String description, String receiver) {
         Account account = accountRepository.findById(accountId)
                 .orElseThrow(() -> new ResourceNotFoundException("Account not found for this id :: " + accountId));
         Double charge = calculateTransactionCharge(amount);
         Double totalAmount = amount + charge;
         validateTransaction(account, totalAmount, type);
         Transaction transaction = new Transaction();
         transaction.setAccount(account);
         transaction.setAmount(amount);
         transaction.setType(type);
         transaction.setTransactionDate(LocalDateTime.now());
         transaction.setCharge(charge);
         transaction.setDescription(description);
         transaction.setReceiver(receiver);
         updateAccountBalance(account, totalAmount, type);
         transactionRepository.save(transaction);
         accountRepository.save(account);
         return transaction;
     }
     private Double calculateTransactionCharge(Double amount) {
         if (amount <= 1000) {
             return 10.00;
         } else if (amount <= 5000) {
             return 25.00;
         } else {
             return 50.00;
         }
     }
     private void validateTransaction(Account account, Double totalAmount, String type) {
         if (type.equalsIgnoreCase("debit") && account.getBalance() < totalAmount) {
             throw new InsufficientFundsException("Insufficient funds for this transaction");
         }
     }
     private void updateAccountBalance(Account account, Double totalAmount, String type) {
         if (type.equalsIgnoreCase("debit")) {
             account.setBalance(account.getBalance() - totalAmount);
         } else if (type.equalsIgnoreCase("credit")) {
             account.setBalance(account.getBalance() + totalAmount);
         }
     }
     public List<Account> getTransactionsByAccountId(Long accountId) {
         return transactionRepository.findByAccountId(accountId);
    }
}
