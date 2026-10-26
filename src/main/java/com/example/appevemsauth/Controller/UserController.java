package com.example.appevemsauth.Controller;

import com.example.appevecommon.Controller.BaseController;
import com.example.appevecommon.Models.User.User;
import com.example.appevecommon.Service.Utilities.Responses.Response;
import com.example.appevecommon.Service.Utilities.Responses.ResponseFactory;
import com.example.appevemsauth.Service.User.UserFilter;
import com.example.appevemsauth.Service.User.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.CreateUserDto;
import org.openapitools.model.UpdateUserDto;
import org.openapitools.model.UserDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/users")
@Tag( name = "Users", description = "Controller for users ")
@PreAuthorize("hasRole('Administrador')")
public class UserController extends BaseController<User, UserDto, UserFilter, UserService> {
    protected UserController(UserService userService){super(userService);}

    @PostMapping
    public Response<UserDto> create(@Valid @RequestBody CreateUserDto dto){
        log.info("Se solicitó crear un usuario de nombre {} con rolId {} y email {} ",dto.getName(),dto.getRoleId(),dto.getEmail());
        return ResponseFactory.resourceCreated(service.createUser(dto));
    }
    @PatchMapping("/{id}")
    public Response<UserDto> update(@PathVariable Long id,@Valid @RequestBody UpdateUserDto dto){
        log.info("Se actualiza el usuario con id {}",id);
        return ResponseFactory.ok("Usuario Actualizado Correctamente",service.update(id,dto));
    }
}
