package com.web.alpha.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
@EnableCaching
public class RedisConfig {

	private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

	private static final Set<String> CACHE_NAMES = Set.of(
		"admin-cache",
		"book-cache",
		"chapter-cache",
		"category-cache",
		"banner-cache",
		"genre-cache",
		"genre-list-cache",
		"membership-plan-cache",
		"membership-plan-list-cache",
		"story-type-cache",
		"story-type-list-cache"
	);

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		BasicPolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
			.allowIfSubType("com.web.alpha.")
			.allowIfSubType("java.math.")
			.allowIfSubType("java.time.")
			.allowIfSubType("java.util.")
			.build();
		GenericJacksonJsonRedisSerializer valueSerializer = GenericJacksonJsonRedisSerializer.builder()
			.enableDefaultTyping(typeValidator)
			.writer((mapper, value) -> mapper.writerFor(Object.class)
				.writeValueAsBytes(normalizeCollection(value)))
			.build();

		RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(DEFAULT_TTL)
			.disableCachingNullValues()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));

		return RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(cacheConfiguration)
			.initialCacheNames(CACHE_NAMES)
			.transactionAware()
			.build();
	}

	private static Object normalizeCollection(Object value) {
		if (value instanceof List<?> list) {
			return new ArrayList<>(list);
		}
		if (value instanceof Set<?> set) {
			return new HashSet<>(set);
		}
		if (value instanceof Map<?, ?> map) {
			return new LinkedHashMap<>(map);
		}
		return value;
	}
}
