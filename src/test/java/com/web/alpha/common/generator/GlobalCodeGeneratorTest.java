package com.web.alpha.common.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GlobalCodeGeneratorTest {

	private final GlobalCodeGenerator generator = new GlobalCodeGenerator();

	@Test
	void generatesPlanCodeWithDefaults() {
		assertEquals("PLN-001", generator.generate("PLN", 1L));
	}

	@Test
	void generatesStoryCodeWithDefaults() {
		assertEquals("STR-012", generator.generate("STR", 12L));
	}

	@Test
	void generatesCodeWithCustomPadding() {
		assertEquals("GEN-0007", generator.generate("GEN", 7L, 4));
	}

	@Test
	void generatesCodeWithCustomPaddingAndSeparator() {
		assertEquals("PAY/00025", generator.generate("PAY", 25L, 5, "/"));
	}

	@Test
	void convertsLowercasePrefixToUppercase() {
		assertEquals("PLN-001", generator.generate("pln", 1L));
	}

	@Test
	void trimsSpacesAroundPrefix() {
		assertEquals("PLN-001", generator.generate("  PLN  ", 1L));
	}

	@Test
	void doesNotTruncateIdLongerThanPadding() {
		assertEquals("GEN-1234", generator.generate("GEN", 1234L, 3));
	}

	@Test
	void rejectsNullPrefix() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> generator.generate(null, 1L)
		);
		assertEquals("Prefix must not be null", exception.getMessage());
	}

	@Test
	void rejectsBlankPrefix() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> generator.generate("   ", 1L)
		);
		assertEquals("Prefix must not be blank", exception.getMessage());
	}

	@Test
	void rejectsInvalidPrefixCharacters() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> generator.generate("PLN!", 1L)
		);
		assertEquals(
				"Prefix must contain only letters, numbers, underscores, or hyphens",
				exception.getMessage()
		);
	}

	@Test
	void rejectsNullId() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> generator.generate("PLN", null)
		);
		assertEquals("ID must not be null", exception.getMessage());
	}

	@Test
	void rejectsZeroId() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> generator.generate("PLN", 0L)
		);
		assertEquals("ID must be greater than zero", exception.getMessage());
	}

	@Test
	void rejectsNegativeId() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> generator.generate("PLN", -1L)
		);
		assertEquals("ID must be greater than zero", exception.getMessage());
	}

	@Test
	void rejectsPaddingOutsideAllowedRange() {
		IllegalArgumentException tooShort = assertThrows(
				IllegalArgumentException.class,
				() -> generator.generate("PLN", 1L, 0)
		);
		IllegalArgumentException tooLong = assertThrows(
				IllegalArgumentException.class,
				() -> generator.generate("PLN", 1L, 13)
		);
		assertEquals("Padding length must be between 1 and 12", tooShort.getMessage());
		assertEquals("Padding length must be between 1 and 12", tooLong.getMessage());
	}

	@Test
	void rejectsBlankSeparator() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> generator.generate("PLN", 1L, 3, "   ")
		);
		assertEquals("Separator must not be blank", exception.getMessage());
	}

	@Test
	void rejectsNullSeparator() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> generator.generate("PLN", 1L, 3, null)
		);
		assertEquals("Separator must not be null", exception.getMessage());
	}
}
