package com.logic.account.account.rest;

import static com.logic.account.utils.MyUtils.makeLocation;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logic.account.account.Account;

import lombok.*;

@RestController
@RequestMapping(path = "/api/v1/account")
public class AccountController {
    @Autowired private AccountService accServ; 

    @GetMapping("/{id}")
    public ResponseEntity<Account> get(@PathVariable("id") UUID id) {
        val acc = accServ.findById(id);
        if (acc.isPresent()) {
            return ResponseEntity.ok().body(acc.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/byNo")
    public ResponseEntity<Account> getByNo(@RequestParam(name = "no") int no) {
        val acc = accServ.findByNo(no);
        if (acc.isPresent()) {
            return ResponseEntity.ok().body(acc.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<Account>> get() {
        val acc = accServ.findAll();
        if (!acc.isEmpty()) {
            return ResponseEntity.ok().body(acc);
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(acc);
    }

    @PutMapping("/{id}")
    public void putAccount(@PathVariable("id") UUID id, @RequestBody Account acc) {
        accServ.update(id, acc);
    }

    @PostMapping
    public ResponseEntity<?> postAccount(@RequestBody Account acc) {
        val result = accServ.save(acc);

        return ResponseEntity
            .created(makeLocation(result)).build();
    }
}
