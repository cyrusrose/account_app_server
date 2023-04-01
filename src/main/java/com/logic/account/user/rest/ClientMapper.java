package com.logic.account.user.rest;

import org.mapstruct.*;

import com.logic.account.user.Client;
import com.logic.account.user.CorporateCustomer;
import com.logic.account.user.Customer;

@Mapper(componentModel = "spring")
public abstract class ClientMapper {
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void updateCustomer(Customer clntDto, @MappingTarget Customer clnt);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void updateCorporate(CorporateCustomer clntDto, @MappingTarget CorporateCustomer clnt);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void updateClient(Client clntDto, @MappingTarget Client clnt);

    void update(Client clntDto, Client clnt) {
        if(clnt.getClass().equals(Customer.class))
            updateCustomer((Customer)clntDto, (Customer)clnt);
        else if (clnt.getClass().equals(CorporateCustomer.class))
            updateCorporate((CorporateCustomer)clntDto, (CorporateCustomer)clnt);
        else {
            updateClient(clntDto, clnt);
        }
    }
}
