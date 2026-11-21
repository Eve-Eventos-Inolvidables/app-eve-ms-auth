package com.example.appevemsauth.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {
    public static final String ROLE_SECTION = "roles";
    public static final String USER_SECTION = "users";
    private static final String USER_KEY_PREFIX = "api_users_";
    private static final String ROLE_KEY_PREFIX = "api_role_";
    private static final String ROLES_LIST_KEY = "api_roles_all";

    public static String userKey(Long id) {
        return USER_KEY_PREFIX + id;
    }

    public static String roleKey(Long id) {
        return ROLE_KEY_PREFIX + id;
    }

    public static String rolesListKey() {
        return ROLES_LIST_KEY;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
        @Value("${cache.users.ttl-days}") long usersTtldays,
        @Value("${cache.roles.ttl-days}") long rolesTtldays
    ) {
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();

        GenericJacksonJsonRedisSerializer valueSerializer = GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(typeValidator)
                .enableSpringCacheNullValueSupport()
                .build();

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer)
                );
        Map<String, RedisCacheConfiguration> perCache = new HashMap<>();
        perCache.put(ROLE_SECTION, redisCacheConfiguration.entryTtl(Duration.ofDays(rolesTtldays)));
        perCache.put(USER_SECTION, redisCacheConfiguration.entryTtl(Duration.ofDays(usersTtldays)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(perCache)
                .build();
    }

}