package com.logic.account.utils.rest;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logic.account.utils.Currency;

import lombok.val;

@RestController
@RequestMapping(path = "/api/v1/currency")
public class CurrencyController {
    @Autowired CurrencyService curServ;

    @GetMapping("/{code}")
    public ResponseEntity<Currency> get(@PathVariable("code") Integer code) {
        val cur = curServ.findById(code);
        if (cur.isPresent()) {
            return ResponseEntity.ok().body(cur.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }   

    @GetMapping
    public ResponseEntity<List<Currency>> get() {
        val list = curServ.findAll();
        
        if (!list.isEmpty()) {
            return ResponseEntity.ok().body(list);
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(list);
    }

    @GetMapping("/convert")
    public BigDecimal convert(
        @RequestParam(name = "from_code") Integer fromCode,
        @RequestParam(name = "to_code") Integer toCode,
        @RequestParam(name = "sum") BigDecimal money
    ) {
        return curServ.convert(fromCode, toCode, money);
    }
}
