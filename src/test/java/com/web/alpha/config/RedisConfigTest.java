package com.web.alpha.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.web.alpha.appgenre.dto.AppGenreResponse;
import com.web.alpha.membership.dto.MembershipPlanResponse;
import com.web.alpha.membership.enums.MembershipDurationUnit;
import com.web.alpha.storytype.dto.StoryTypeResponse;
import java.nio.ByteBuffer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;

class RedisConfigTest {

	@Test
	void membershipPlanCachesUseDeclaredResponseTypes() {
		MembershipPlanResponse response = membershipPlanResponse();
		GenericJacksonJsonRedisSerializer previousSerializer = GenericJacksonJsonRedisSerializer.builder().build();

		MembershipPlanResponse cachedResponse = RedisConfig.membershipPlanSerializationPair().read(
				ByteBuffer.wrap(previousSerializer.serialize(response))
		);
		List<MembershipPlanResponse> cachedList = RedisConfig.membershipPlanListSerializationPair().read(
				ByteBuffer.wrap(previousSerializer.serialize(List.of(response)))
		);

		assertInstanceOf(MembershipPlanResponse.class, cachedResponse);
		assertEquals(1, cachedList.size());
		assertInstanceOf(MembershipPlanResponse.class, cachedList.getFirst());
	}

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

	private MembershipPlanResponse membershipPlanResponse() {
		return new MembershipPlanResponse(
				1L,
				"PLN-001",
				"Monthly Plan",
				new BigDecimal("9.99"),
				1L,
				"Monthly membership",
				MembershipDurationUnit.MONTH,
				10,
				0,
				1,
				0,
				1L,
				LocalDateTime.of(2026, 7, 30, 10, 30),
				1L,
				LocalDateTime.of(2026, 7, 30, 10, 30)
		);
	}
}
