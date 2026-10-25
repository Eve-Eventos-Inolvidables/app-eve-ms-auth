package com.example.appevemsauth.Repository;

import com.example.appevecommon.Models.User.Role;
import com.example.appevecommon.Repository.IBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends IBaseRepository<Role> {
    Optional<Role> findByNameIgnoreCase(String name);
}
