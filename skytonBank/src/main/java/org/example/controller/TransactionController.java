package org.example.controller;

import org.example.model.Account;
import org.example.model.Transaction;
import org.example.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/{accountId}")
    public String listTransactions(@PathVariable Long accountId, Model model) {
        List<Account> transactions = transactionService.getTransactionsByAccountId(accountId);
        model.addAttribute("transactions", transactions);
        return "transaction-history";
    }

    @PostMapping
    public String createTransaction(@RequestParam Long accountId, @RequestParam Double amount, @RequestParam String type, @RequestParam String description, @RequestParam String receiver) {
        transactionService.createTransaction(accountId, amount, type, description, receiver);
        return "redirect:/transactions/" + accountId;
    }
}
