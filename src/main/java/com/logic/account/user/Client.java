package com.logic.account.user;

import static com.logic.account.utils.MyUtils.jsonify;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.logic.account.account.personal.PersonalAccount;
import com.logic.account.transactions.UserTransaction;

import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded =  true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "clss")
@JsonTypeName("client")
@DiscriminatorColumn(name = "clss")
@DiscriminatorValue("client")
@JsonSubTypes({
    @JsonSubTypes.Type(Customer.class), 
    @JsonSubTypes.Type(CorporateCustomer.class) 
})
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class, 
    property = "id"
)
public class Client {
    @Id @GeneratedValue @Column(updatable = false)
    @EqualsAndHashCode.Include
        private UUID id;
    @JoinColumn(foreignKey = @ForeignKey(
        foreignKeyDefinition = "FOREIGN KEY (default_account_id) REFERENCES personal_account ON DELETE SET NULL",
        value = ConstraintMode.CONSTRAINT), nullable = true)
    @OneToOne(fetch = FetchType.LAZY)
        private PersonalAccount defaultAccount;
    @OneToOne(fetch = FetchType.LAZY)
        private User user;

    // <-- Only for JPQL
    @JsonProperty(access = Access.WRITE_ONLY)
    @OneToMany(mappedBy = "client")
        private List<PersonalAccount> personalAccounts = null;
    // Only for JPQL -->

    @Override 
    public String toString() {
        return jsonify(this);
    }
}
