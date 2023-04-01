package com.logic.account.account.personal;

import static com.logic.account.utils.MyUtils.jsonify;

import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.logic.account.account.Account;
import com.logic.account.account.Deposit;
import com.logic.account.user.Client;
import com.logic.account.utils.Currency;

import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper=true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonTypeName("personal_deposit")
@DiscriminatorValue("personal_deposit")
public class PersonalDeposit extends PersonalAccount {
    @Column(nullable = false, unique = true)
        @NonNull private BigInteger accountNo;

    public PersonalDeposit(
        @NonNull Client client, @NonNull Account account, @NonNull Currency currency,
        @NonNull BigInteger accountNo
    ) {
        super(client, account, currency);
        
        if (!account.getClass().equals(Deposit.class))
            throw new IllegalArgumentException(
                "%s cannot be chosen, expected %s"
                .formatted(
                    account.getClass().getCanonicalName(), 
                    Deposit.class.getCanonicalName()
                )
            );
        this.accountNo = accountNo;
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }
}
