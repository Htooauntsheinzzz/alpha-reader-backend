package com.web.alpha.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.web.alpha.appgenre.dto.AppGenreResponse;
import com.web.alpha.storytype.dto.StoryTypeResponse;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;

class RedisConfigTest {

	@Test
	void genreCachesDeserializeExistingJsonToDeclaredResponseTypes() {
		AppGenreResponse response = genreResponse();
		GenericJacksonJsonRedisSerializer previousSerializer = GenericJacksonJsonRedisSerializer.builder().build();

		AppGenreResponse cachedResponse = RedisConfig.genreSerializationPair().read(
				ByteBuffer.wrap(previousSerializer.serialize(response))
		);
		List<AppGenreResponse> cachedList = RedisConfig.genreListSerializationPair().read(
				ByteBuffer.wrap(previousSerializer.serialize(List.of(response)))
		);

		assertInstanceOf(AppGenreResponse.class, cachedResponse);
		assertEquals(1, cachedList.size());
		assertInstanceOf(AppGenreResponse.class, cachedList.getFirst());
	}

	@Test
	void storyTypeCachesDeserializeExistingJsonToDeclaredResponseTypes() {
		StoryTypeResponse response = response();
		GenericJacksonJsonRedisSerializer previousSerializer = GenericJacksonJsonRedisSerializer.builder().build();

		StoryTypeResponse cachedResponse = RedisConfig.storyTypeSerializationPair().read(
				ByteBuffer.wrap(previousSerializer.serialize(response))
		);
		List<StoryTypeResponse> cachedList = RedisConfig.storyTypeListSerializationPair().read(
				ByteBuffer.wrap(previousSerializer.serialize(List.of(response)))
		);

		assertInstanceOf(StoryTypeResponse.class, cachedResponse);
		assertEquals(1, cachedList.size());
		assertInstanceOf(StoryTypeResponse.class, cachedList.getFirst());
	}

	private StoryTypeResponse response() {
		return new StoryTypeResponse(
				1L,
				"Novel",
				LocalDate.of(2026, 7, 22),
				"Novel story type",
				1,
				0,
				1L,
				LocalDateTime.of(2026, 7, 22, 10, 30)
		);
	}

	private AppGenreResponse genreResponse() {
		return new AppGenreResponse(
				1L,
				"Fantasy",
				LocalDate.of(2026, 7, 22),
				"Fantasy genre",
				1,
				0,
				1L,
				LocalDateTime.of(2026, 7, 22, 10, 30)
		);
	}
}
