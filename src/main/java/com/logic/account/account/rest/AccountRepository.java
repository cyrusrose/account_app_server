package com.logic.account.account.rest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.logic.account.account.Account;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    public Optional<Account> findByNo(int no);
}
