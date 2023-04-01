package com.logic.account.account;

import org.hamcrest.CoreMatchers;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.logic.account.account.personal.PersonalAccount;
import com.logic.account.account.personal.PersonalCard;
import com.logic.account.account.personal.PersonalDeposit;
import com.logic.account.user.*;
import com.logic.account.user.phone.Phone;
import com.logic.account.utils.Currency;
import com.logic.account.utils.ValidThrough;

import lombok.val;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import jakarta.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AccountJpaTest {
	@Autowired TestEntityManager tem;
	Client client;
	Client client2;

	Currency USD;
	Currency RUB;
	
	ClientAccount acc;
	Card card;
	Deposit dep;

	@BeforeEach
	void setUp() {
		client = tem.persist(
			new Customer(Phone.of("+1 (999) 999-99-91"), "Bob", "Robinson")
		);
		client2 = tem.persist(
			new Customer(Phone.of("+1 (999) 999-99-92"), "Ben", "Smith")
		);

		USD = tem.persist(Currency.of(840, "USD", BigDecimal.valueOf(1)));
		RUB = tem.persist(Currency.of(643, "RUB", BigDecimal.valueOf(72)));

		// Client Account
		acc = new ClientAccount(
			1, "#F48FB1", "Description", "Account 1",
			BigDecimal.valueOf(7L), 1, BigDecimal.valueOf(100L)
		);
		acc.setContentRu("Описание");
		acc.setTitleRu("Счет 1");
		acc = tem.persist(acc);

		// Card
		card = tem.persist(new Card(
			2, "#F48FB1", "Description", "Card 1", 
			BigDecimal.valueOf(5L)
		));

		// Deposit
		dep = tem.persist(new Deposit(
			3, "#F48FB1", "Description", "Deposit 1",
			BigDecimal.valueOf(5L)
		));
	}

	@Test
	void testUniqueViolation() {
		// For Client Account
		final PersonalAccount perClientAcc = tem.persist(
			new PersonalDeposit(client, acc, USD, BigInteger.valueOf(123L))
		);
		// For Deposit
		final PersonalAccount perDep = tem.persist(
			new PersonalDeposit(client, dep, USD, BigInteger.valueOf(123L))
		);

		val th = assertThrows(
			PersistenceException.class, 
			() -> tem.flush()
		);
		
		assertEquals(th.getCause().getClass(), ConstraintViolationException.class);
	}
}
