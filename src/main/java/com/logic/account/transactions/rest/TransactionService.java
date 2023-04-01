package com.logic.account.transactions.rest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logic.account.account.personal.PersonalAccount;
import com.logic.account.account.personal.PersonalCard;
import com.logic.account.account.personal.PersonalClientAccount;
import com.logic.account.account.personal.PersonalDeposit;
import com.logic.account.account.personal.rest.PersonalRepository;
import com.logic.account.transactions.UserTransaction;
import com.logic.account.transactions.dtos.TransactionDto;
import com.logic.account.user.CorporateCustomer;
import com.logic.account.user.phone.Phone;
import com.logic.account.user.rest.ClientRepository;
import com.logic.account.utils.Currency;

import lombok.*;

@Service
public class TransactionService {
    @Autowired TransactionRepository trnRep;
    @Autowired ClientRepository clntRep;
    @Autowired PersonalRepository prsnRep;

    @Transactional
    public void sendMoney(BigDecimal money, UUID senderAccountId, String phoneNumber) throws IllegalArgumentException {
        if (money.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Incorrect ammount");

        val senderAccount = prsnRep.findById(senderAccountId).orElseThrow(() -> {
            throw new NoSuchElementException("sender's account is not present for current transaction"); 
        });
        val receiver = clntRep.findByPhone(Phone.of(phoneNumber)).orElseThrow(() -> {
            throw new NoSuchElementException("receiver doesn't have a phone associated with his/her account"); 
        });

        // Set default account if not present
        var receiverAccount = receiver.getDefaultAccount();
        if (receiverAccount == null) {
            receiverAccount = prsnRep.findFirstByClient(receiver).orElseThrow(() -> {
                throw new NoSuchElementException("Sender %s doesn't have any accounts yet".formatted(receiver.toString())); 
            });
            receiver.setDefaultAccount(receiverAccount);
        }
        
        if (senderAccount.getId().equals(receiverAccount.getId()))
            throw new IllegalArgumentException("It's the same account: %s".formatted(receiverAccount.toString()));

        sendMoney(senderAccount, receiverAccount, phoneNumber, money);
    }

    public void sendMoney(BigDecimal money, UUID senderAccountId, UUID receiverAccountId) {
        sendMoney(money, senderAccountId, receiverAccountId, true);
    }

    @Transactional
    public void sendMoney(BigDecimal money, UUID senderAccountId, UUID receiverAccountId, boolean failOnZero) throws IllegalArgumentException {
        if ((money.compareTo(BigDecimal.ZERO) <= 0) && failOnZero)
            throw new IllegalArgumentException("Incorrect ammount");

        val senderAccount = prsnRep.findById(senderAccountId).orElseThrow(() -> {
            throw new NoSuchElementException("sender's account is not present for current transaction"); 
        });
        val receiverAccount = prsnRep.findById(receiverAccountId).orElseThrow(() -> {
            throw new NoSuchElementException("receiver's account is not present for current transaction"); 
        });

        if (senderAccount.getId().equals(receiverAccount.getId()))
            throw new IllegalArgumentException("It's the same account: %s".formatted(receiverAccount.toString()));

        var via = encodeVia(receiverAccount);

        sendMoney(senderAccount, receiverAccount, via, money);
    }

    @Transactional
    public void sendMoney(BigDecimal money, UUID senderAccountId, BigInteger cardNo) throws IllegalArgumentException {
        if (money.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Incorrect ammount");

        val senderAccount = prsnRep.findById(senderAccountId).orElseThrow(() -> {
            throw new NoSuchElementException("sender's account is not present for current transaction"); 
        });
        val receiverAccount = prsnRep.findCardByCardNo(cardNo).orElseThrow(() -> {
            throw new NoSuchElementException("receiver's card is not present for current transaction"); 
        });

        if (senderAccount.getId().equals(receiverAccount.getId()))
            throw new IllegalArgumentException("It's the same account: %s".formatted(receiverAccount.toString()));

        var via = encodeVia(receiverAccount);

        sendMoney(senderAccount, receiverAccount, via, money);
    }

    public String encodeVia(PersonalAccount receiverAccount) {
        var via = "";
        if (receiverAccount instanceof PersonalCard) {
            val item = ((PersonalCard)receiverAccount).getCardNo().toString();
            via = "*" + item.substring(item.length() - 3, item.length());
        } else if (receiverAccount instanceof PersonalDeposit) {
            val item = ((PersonalDeposit)receiverAccount).getAccountNo().toString();
            via = "*" + item.substring(item.length() - 3, item.length());
        } else if (receiverAccount instanceof PersonalClientAccount) {
            val item = ((PersonalClientAccount)receiverAccount).getAccountNo2().toString();
            via = "*" + item.substring(item.length() - 3, item.length());
        }

        return via;
    }

    @Transactional
    public void sendMoney(BigDecimal money, UUID senderAccountId,  BigInteger clientSsn, BigInteger clientNo) throws IllegalArgumentException {
        if (money.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Incorrect ammount");
        
        val senderAccount = prsnRep.findById(senderAccountId).orElseThrow(() -> {
            throw new NoSuchElementException("sender's account is not present for current transaction"); 
        });
        val receiver = clntRep.findByClientNo(clientSsn, clientNo).orElseThrow(() -> {
            throw new NoSuchElementException("receiver doesn't have a client number associated with the specified SSN"); 
        });

        if (senderAccount.getClient().getId().equals(receiver.getId()))
            throw new IllegalArgumentException("Receiver cannot be a sender");

        // Set default account if not present
        var receiverAccount = receiver.getDefaultAccount();
        if (receiverAccount == null) {
            receiverAccount = prsnRep.findFirstByClient(receiver).orElseThrow(() -> {
                throw new NoSuchElementException("Sender %s doesn't have any accounts yet".formatted(receiver.toString())); 
            });
            receiver.setDefaultAccount(receiverAccount);
        }

        val via = clientNo.toString();
        sendMoney(senderAccount, receiverAccount, via, money);
    }

    public void sendMoney(PersonalAccount senderAccount, PersonalAccount receiverAccount, String via, BigDecimal money) {
        val currency = senderAccount.getCurrency();
        val rcurrency = receiverAccount.getCurrency();

        senderAccount.subtractMoney(money);
        var conversion = BigDecimal.ONE;
        if (currency.getCode() != rcurrency.getCode()) {
            conversion = conversion
                .divide(currency.getRate(), 6, RoundingMode.HALF_UP)
                .multiply(rcurrency.getRate())
                .setScale(6, RoundingMode.HALF_UP);
        }

        money = money.setScale(6, RoundingMode.HALF_UP);

        receiverAccount.addMoney(money.multiply(conversion).setScale(6));

        if (senderAccount.getMoney().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Not enough funds");

        val sender = senderAccount.getClient();
        val receiver = receiverAccount.getClient();

        if (sender.getId().equals(receiver.getId())) {
            val title = senderAccount.getAccount().getTitle();
            var titleRu = senderAccount.getAccount().getTitleRu();
            val rtitle = receiverAccount.getAccount().getTitle();
            val rtitleRu = receiverAccount.getAccount().getTitleRu();

            val mtitle = "From %s To %s".formatted(title, rtitle);
            String mtitleRu = null;
            if (titleRu != null && rtitleRu != null)
                mtitleRu = "Из %s В %s".formatted(titleRu, rtitleRu);

            val resTo = new UserTransaction(
                money, currency, "change", via, 
                mtitle, mtitleRu,
                sender.getId(), receiver.getId(), senderAccount.getId(), receiverAccount.getId()
            );        
            trnRep.save(resTo);
        } else {
            val title = senderAccount.getAccount().getTitle();
            var titleRu = senderAccount.getAccount().getTitleRu();
            val rtitle = receiverAccount.getAccount().getTitle();
            val rtitleRu = receiverAccount.getAccount().getTitleRu();

            val toTitle = "To %s".formatted(rtitle);
            String toTitleRu = null;
            if (rtitleRu != null) 
                toTitleRu = "Для %s".formatted(rtitleRu);

            val fromTitle = "From %s".formatted(title);
            String fromTitleRu = null;
            if (titleRu != null) 
                fromTitleRu = "От %s".formatted(titleRu);

            val resTo = new UserTransaction(
                money, currency, "to", via, 
                toTitle, toTitleRu,
                sender.getId(), receiver.getId(), senderAccount.getId(), receiverAccount.getId()
            );        
            trnRep.save(resTo);

            val viaFrom = encodeVia(senderAccount);

            val resFrom = new UserTransaction(
                money, currency, "from", viaFrom, 
                fromTitle, fromTitleRu,
                sender.getId(), receiver.getId(), senderAccount.getId(), receiverAccount.getId()
            );        
            trnRep.save(resFrom);
        }
    }

    public Optional<UserTransaction> findById(UUID id) {
        return trnRep.findById(id);
    }

    public List<UserTransaction> findByClientId(UUID id, String via, String state) {
        List<UserTransaction> tn;
        if (via != null)
            tn = trnRep.findByClientId(id, via, (state == null ? "both" : state) );
        else
            tn = trnRep.findByClientId(id, (state == null ? "both" : state) );
        return tn.stream().sorted(
            (a, b) -> { return Objects.compare(a.getTime(), b.getTime(), LocalDateTime::compareTo); }
        )
        .toList();
    }
}
