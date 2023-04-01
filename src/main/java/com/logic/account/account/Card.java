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
@JsonTypeName("card")
@DiscriminatorValue("card")
public class Card extends Account {
    @Column(nullable = false)
        @NonNull private BigDecimal anualInterestRate;

    public Card(
        int no, @NonNull String color, @NonNull String content, @NonNull String title,
        @NonNull BigDecimal anualInterestRate
    ) {
        super(no, color, content, title);
        this.anualInterestRate = anualInterestRate;
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }
}


