package com.example.appevemsauth.Repository;

import com.example.appevecommon.Models.User.User;
import com.example.appevecommon.Repository.IBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends IBaseRepository<User> {

    Optional<User> findByEmailIgnoreCase(String email);
}
