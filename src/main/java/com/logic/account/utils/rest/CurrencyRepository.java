package com.logic.account.utils.rest;

import org.springframework.stereotype.Repository;

import com.logic.account.utils.Currency;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    @Query("SELECT p.currency FROM PersonalAccount p WHERE p.id = ?1")
    Currency findByPersonal(UUID id);
}
