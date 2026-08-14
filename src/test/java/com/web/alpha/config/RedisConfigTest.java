package com.web.alpha.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

class RedisConfigTest {

	@Test
	void everyConfiguredAndFutureCachePreservesValueTypes() {
		RedisCacheManager cacheManager = (RedisCacheManager) new RedisConfig()
				.cacheManager(redisConnectionFactoryStub());
		cacheManager.afterPropertiesSet();

		cacheManager.getCacheNames().forEach(cacheName -> assertCacheRoundTrip(cacheManager, cacheName));
		assertCacheRoundTrip(cacheManager, "future-module-cache");
	}

	private void assertCacheRoundTrip(RedisCacheManager cacheManager, String cacheName) {
		CacheTestValue value = new CacheTestValue(
				1L,
				"Test value",
				new BigDecimal("9.99"),
				LocalDateTime.of(2026, 8, 14, 12, 0)
		);

		Object cachedValue = deserializeWithConfiguredCache(cacheManager, cacheName, value);
		assertInstanceOf(CacheTestValue.class, cachedValue);

		Object cachedListValue = deserializeWithConfiguredCache(cacheManager, cacheName, List.of(value));
		List<?> cachedList = assertInstanceOf(List.class, cachedListValue);
		assertEquals(1, cachedList.size());
		assertInstanceOf(CacheTestValue.class, cachedList.getFirst());

		Object cachedSetValue = deserializeWithConfiguredCache(cacheManager, cacheName, Set.of(value));
		Set<?> cachedSet = assertInstanceOf(Set.class, cachedSetValue);
		assertInstanceOf(CacheTestValue.class, cachedSet.iterator().next());

		Object cachedMapValue = deserializeWithConfiguredCache(cacheManager, cacheName, Map.of("value", value));
		Map<?, ?> cachedMap = assertInstanceOf(Map.class, cachedMapValue);
		assertInstanceOf(CacheTestValue.class, cachedMap.get("value"));
	}

	private Object deserializeWithConfiguredCache(
			RedisCacheManager cacheManager,
			String cacheName,
			Object value
	) {
		TransactionAwareCacheDecorator cache = (TransactionAwareCacheDecorator)
				cacheManager.getCache(cacheName);
		assertNotNull(cache);
		RedisCache redisCache = (RedisCache) cache.getTargetCache();

		var serializationPair = redisCache.getCacheConfiguration().getValueSerializationPair();
		return serializationPair.read(serializationPair.write(value));
	}

	private RedisConnectionFactory redisConnectionFactoryStub() {
		return (RedisConnectionFactory) Proxy.newProxyInstance(
				RedisConnectionFactory.class.getClassLoader(),
				new Class<?>[] { RedisConnectionFactory.class },
				(proxy, method, arguments) -> null
		);
	}

	private record CacheTestValue(Long id, String name, BigDecimal amount, LocalDateTime createdAt) {}
}
