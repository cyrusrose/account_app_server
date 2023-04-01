package com.logic.account.account;

import static com.logic.account.utils.MyUtils.jsonify;

import java.util.List;
import java.util.UUID;

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
import com.logic.account.account.personal.PersonalAccount;
import com.logic.account.transactions.UserTransaction;
import com.logic.account.utils.Currency;

import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded =  true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "clss")
@JsonTypeName("account")
@DiscriminatorColumn(name = "clss")
@DiscriminatorValue("account")
@JsonSubTypes({
    @JsonSubTypes.Type(Card.class), 
    @JsonSubTypes.Type(ClientAccount.class),
    @JsonSubTypes.Type(Deposit.class)
})
public class Account {
    @Id @GeneratedValue @Column(updatable = false)
    @EqualsAndHashCode.Include
        private UUID id;
    @Column(nullable = false, unique = true)
        private int no;
    @Column(nullable = false)
        @NonNull private String color;
    @Column(nullable = false)
        @NonNull private String content;
    @Column
        private String contentRu;
    @Column(nullable = false)
        @NonNull private String title;
    @Column
        private String titleRu;
    // <-- Only for JPQL
    @JsonProperty(access = Access.WRITE_ONLY)
    @OneToMany(mappedBy = "account")
        private List<PersonalAccount> personalAccounts = null;
    // Only for JPQL -->
    
    public Account(
        int no, @NonNull String color, @NonNull String content, 
        @NonNull String title
    ) {
        this.no = no;
        this.color = color;
        this.content = content;
        this.title = title;
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }
}
