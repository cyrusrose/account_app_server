package com.logic.account.user;

import static com.logic.account.utils.MyUtils.jsonify;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import com.logic.account.user.phone.Phone;
import com.logic.account.user.phone.PhoneConverter;
import com.logic.account.utils.Views;

import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonTypeName("customer")
@DiscriminatorValue("customer")
public class Customer extends Client {
    @JsonView(Views.Public.class)
    @Column(nullable = false)
        @NonNull private String name;
    @JsonView(Views.Public.class)
    @Column(nullable = false)
        @NonNull private String surname;
    @JsonView(Views.Public.class)
    @Column(updatable = false, unique = true) @Convert(converter = PhoneConverter.class)
        @NonNull private Phone phone;

    public Customer(@NonNull Phone phone, @NonNull String name, @NonNull String surname) {
        super();
        this.phone = phone;
        this.name = name;
        this.surname = surname;
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }
}
  