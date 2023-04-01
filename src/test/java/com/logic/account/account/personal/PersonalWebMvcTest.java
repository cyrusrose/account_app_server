package com.logic.account.account.personal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logic.account.TestConfiguration;
import com.logic.account.account.Account;
import com.logic.account.account.Card;
import com.logic.account.account.personal.rest.PersonalController;
import com.logic.account.account.personal.rest.PersonalRepository;
import com.logic.account.account.personal.rest.PersonalService;
import com.logic.account.account.rest.AccountController;
import com.logic.account.account.rest.AccountService;
import com.logic.account.user.Client;
import com.logic.account.user.Customer;
import com.logic.account.user.phone.Phone;
import com.logic.account.utils.Currency;
import com.logic.account.utils.ValidThrough;

import lombok.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@WebMvcTest(controllers = PersonalController.class)
public class PersonalWebMvcTest {
    @Autowired MockMvc mvc;
    @MockBean PersonalService accSrv;
    @SpyBean PersonalController accCtr;

    Currency USD = Currency.of(840, "USD", BigDecimal.valueOf(1));
    ObjectMapper mapper = new ObjectMapper();
    Client client = new Customer(Phone.of("+1 (999) 999-99-91"), "Bob", "Robinson");
    Account acc = new Card(
        1, "#F48FB1", "Description", "Card",
        BigDecimal.valueOf(5L)
    );

    @Test
    void getCard() throws Exception {
        acc.setId(UUID.randomUUID());
        client.setId(UUID.randomUUID());
        PersonalCard pAcc = new PersonalCard(
            client, acc, USD, 
            new BigInteger("4444111122223331"), 123, ValidThrough.of(11, 27)
        );
        pAcc.setId(UUID.randomUUID());

        when(accSrv.findCardByCardNoWithAll(ArgumentMatchers.any(BigInteger.class)))
            .thenReturn(Optional.of(pAcc));

        val accResp = mvc.perform(get("/api/v1/card_with_all")
            .queryParam("cardNo", "4444111122223331"))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();
        
        val accAns = mapper.readValue(
            accResp.getResponse().getContentAsString(), PersonalAccount.class
        );

        verify(accCtr).getCardWithAll(ArgumentMatchers.any(BigInteger.class));
        assertThat(accAns, is(instanceOf(PersonalCard.class)));
    }    
}
