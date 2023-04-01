package com.logic.account.account.personal.rest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.logic.account.account.Card;
import com.logic.account.account.ClientAccount;
import com.logic.account.account.Deposit;
import com.logic.account.account.personal.PersonalAccount;
import com.logic.account.account.personal.PersonalCard;
import com.logic.account.account.personal.PersonalClientAccount;
import com.logic.account.account.personal.PersonalDeposit;
import com.logic.account.account.rest.AccountRepository;
import com.logic.account.user.Client;
import com.logic.account.user.rest.ClientRepository;
import com.logic.account.utils.Currency;
import com.logic.account.utils.ValidThrough;
import com.logic.account.utils.rest.CurrencyRepository;

import jakarta.persistence.DiscriminatorValue;
import lombok.*;

@Service
public class PersonalService {
    @Autowired private PersonalRepository persRep;
    @Autowired private ClientRepository clntRep;
    @Autowired private AccountRepository accRep;
    @Autowired private CurrencyRepository curRep;
    @Autowired private PersonalMapper accMapper;

    public List<PersonalAccount> findByClientId(UUID id) {
        return persRep.findByClientId(id);
    }

    public Optional<PersonalAccount> findById(UUID id) {
        return persRep.findById(id);
    }

    public Optional<PersonalAccount> findByIdWithAll(UUID id) {
        return persRep.findByIdWithAll(id);
    }

    public Optional<PersonalAccount> findCardByCardNo(BigInteger cardNo) {
        return persRep.findCardByCardNo(cardNo);
    }

    public Optional<PersonalAccount> findCardByCardNoWithAll(BigInteger cardNo) {
        return persRep.findCardByCardNoWithAll(cardNo);
    }

    public Optional<PersonalAccount> findDepositByAccountNo(BigInteger accNo) {
        return persRep.findDepositByAccountNo(accNo);
    }

    public Optional<PersonalAccount> findDepositByAccountNoWithAll(BigInteger accNo) {
        return persRep.findDepositByAccountNoWithAll(accNo);
    }

    public PersonalAccount update(UUID id, PersonalAccount accDetails) {
        return persRep.findById(id).map(acc -> {
            accMapper.update(accDetails, acc);
            return persRep.save(acc);
        })
        .orElseThrow();
    }

    public PersonalAccount save(PersonalAccount acc) {
        return persRep.save(acc);
    }

    @Transactional
    public void deleteCard(UUID persId, UUID clntId) {
        persRep.deleteByIdAndClientId(persId, clntId);
    }
    
    public void trySetAnyDefault(Client clnt) {
        persRep.findFirstByClient(clnt).ifPresent(it -> {
            clnt.setDefaultAccount(it);
            clntRep.save(clnt);
        });
    }

    @Transactional
    public void setAnyDefaultIfNotPresent(UUID clntId) {
        val clnt = clntRep.findById(clntId).orElseThrow(() -> {
            throw new NoSuchElementException("client is not present"); 
        });

        if (clnt.getDefaultAccount() == null)
            trySetAnyDefault(clnt); 
    }

    public void setDefault(UUID clntId, UUID persId) {
        val clnt = clntRep.findById(clntId).orElseThrow(() -> {
            throw new NoSuchElementException("client is not present"); 
        });
 
        val acc = persRep.findByIdAndClientId(persId, clntId).orElseThrow(() -> {
            throw new NoSuchElementException("client doesn't own this accounts"); 
        });
        clnt.setDefaultAccount(acc);
        
        clntRep.save(clnt);
    }

    public PersonalAccount makePersonalCard(BigDecimal money, Client clnt, UUID accountId, Currency currency) {
        val acc = accRep.findById(accountId).orElseThrow(() -> {
            throw new NoSuchElementException("account type is not present for current transaction"); 
        });
        
        PersonalAccount pAcc = null;

        val rand = new Random();

        if (acc instanceof Card) {
            BigInteger cardNo;
            while (true) {
                var ans = "";
                for(int i = 0; i < 16; ++i)
                    ans += rand.nextInt(1, 10);
                
                cardNo = new BigInteger(ans);
                if (!persRep.existsByCardNo(cardNo))
                    break;
            }

            val cvc = rand.nextInt(111, 999);
            val data = LocalDate.now().plusMonths(7);
            val valid = ValidThrough.of(data.getMonthValue(), data.getYear() % 100);

            pAcc = new PersonalCard(clnt, acc, currency, cardNo, cvc, valid); 
        } else {
            BigInteger accNo;
            while (true) {
                var ans = "";
                for(int i = 0; i < 10; ++i)
                    ans += rand.nextInt(1, 10);

                accNo = new BigInteger(ans);
                if (!persRep.existsByAccountNo(accNo) && !persRep.existsByAccountNo2(accNo))
                    break;
            }
            
            if (acc instanceof Deposit) {
                pAcc = new PersonalDeposit(clnt, acc, currency, accNo);
            } else if (acc instanceof ClientAccount) {
                var min = ((ClientAccount)acc).getMinAmount();
                min = convert(840, currency.getCode(), min);
                
                if (money.compareTo(min) < 0)
                    throw new IllegalArgumentException("Too small sum");

                pAcc = new PersonalClientAccount(clnt, acc, currency, accNo);
            }
        }
        
        if (pAcc == null)
            throw new IllegalArgumentException("Wrong type for personal account");

        return persRep.save(pAcc);
    }

    public BigDecimal convert(Integer fromCode, Integer toCode, BigDecimal money) {
        val from = curRep.findById(fromCode).orElseThrow(() -> {
            throw new NoSuchElementException("No currency with code %d".formatted(fromCode)); 
        });
        val to = curRep.findById(toCode).orElseThrow(() -> {
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

    public boolean isMoreThan1(Client clnt) {
        return persRep.isPresent(clnt);
    }
}
