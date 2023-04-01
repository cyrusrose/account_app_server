package com.logic.account.account.personal;

import static com.logic.account.utils.MyUtils.jsonify;

import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.logic.account.account.Account;
import com.logic.account.account.ClientAccount;
import com.logic.account.user.Client;
import com.logic.account.utils.Currency;

import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper=true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonTypeName("personal_client")
@DiscriminatorValue("personal_client")
public class PersonalClientAccount extends PersonalAccount {
    @Column(nullable = false, unique = true)
    @JsonProperty(value = "accountNo")
        @NonNull private BigInteger accountNo2;

    public PersonalClientAccount(
        @NonNull Client client, @NonNull Account account, @NonNull Currency currency,
        @NonNull BigInteger accountNo
    ) {
        super(client, account, currency);
        
        if (!account.getClass().equals(ClientAccount.class))
            throw new IllegalArgumentException(
                "%s cannot be chosen, expected %s"
                .formatted(
                    account.getClass().getCanonicalName(), 
                    ClientAccount.class.getCanonicalName()
                )
            );
        this.accountNo2 = accountNo;
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }
}
