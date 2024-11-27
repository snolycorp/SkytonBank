package org.example.service;

import org.example.model.Account;
import org.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    public Account saveAccount(Account account){
        return accountRepository.save(account);
    }

    public List<Account> findByUserId(Long userId){
        return accountRepository.findByUserId(userId);
    }
}
