package com.logic.account.account;

import static com.logic.account.utils.MyUtils.jsonify;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.logic.account.utils.Currency;

import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper=true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonTypeName("client_account")
@DiscriminatorValue("client_account")
public class ClientAccount extends Account {
    @Column(nullable = false)
        @NonNull private BigDecimal anualInterestRate;
    @Column(nullable = false)
        private int monthsPeriod;
    @Column(nullable = false)
        @NonNull private BigDecimal minAmount;

    public ClientAccount(
        int no, @NonNull String color, @NonNull String content, @NonNull String title,
        @NonNull BigDecimal anualInterestRate, 
        int monthsPeriod, @NonNull BigDecimal minAmount
    ) {
        super(no, color, content, title);
        this.anualInterestRate = anualInterestRate;
        this.monthsPeriod = monthsPeriod;
        this.minAmount = minAmount;
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }
}
