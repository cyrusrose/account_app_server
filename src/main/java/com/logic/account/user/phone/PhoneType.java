package com.logic.account.user.phone;

import java.util.regex.Pattern;

import lombok.val;

public enum PhoneType {
    GENERAL("\\+?\\d \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}"),
    SHORT("\\d{3}"),
    MALFORMED(".*");

    private Pattern pattern;

    PhoneType(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public static PhoneType getType(String regex) {
        for(val type: values()) {
            if (type.pattern.matcher(regex).matches())
                return type;
        }
        return MALFORMED;
    }
}
