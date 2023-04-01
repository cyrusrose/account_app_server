package com.logic.account.user;

import static com.logic.account.utils.MyUtils.jsonify;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import com.logic.account.utils.Views;

import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper=true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonTypeName("corporate")
@DiscriminatorValue("corporate")
public class CorporateCustomer extends Client {
    @JsonView(Views.Public.class)
    @Column(nullable = false)
        @NonNull private String name;
    @JsonView(Views.Public.class)
        private String nameRu;
    @JsonView(Views.Public.class)
    @Column(nullable = false)
        @NonNull private BigInteger clientSsn;
    @OneToMany(mappedBy = "cust")
    @JsonView(Views.Public.class)
    private List<ClientNumber> clientNos = new ArrayList<>();

    public CorporateCustomer(@NonNull String name, @NonNull BigInteger clientSsn) {
        super();
        this.name = name;
        this.clientSsn = clientSsn;
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }
}