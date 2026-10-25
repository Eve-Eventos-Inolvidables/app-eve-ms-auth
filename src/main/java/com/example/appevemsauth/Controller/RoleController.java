package com.example.appevemsauth.Controller;

import com.example.appevecommon.Controller.BaseController;
import com.example.appevecommon.Models.User.Role;
import com.example.appevecommon.Service.Utilities.Responses.Response;
import com.example.appevecommon.Service.Utilities.Responses.ResponseFactory;
import com.example.appevemsauth.Service.Role.RoleFilter;
import com.example.appevemsauth.Service.Role.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.CreateRoleDto;
import org.openapitools.model.RoleDto;
import org.openapitools.model.UpdateRoleDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/roles")
@Tag( name = "Roles", description = "Controller with a sample of simple endpoints for a simple entity")
public class RoleController extends BaseController<Role, RoleDto, RoleFilter, RoleService> {

    protected RoleController(RoleService service) {
        super(service);
    }

    @PostMapping
    Response<RoleDto> create(@Valid @RequestBody CreateRoleDto dto){
        return ResponseFactory.ok(service.createRole(dto) );
    }

    @GetMapping("/all")
    Response<List<RoleDto>> getAll(){
        log.info("GET ALL /api/roles/all");
        return ResponseFactory.ok(service.getAll());
    }

    @PatchMapping("/{id}")
    Response<RoleDto> update(@PathVariable Long id, @Valid @RequestBody UpdateRoleDto dto){
        return ResponseFactory.ok("Rol actualizado correctamente",service.update(id,dto));
    }

}
