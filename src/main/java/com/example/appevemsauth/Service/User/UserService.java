package com.example.appevemsauth.Service.User;

import com.example.appevecommon.Models.User.Role;
import com.example.appevecommon.Models.User.User;
import com.example.appevecommon.Service.Exception.ResourceNotFoundException;
import com.example.appevecommon.Service.UpdatableService;
import com.example.appevemsauth.Config.RedisCacheConfig;
import com.example.appevemsauth.Dto.RegisterRequest;
import com.example.appevemsauth.Repository.IRoleRepository;
import com.example.appevemsauth.Repository.IUserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService extends UpdatableService<User, UserDto, UpdateUserDto, UserMapper, UserFilter> {

    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            IUserRepository repository,
            UserMapper userMapper,
            IRoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        super(repository, userMapper);
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @CachePut(value = RedisCacheConfig.USER_SECTION, key = "'api_users_'+#result.id")
    public UserDto createUser(CreateUserDto dto) {
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("El rol ingresado no existe"));
        User user = mapper.toEntity(dto);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User savedUser = repository.save(user);
        return mapper.toDto(savedUser);
    }

    public UserDto register(RegisterRequest request) {
        Role clientRole = roleRepository.findByNameIgnoreCase("Cliente")
                .orElseThrow(() -> new ResourceNotFoundException("El rol Cliente no existe"));
        CreateUserDto dto = new CreateUserDto(
                clientRole.getId(), request.getName(), request.getEmail(), request.getPassword());
        return createUser(dto);
    }

    @Override
    @Transactional
    @CacheEvict(value = RedisCacheConfig.USER_SECTION, key = "'api_users_'+#id")
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario con id: " + id + " no existe");
        }
        return super.delete(id);
    }

    @Override
    @CachePut(value = RedisCacheConfig.USER_SECTION, key = "'api_users_'+#result.id")
    public UserDto update(Long id, UpdateUserDto updateDto) {
        return super.update(id, updateDto);
    }

    @Override
    @Cacheable(value = RedisCacheConfig.USER_SECTION, key = "'api_users_'+#id")
    public UserDto getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Specification<User> toSpecification(UserFilter f) {
        return (root, query, criteriaBuilder) -> {

            if (f == null) return criteriaBuilder.conjunction();
            List<Predicate> predicateList = new ArrayList<>();
            // Basic filter by name (searchParam)
            if (f.getName() != null && !f.getName().isBlank()) {
                predicateList.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")), "%" + f.getName().toLowerCase() + "%"
                        )
                );
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
