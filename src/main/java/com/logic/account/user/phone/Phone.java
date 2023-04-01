package com.logic.account.user.phone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.logic.account.utils.Views;

import lombok.*;

@Value
@JsonView(Views.Public.class)
public class Phone {
    @NonNull private String phoneNumber;

    @JsonProperty(access = Access.READ_ONLY)
    public PhoneType getType() {
        return PhoneType.getType(phoneNumber);
    }
    
    @JsonCreator
    public static Phone of(@NonNull @JsonProperty("phoneNumber") String phoneNumber) {
        return new Phone(phoneNumber);
    }
}