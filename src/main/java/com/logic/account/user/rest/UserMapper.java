package com.logic.account.user.rest;

import org.mapstruct.*;

import com.logic.account.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(User usrDto, @MappingTarget User usr);
}
