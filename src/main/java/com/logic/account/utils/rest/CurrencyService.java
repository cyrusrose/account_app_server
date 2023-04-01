package com.logic.account.utils.rest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logic.account.utils.Currency;

import lombok.val;

@Service
public class CurrencyService {
    @Autowired private CurrencyRepository rep;
    
    public Optional<Currency> findById(Integer id) {
        return rep.findById(id);
    }

    public List<Currency> findAll() {
        return rep.findAll();
    }

    public BigDecimal convert(Integer fromCode, Integer toCode, BigDecimal money) {
        val from = rep.findById(fromCode).orElseThrow(() -> {
            throw new NoSuchElementException("No currency with code %d".formatted(fromCode)); 
        });
        val to = rep.findById(toCode).orElseThrow(() -> {
            throw new NoSuchElementException("No currency with code %d".formatted(fromCode)); 
        });
        
        var conversion = BigDecimal.ONE;
        if (from.getCode() != to.getCode()) {
            conversion = conversion
                .divide(from.getRate(), 6, RoundingMode.HALF_UP)
                .multiply(to.getRate())
                .setScale(6, RoundingMode.HALF_UP);
        }

        return money
            .multiply(conversion)
            .setScale(6, RoundingMode.HALF_UP);
    }

    public Currency findByPersonal(UUID id) {
        return rep.findByPersonal(id);
    }
}
