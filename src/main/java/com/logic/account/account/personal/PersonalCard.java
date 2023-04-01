package com.logic.account.account.personal;

import static com.logic.account.utils.MyUtils.jsonify;

import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.logic.account.account.Account;
import com.logic.account.account.Card;
import com.logic.account.user.Client;
import com.logic.account.utils.Currency;
import com.logic.account.utils.ValidThrough;

import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper=true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonTypeName("personal_card")
@DiscriminatorValue("personal_card")
public class PersonalCard extends PersonalAccount {
    @Column(nullable = false, precision = 16, unique = true)
        @NonNull private BigInteger cardNo;
    @Column(nullable = false, columnDefinition = "smallint")
        private int cvc;
    @Embedded
    @NonNull private ValidThrough validThrow;
    
    public PersonalCard(
        @NonNull Client client, @NonNull Account account, @NonNull Currency currency,
        @NonNull BigInteger cardNo, int cvc, @NonNull ValidThrough validThrow
    ) {
        super(client, account, currency);

        if (!account.getClass().equals(Card.class))
            throw new IllegalArgumentException(
                "%s cannot be chosen, expected %s"
                .formatted(account.getClass().getCanonicalName(), Card.class.getCanonicalName())
            );
        this.cardNo = cardNo;
        this.cvc = cvc;
        this.validThrow = validThrow;
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }

}



