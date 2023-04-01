package com.logic.account.account.personal.rest;

import static com.logic.account.utils.MyUtils.makeLocation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.logic.account.account.personal.PersonalAccount;
import com.logic.account.account.personal.PersonalCard;
import com.logic.account.transactions.rest.TransactionService;
import com.logic.account.user.rest.UserService;
import com.logic.account.utils.rest.CurrencyService;

import jakarta.transaction.Transactional;
import lombok.*;

@RestController
@RequestMapping(path = "/api/v1")
public class PersonalController {
    @Autowired private PersonalService accServ;
    @Autowired private CurrencyService curServ;
    @Autowired private UserService usrServ;
    @Autowired private TransactionService trnServ;

    @GetMapping("/client/{id}/personal")
    public ResponseEntity<List<PersonalAccount>> getByClientId(
        @PathVariable("id") UUID id
    ) {
        val acc = accServ.findByClientId(id);

        if (!acc.isEmpty()) {
            return ResponseEntity.ok().body(acc);
        } else
            return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(acc);
    }

    @GetMapping("/personal_with_all/{id}")
    public ResponseEntity<PersonalAccount> getAccountWithAll(
        @PathVariable("id") UUID id
    ) throws JsonProcessingException {
        val acc = accServ.findByIdWithAll(id); 
        if (acc.isPresent()) {
            return ResponseEntity.ok().body(acc.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/personal/{id}")
    public ResponseEntity<PersonalAccount> getAccount(@PathVariable("id") UUID id) {
        val acc = accServ.findById(id); 
        if (acc.isPresent()) {
            return ResponseEntity.ok().body(acc.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/card")
    public ResponseEntity<PersonalAccount> getCard(@RequestParam("cardNo") BigInteger cardNo) {
        val acc = accServ.findCardByCardNo(cardNo);
        if (acc.isPresent()) {
            return ResponseEntity.ok().body(acc.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/card_with_all")
    public ResponseEntity<PersonalAccount> getCardWithAll(@RequestParam("cardNo") BigInteger cardNo) {
        val acc = accServ.findCardByCardNoWithAll(cardNo);
        if (acc.isPresent()) {
            return ResponseEntity.ok().body(acc.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/deposit")
    public ResponseEntity<PersonalAccount> getDeposit(@RequestParam("accNo") BigInteger accNo) {
        val acc = accServ.findDepositByAccountNo(accNo);
        if (acc.isPresent()) {
            return ResponseEntity.ok().body(acc.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/deposit_with_all")
    public ResponseEntity<PersonalAccount> getDepositWithAll(@RequestParam("accNo") BigInteger accNo) {
        val acc = accServ.findDepositByAccountNoWithAll(accNo);
        if (acc.isPresent()) {
            return ResponseEntity.ok().body(acc.get());
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/personal/{id}")
    public void putAccount(@PathVariable("id") UUID id, @RequestBody PersonalAccount acc) {
        accServ.update(id, acc);
    }

    @DeleteMapping("/client/{id}/personal/{persId}")
    public void deleteCard(@PathVariable("id") UUID clntId, @PathVariable("persId") UUID persId) {
        // tr 1
        accServ.deleteCard(persId, clntId);
        // tr 2
        accServ.setAnyDefaultIfNotPresent(clntId);
    }

    @PostMapping("/personal")
    @Transactional
    public ResponseEntity<?> postCard(
        @RequestParam(name = "money", defaultValue = "0") BigDecimal money,
        @RequestParam(name = "sender_account_id", required = false) UUID senderAccountId,
        @RequestParam(name = "account_id") UUID accountId,
        @RequestParam(name = "client_id") UUID clientId,
        @RequestParam(name = "code") int code
    ) {
        // All operations in USD
        

        val currency = curServ.findById(code).orElseThrow(() -> {
            throw new NoSuchElementException("currency with this code is not present"); 
        });

        val clnt = usrServ.findClientById(clientId).orElseThrow(() -> {
            throw new NoSuchElementException("client is not present for specified account");
        });

        val reseiverAcc = accServ.makePersonalCard(money, clnt, accountId, currency);
        if(accServ.isMoreThan1(clnt)) {
            if (senderAccountId == null)
                throw new IllegalArgumentException("sender_account_id is not specified");

            // tr 1
            val senderCurrency = curServ.findByPersonal(senderAccountId).getCode();
            money = curServ.convert(code, senderCurrency, money);
            trnServ.sendMoney(money, senderAccountId, reseiverAcc.getId(), !(reseiverAcc instanceof PersonalCard));
        }
        // tr 2
        accServ.setAnyDefaultIfNotPresent(clientId);

        return ResponseEntity
            .created(makeLocation(reseiverAcc)).build();
    }

    @PutMapping("/client/{id}/default_personal/{persId}")
    public void putDefault(@PathVariable("id") UUID clntId, @PathVariable("persId") UUID persId) {
        accServ.setDefault(clntId, persId);
    }
}
