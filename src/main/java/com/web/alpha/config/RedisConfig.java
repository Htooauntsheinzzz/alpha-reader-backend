package com.web.alpha.config;

import com.web.alpha.appgenre.dto.AppGenreResponse;
import com.web.alpha.storytype.dto.StoryTypeResponse;
import java.time.Duration;
import java.util.List;
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
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.type.TypeFactory;

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
		"story-type-cache",
		"story-type-list-cache"
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
		cacheConfigurations.put(
			"genre-cache",
			cacheConfiguration.serializeValuesWith(genreSerializationPair())
		);
		cacheConfigurations.put(
			"genre-list-cache",
			cacheConfiguration.serializeValuesWith(genreListSerializationPair())
		);
		cacheConfigurations.put(
			"story-type-cache",
			cacheConfiguration.serializeValuesWith(storyTypeSerializationPair())
		);
		cacheConfigurations.put(
			"story-type-list-cache",
			cacheConfiguration.serializeValuesWith(storyTypeListSerializationPair())
		);

		return RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(cacheConfiguration)
			.withInitialCacheConfigurations(cacheConfigurations)
			.initialCacheNames(CACHE_NAMES)
			.transactionAware()
			.build();
	}

	static RedisSerializationContext.SerializationPair<AppGenreResponse> genreSerializationPair() {
		return RedisSerializationContext.SerializationPair.fromSerializer(
				new JacksonJsonRedisSerializer<>(AppGenreResponse.class)
		);
	}

	static RedisSerializationContext.SerializationPair<List<AppGenreResponse>> genreListSerializationPair() {
		return RedisSerializationContext.SerializationPair.fromSerializer(
				new JacksonJsonRedisSerializer<>(
						TypeFactory.createDefaultInstance()
								.constructCollectionType(List.class, AppGenreResponse.class)
				)
		);
	}

	static RedisSerializationContext.SerializationPair<StoryTypeResponse> storyTypeSerializationPair() {
		return RedisSerializationContext.SerializationPair.fromSerializer(
				new JacksonJsonRedisSerializer<>(StoryTypeResponse.class)
		);
	}

	static RedisSerializationContext.SerializationPair<List<StoryTypeResponse>> storyTypeListSerializationPair() {
		return RedisSerializationContext.SerializationPair.fromSerializer(
				new JacksonJsonRedisSerializer<>(
						TypeFactory.createDefaultInstance()
								.constructCollectionType(List.class, StoryTypeResponse.class)
				)
		);
	}
}
