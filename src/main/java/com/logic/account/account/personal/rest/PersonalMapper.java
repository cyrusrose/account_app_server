package com.logic.account.account.personal.rest;

import org.mapstruct.*;

import com.logic.account.account.personal.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PersonalMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cvc", conditionExpression = "java(accDto.getCvc() != 0)")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void updateCard(PersonalCard accDto, @MappingTarget PersonalCard acc);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void updateDeposit(PersonalDeposit accDto, @MappingTarget PersonalDeposit acc);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void updateClient(PersonalClientAccount accDto, @MappingTarget PersonalClientAccount acc);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void updateAccount(PersonalAccount accDto, @MappingTarget PersonalAccount acc);

    void update(PersonalAccount accDto, PersonalAccount acc) {
        if(acc.getClass().equals(PersonalCard.class))
            updateCard((PersonalCard)accDto, (PersonalCard)acc);
        else if (acc.getClass().equals(PersonalDeposit.class))
            updateDeposit((PersonalDeposit)accDto, (PersonalDeposit)acc);
        else if (acc.getClass().equals(PersonalClientAccount.class))
            updateClient((PersonalClientAccount)accDto, (PersonalClientAccount)acc);
        else
            updateAccount(accDto, acc);
    }
}
