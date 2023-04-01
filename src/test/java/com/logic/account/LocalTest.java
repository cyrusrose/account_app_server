package com.logic.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logic.account.user.phone.Phone;
import com.logic.account.user.phone.PhoneType;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


@ExtendWith(MockitoExtension.class)
public class LocalTest {
    @Test
    void testPhoneTypes() {
        assertThat(
            /*in:*/ List.of(
                Phone.of("9 (999) 999-99-99").getType(),
                Phone.of("+1 (999) 999-99-99").getType()
            ), 
            everyItem(is(PhoneType.GENERAL))
        );
        
        var phone = Phone.of("999");
        assertThat(phone.getType(), is(PhoneType.SHORT));
        
        assertThat(
            /*in:*/ List.of(
                Phone.of("33").getType(), 
                Phone.of("bad").getType(),
                Phone.of("+1\t(999)    999-99-99").getType()
            ), 
            everyItem(is(PhoneType.MALFORMED))
        );
    }    
}
