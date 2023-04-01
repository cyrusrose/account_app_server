package com.logic.account.user.phone;

import jakarta.persistence.AttributeConverter;

public class PhoneConverter implements AttributeConverter<Phone, String> {
    @Override
    public String convertToDatabaseColumn(Phone phone) {
        return phone.getPhoneNumber();
    }

    @Override
    public Phone convertToEntityAttribute(String phone) {
        return Phone.of(phone);
    }
}
