package com.web.alpha.config;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

@Configuration
@EnableCaching
public class RedisConfig {

	private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

	private static final Set<String> CACHE_NAMES = Set.of(
		"admin-cache",
		"book-cache",
		"chapter-cache",
		"category-cache",
		"banner-cache"
	);

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(DEFAULT_TTL)
			.disableCachingNullValues()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(GenericJacksonJsonRedisSerializer.builder().build()));

		Map<String, RedisCacheConfiguration> cacheConfigurations = CACHE_NAMES.stream()
			.collect(Collectors.toMap(cacheName -> cacheName, cacheName -> cacheConfiguration));

		return RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(cacheConfiguration)
			.withInitialCacheConfigurations(cacheConfigurations)
			.initialCacheNames(CACHE_NAMES)
			.build();
	}
}
