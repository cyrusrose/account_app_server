package com.logic.account.transactions.rest;

import static com.logic.account.utils.MyUtils.makeLocation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logic.account.transactions.UserTransaction;
import com.logic.account.transactions.dtos.TransactionDto;

import lombok.*;

@RestController
@RequestMapping("api/v1")
public class TransactionController {
    @Autowired private TransactionService trnServ;

    @GetMapping("/client/{id}/transaction")
    public ResponseEntity<List<UserTransaction>> getByClientId(
        @PathVariable("id") UUID id,    
        @RequestParam(name = "state", required = false) String state,
        @RequestParam(name = "via", required = false) String via
    ) {
        List<UserTransaction> trn;
        trn = trnServ.findByClientId(id, via, state);
        
        if (!trn.isEmpty()) {
            return ResponseEntity.ok().body(trn);
        } else
            return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(trn);
    }

    @PostMapping("/transaction")
    public void sendMoney(
        @RequestParam(name = "money") BigDecimal money,
        @RequestParam(name = "sender_account_id") UUID senderAccountId,
        @RequestParam(name = "phone", required = false) String phone,
        @RequestParam(name = "client_no", required = false) BigInteger clientNo,
        @RequestParam(name = "ssn", required = false) BigInteger clientSsn,
        @RequestParam(name = "receiver_account_id", required = false) UUID receiverAccountId,
        @RequestParam(name = "card_no", required = false) BigInteger cardNo
    ) {
        if (phone != null)
            trnServ.sendMoney(money, senderAccountId, phone);
        else if (clientNo != null && clientSsn != null)
            trnServ.sendMoney(money, senderAccountId, clientSsn, clientNo);
        else if (receiverAccountId != null)
            trnServ.sendMoney(money, senderAccountId, receiverAccountId);
        else if (cardNo != null)
            trnServ.sendMoney(money, senderAccountId, cardNo);
    }


}
