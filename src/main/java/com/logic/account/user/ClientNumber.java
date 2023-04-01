package com.logic.account.user;

import java.math.BigInteger;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.logic.account.utils.Views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Entity
@Data
@RequiredArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(name = "client_no_uc", columnNames = {"id", "clientNo"}))
@EqualsAndHashCode(onlyExplicitlyIncluded =  true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientNumber {
    @Id @GeneratedValue @Column(updatable = false) 
    @EqualsAndHashCode.Include
    @JsonView(Views.Public.class)
        UUID id;
    @JsonView(Views.Public.class)
        @NonNull BigInteger clientNo;
    @JsonView(Views.Public.class)
        @NonNull BigInteger clientSsn;
    @JsonIgnore
    @ManyToOne
        @NonNull CorporateCustomer cust;
    @JsonView(Views.Public.class)
        @NonNull String name;
    @JsonView(Views.Public.class)
        String nameRu;
    
    public ClientNumber(@NonNull BigInteger clientNo, @NonNull CorporateCustomer cust, @NonNull String name,
            String nameRu) {
        this.clientNo = clientNo;
        this.clientSsn = cust.getClientSsn();
        this.cust = cust;
        this.name = name;
        this.nameRu = nameRu;
    }

    
}
