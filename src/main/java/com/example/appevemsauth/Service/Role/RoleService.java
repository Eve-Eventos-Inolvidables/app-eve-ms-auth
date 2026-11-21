package com.example.appevemsauth.Service.Role;

import com.example.appevecommon.Models.User.Role;
import com.example.appevecommon.Repository.IBaseRepository;
import com.example.appevecommon.Service.UpdatableService;
import com.example.appevecommon.Service.Utilities.Responses.Ok.PageResult;
import com.example.appevemsauth.Config.RedisCacheConfig;
import com.example.appevemsauth.Service.Cache.IndividualCacheService;
import org.openapitools.model.CreateRoleDto;
import org.openapitools.model.RoleDto;
import org.openapitools.model.UpdateRoleDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Service;


@Service
public class RoleService extends UpdatableService<
        Role,
        RoleDto,
        UpdateRoleDto,
        RoleMapper,
        RoleFilter> {


    private final IndividualCacheService individualCacheService;
    private final String x = "#{ '${cache.sections.roles}' }";
    protected RoleService(IBaseRepository<Role> repository, RoleMapper mapper,
                          IndividualCacheService individualCacheService) {
        super(repository, mapper);
        this.individualCacheService = individualCacheService;
    }

    @Caching(
            put = {
                    @CachePut(value =  RedisCacheConfig.ROLE_SECTION,
                            key = "T(com.example.appevemsauth.Config.RedisCacheConfig).roleKey(#result.id)")
            },
            evict = {
                    @CacheEvict(value = RedisCacheConfig.ROLE_SECTION,
                            key = "T(com.example.appevemsauth.Config.RedisCacheConfig).rolesListKey()")
            }
    )
    public RoleDto createRole(CreateRoleDto dto){
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    @Cacheable(value = RedisCacheConfig.ROLE_SECTION,
            key = "T(com.example.appevemsauth.Config.RedisCacheConfig).rolesListKey()")
    public List<RoleDto> getAll(){
        List<RoleDto> roles = new ArrayList<>(repository.findAll().stream().map(mapper::toDto).toList());
        individualCacheService.putIfAbsent(
                RedisCacheConfig.ROLE_SECTION,
                roles,
                role -> RedisCacheConfig.roleKey(role.getId()));
        return roles;
    }

    @Override
    public PageResult<RoleDto> getByFilter(RoleFilter filter) {
        PageResult<RoleDto> result = super.getByFilter(filter);
        individualCacheService.putIfAbsent(
                RedisCacheConfig.ROLE_SECTION,
                result.items(),
                role -> RedisCacheConfig.roleKey(role.getId()));
        return result;
    }

    //CACHE

    @Override
    @Caching(
            put = {
                    @CachePut(value = RedisCacheConfig.ROLE_SECTION,
                            key = "T(com.example.appevemsauth.Config.RedisCacheConfig).roleKey(#result.id)")
            },
            evict = {
                    @CacheEvict(value = RedisCacheConfig.ROLE_SECTION,
                            key = "T(com.example.appevemsauth.Config.RedisCacheConfig).rolesListKey()"),
                    @CacheEvict(value = RedisCacheConfig.USER_SECTION, allEntries = true)
            }
    )
    public RoleDto update(Long id, UpdateRoleDto updateDto){
        return super.update(id,updateDto);
    }

    @Override
    @Cacheable(value = RedisCacheConfig.ROLE_SECTION,
            key = "T(com.example.appevemsauth.Config.RedisCacheConfig).roleKey(#id)")
    public RoleDto getById(Long id){
        return super.getById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.ROLE_SECTION,
                    key = "T(com.example.appevemsauth.Config.RedisCacheConfig).roleKey(#id)"),
            @CacheEvict(value = RedisCacheConfig.ROLE_SECTION,
                    key = "T(com.example.appevemsauth.Config.RedisCacheConfig).rolesListKey()"),
            @CacheEvict(value = RedisCacheConfig.USER_SECTION, allEntries = true)
    })
    public boolean delete(Long id){
        return super.delete(id);
    }

    @Override
    public Specification<Role> toSpecification(RoleFilter filter) {
        return (root, query, criteriaBuilder) -> {
            if(filter == null) return criteriaBuilder.conjunction(); //Return 1=1(true) if there isn't any filter
            List<Predicate> predicateList =new ArrayList<>();
            if(filter.getName() != null  && !filter.getName().isBlank() ) {
                predicateList.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")) , "%" + filter.getName().toLowerCase() + "%"
                        )
                );
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }

}
