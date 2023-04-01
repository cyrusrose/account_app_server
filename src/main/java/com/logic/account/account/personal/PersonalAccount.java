package com.logic.account.account.personal;

import static com.logic.account.utils.MyUtils.jsonify;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.logic.account.account.Account;
import com.logic.account.transactions.UserTransaction;
import com.logic.account.user.Client;
import com.logic.account.utils.Currency;

import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded =  true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "clss")
@JsonSubTypes({
    @JsonSubTypes.Type(PersonalCard.class), 
    @JsonSubTypes.Type(PersonalDeposit.class),
    @JsonSubTypes.Type(PersonalClientAccount.class)
})
@JsonTypeName("personal")
@DiscriminatorColumn(name = "clss")
@DiscriminatorValue("personal")
public class PersonalAccount {
    @Id @GeneratedValue @Column(updatable = false)
    @EqualsAndHashCode.Include
        private UUID id;
    @Column(nullable = false)
        @NonNull private BigDecimal money;
    @ManyToOne(fetch = FetchType.LAZY)
        @NonNull private Client client;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @NonNull private Account account;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @NonNull private Currency currency;
    
    public PersonalAccount addMoney(@NonNull BigDecimal money) {
        this.money = this.money.add(money);
        return this;
    }

    public PersonalAccount subtractMoney(@NonNull BigDecimal money) {
        this.money = this.money.subtract(money);
        return this;
    }

    public PersonalAccount(@NonNull Client client, @NonNull Account account, @NonNull Currency currency) {
        this.money = BigDecimal.ZERO;
        this.client = client;
        this.account = account;
        this.currency = currency;
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }
}
