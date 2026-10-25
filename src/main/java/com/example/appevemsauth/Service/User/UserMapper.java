package com.example.appevemsauth.Service.User;

import com.example.appevecommon.Models.User.User;
import com.example.appevecommon.Service.Utilities.UpdatableMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapitools.model.*;


@Mapper(componentModel = "spring")
public  interface UserMapper extends UpdatableMapper<User, UserDto, UpdateUserDto> {

    @Mapping(source = "roleId", target = "role.id")
    User toEntity(CreateUserDto dto);

    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "role.name", target = "roleName")
    UserDto toDto(User entity);

}