package com.logic.account.account.rest;

import org.mapstruct.*;

import com.logic.account.account.*;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "no", conditionExpression = "java(accDto.getNo() != 0)")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void updateCard(Card accDto, @MappingTarget Card acc);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "no", conditionExpression = "java(accDto.getNo() != 0)")
    @Mapping(target = "monthsPeriod", conditionExpression = "java(accDto.getMonthsPeriod() != 0)")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void updateClientAccount(ClientAccount accDto, @MappingTarget ClientAccount acc);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "no", conditionExpression = "java(accDto.getNo() != 0)")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void updateDeposit(Deposit accDto, @MappingTarget Deposit acc);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "no", conditionExpression = "java(accDto.getNo() != 0)")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void updateAccount(Account accDto, @MappingTarget Account acc);

    void update(Account accDto, Account acc) {
        if(acc.getClass().equals(Card.class))
            updateCard((Card)accDto, (Card)acc);
        else if (acc.getClass().equals(ClientAccount.class))
            updateClientAccount((ClientAccount)accDto, (ClientAccount)acc);
        else if (acc.getClass().equals(Deposit.class))
            updateDeposit((Deposit)accDto, (Deposit)acc);
        else
            updateAccount(accDto, acc);
    }
}
