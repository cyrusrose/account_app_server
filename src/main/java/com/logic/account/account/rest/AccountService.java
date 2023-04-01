package com.logic.account.account.rest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logic.account.account.Account;

@Service
public class AccountService {
    private @Autowired AccountRepository accRep;
    private @Autowired AccountMapper accMapper;

    public Optional<Account> findById(UUID id) {
        return accRep.findById(id);
    }

    public Optional<Account> findByNo(int no) {
        return accRep.findByNo(no);
    }

    public List<Account> findAll() {
        return accRep.findAll();
    }

    public Account update(UUID id, Account clientDetails) {
        return accRep.findById(id).map(client -> {
            accMapper.update(clientDetails, client);
            return accRep.save(client);
        })
        .orElseThrow();
    }

    public Account save(Account client) {
        return accRep.save(client);
    }
}
