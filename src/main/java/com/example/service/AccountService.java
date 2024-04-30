package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {

    AccountRepository accountRepository;
    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }
    public Account findByAccountId(Integer accountId) {
        System.out.println("jgfdgdfghsdlgfsdfgj");
        return accountRepository.findAccount(accountId);
    }

    

}
