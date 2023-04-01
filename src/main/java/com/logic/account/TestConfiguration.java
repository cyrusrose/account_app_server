package com.logic.account;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.logic.account.account.Card;
import com.logic.account.account.ClientAccount;
import com.logic.account.account.Deposit;
import com.logic.account.account.personal.PersonalCard;
import com.logic.account.account.personal.PersonalClientAccount;
import com.logic.account.account.personal.PersonalDeposit;
import com.logic.account.account.personal.rest.PersonalRepository;
import com.logic.account.account.rest.AccountService;
import com.logic.account.transactions.rest.TransactionService;
import com.logic.account.user.User;
import com.logic.account.user.ClientNumber;
import com.logic.account.user.CorporateCustomer;
import com.logic.account.user.Customer;
import com.logic.account.user.phone.Phone;
import com.logic.account.user.rest.ClientNoRepository;
import com.logic.account.user.rest.ClientRepository;
import com.logic.account.user.rest.UserRepository;
import com.logic.account.utils.Currency;
import com.logic.account.utils.ValidThrough;
import com.logic.account.utils.rest.CurrencyRepository;

import lombok.*;

@Configuration
public class TestConfiguration {

    @Bean
    public ApplicationRunner databaseInitializer(
        CurrencyRepository curRep, ClientRepository clntRep, UserRepository usrRep,
        PersonalRepository paccRep, AccountService accServ, TransactionService trnServ,
        ClientNoRepository noRep
    ) {
        return args -> {
            val USD = curRep.save(Currency.of(840, "USD", BigDecimal.valueOf(1)));
            val RUB = curRep.save(Currency.of(643, "RUB", BigDecimal.valueOf(72)));

            val mir = new Card(
                1, "#70A4FD", "Description of Mir card", "Mir",
                BigDecimal.valueOf(5L)
            );
            mir.setContentRu("Описание карты Мир");
            mir.setTitleRu("Мир");
                accServ.save(mir);

            val dep1 = new Deposit(
                2, "#FFD29A", "Description of deposit 1", "Deposit 1",
                BigDecimal.valueOf(7L)
            );
            dep1.setTitleRu("Вклад 1");
            dep1.setContentRu("Описание вклада 1");
            accServ.save(dep1);

            val dep2 = new Deposit(
                3, "#FFFDB1", "Description of deposit 2", "Deposit 2",
                BigDecimal.valueOf(7L)
            );
            dep2.setTitleRu("Вклад 2");
            dep2.setContentRu("Описание вклада 2");
            accServ.save(dep2);

            val ca1 = new ClientAccount(
                4, "#CE93D8", "Description of client account 1", "Client Account 1",
                BigDecimal.valueOf(7L), 12, BigDecimal.valueOf(10L)
            );
            ca1.setTitleRu("Счет 1");
            ca1.setContentRu("Описание счета 1");
            accServ.save(ca1);

            val ca2 = new ClientAccount(
                5, "#80DEEA", "Description of client account 2", "Client Account 2",
                BigDecimal.valueOf(7L), 12, BigDecimal.valueOf(10L)
            );
            ca2.setTitleRu("Счет 2");
            ca2.setContentRu("Описание счета 2");
            accServ.save(ca2);

            val visa = new Card(
                6, "#7ED1FA", "Description of Visa card", "Visa",
                BigDecimal.valueOf(5L)
            );
            visa.setTitleRu("Visa");
            visa.setContentRu("Описание карты Visa");
            accServ.save(visa);

            val mc = new Card(
                7, "#FFF48D", "Description of MasterCard card", "MasterCard",
                BigDecimal.valueOf(5L)
            );
            mc.setTitleRu("MasterCard");
            mc.setContentRu("Описание карты MasterCard");
            accServ.save(mc);

            // Client 1
            val client = clntRep.save(
                new Customer(Phone.of("+1 (999) 999-99-91"), "Bob", "Robinson")
            );
            val user = usrRep.save(
                new User(client, "bob", "123")
            );

            client.setUser(user);        
            client.setPersonalAccounts(List.of(
                paccRep.save(new PersonalCard(
                    client, mir, USD,
                    new BigInteger("4444111122223331"), 123, ValidThrough.of(11, 27)
                )),
                paccRep.save(new PersonalDeposit(
                    client, dep1, USD,
                    new BigInteger("1111122221")
                )),
                paccRep.save(new PersonalCard(
                    client, visa, USD,
                    new BigInteger("4444211122223331"), 123, ValidThrough.of(11, 27)
                )),
                paccRep.save(new PersonalDeposit(
                    client, dep2, USD,
                    new BigInteger("1112122221")
                )),
                paccRep.save(new PersonalClientAccount(
                    client, ca1, USD,
                    new BigInteger("3111122221")
                ))
            ));

            client.getPersonalAccounts().get(0).setMoney(BigDecimal.valueOf(200));
            client.getPersonalAccounts().get(1).setMoney(BigDecimal.valueOf(200));
            client.getPersonalAccounts().get(2).setMoney(BigDecimal.valueOf(200));
            client.getPersonalAccounts().get(3).setMoney(BigDecimal.valueOf(200));
            client.getPersonalAccounts().get(4).setMoney(BigDecimal.valueOf(200));
            paccRep.save(client.getPersonalAccounts().get(0));
            paccRep.save(client.getPersonalAccounts().get(1));
            paccRep.save(client.getPersonalAccounts().get(2));
            paccRep.save(client.getPersonalAccounts().get(3));
            paccRep.save(client.getPersonalAccounts().get(4));
            client.setDefaultAccount(client.getPersonalAccounts().get(0));

            clntRep.save(client);

            // Client 2
            val client2 = clntRep.save(
                new Customer(Phone.of("+1 (999) 999-99-92"), "Иван", "Иванов")
            );
            val user2 = usrRep.save(
                new User(client2, "ivan", "123")
            );

            client2.setUser(user2);       
            client2.setPersonalAccounts(List.of(
                paccRep.save(new PersonalCard(
                    client2, mir, RUB,
                    new BigInteger("4444111122223332"), 123, ValidThrough.of(10, 27)
                )),
                paccRep.save(new PersonalDeposit(
                    client2, dep1, RUB,
                    new BigInteger("1111122222")
                )),
                paccRep.save(new PersonalCard(
                    client2, mc, RUB,
                    new BigInteger("4444311122223331"), 123, ValidThrough.of(11, 27)
                )),
                paccRep.save(new PersonalDeposit(
                    client2, dep2, RUB,
                    new BigInteger("1113122221")
                ))
            ));

            client2.getPersonalAccounts().get(0).setMoney(BigDecimal.valueOf(200));
            client2.getPersonalAccounts().get(1).setMoney(BigDecimal.valueOf(200));
            client2.getPersonalAccounts().get(2).setMoney(BigDecimal.valueOf(200));
            client2.getPersonalAccounts().get(3).setMoney(BigDecimal.valueOf(200));
            paccRep.save(client2.getPersonalAccounts().get(0));
            paccRep.save(client2.getPersonalAccounts().get(1));
            paccRep.save(client2.getPersonalAccounts().get(2));
            paccRep.save(client2.getPersonalAccounts().get(3));
            client2.setDefaultAccount(client2.getPersonalAccounts().get(0));

            clntRep.save(client2);

            // Client 3
            val client3 = clntRep.save(
                new CorporateCustomer("Our Town", BigInteger.valueOf(12345L))
            );
            val user3 = usrRep.save(
                new User(client3, "com", "123")
            );

            client3.setNameRu("Наш Город");
            client3.setUser(user3);       
            client3.setPersonalAccounts(List.of(
                paccRep.save(new PersonalDeposit(
                    client3, dep1, USD,
                    new BigInteger("1111122223")
                ))
            ));
            val acc1 = new ClientNumber(
                BigInteger.valueOf(10001L), client3, 
                "Housing & Public Utilities", "Жилищно Комунальные Услуги"
            );
            val acc2 = new ClientNumber(
                BigInteger.valueOf(10002L), client3,
                "Internet", "Интернет"
            );
            client3.setClientNos(List.of(
                noRep.save(acc1),
                noRep.save(acc2)
            ));

            client3.getPersonalAccounts().get(0).setMoney(BigDecimal.valueOf(2000));
            paccRep.save(client3.getPersonalAccounts().get(0));
            client3.setDefaultAccount(client3.getPersonalAccounts().get(0));
            clntRep.save(client3);

            // Transactions
            // client1
            trnServ.sendMoney(new BigDecimal(10), client.getDefaultAccount().getId(), "+1 (999) 999-99-92");
            trnServ.sendMoney(new BigDecimal(10), client.getPersonalAccounts().get(4).getId(), "+1 (999) 999-99-92");
            trnServ.sendMoney(new BigDecimal(10), client.getDefaultAccount().getId(), client3.getClientSsn(), acc1.getClientNo());

            // client2
            trnServ.sendMoney(new BigDecimal(10), client2.getPersonalAccounts().get(0).getId(), "+1 (999) 999-99-91");
            trnServ.sendMoney(new BigDecimal(10), client2.getPersonalAccounts().get(1).getId(), client3.getClientSsn(), acc2.getClientNo());

            //client3
            trnServ.sendMoney(new BigDecimal(20), client3.getDefaultAccount().getId(), "+1 (999) 999-99-91");
            trnServ.sendMoney(new BigDecimal(40), client3.getDefaultAccount().getId(), "+1 (999) 999-99-91");
            trnServ.sendMoney(new BigDecimal(30), client3.getDefaultAccount().getId(), "+1 (999) 999-99-91");
            trnServ.sendMoney(new BigDecimal(15), client3.getDefaultAccount().getId(), "+1 (999) 999-99-92");
            trnServ.sendMoney(new BigDecimal(155), client3.getDefaultAccount().getId(), "+1 (999) 999-99-92");
        };
    }
}
