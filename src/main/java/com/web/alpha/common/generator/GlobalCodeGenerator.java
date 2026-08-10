package com.web.alpha.common.generator;

import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public final class GlobalCodeGenerator {

	private static final int DEFAULT_PADDING_LENGTH = 3;
	private static final String DEFAULT_SEPARATOR = "-";
	private static final int MIN_PADDING_LENGTH = 1;
	private static final int MAX_PADDING_LENGTH = 12;
	private static final Pattern VALID_PREFIX = Pattern.compile("[A-Za-z0-9_-]+");

	public String generate(String prefix, Long id) {
		return generate(prefix, id, DEFAULT_PADDING_LENGTH, DEFAULT_SEPARATOR);
	}

	public String generate(String prefix, Long id, int paddingLength) {
		return generate(prefix, id, paddingLength, DEFAULT_SEPARATOR);
	}

	public String generate(String prefix, Long id, int paddingLength, String separator) {
		String normalizedPrefix = normalizePrefix(prefix);
		validateId(id);
		validatePaddingLength(paddingLength);
		String normalizedSeparator = normalizeSeparator(separator);
		String paddedId = String.format(Locale.ROOT, "%0" + paddingLength + "d", id);
		return normalizedPrefix + normalizedSeparator + paddedId;
	}

	private String normalizePrefix(String prefix) {
		if (prefix == null) {
			throw new IllegalArgumentException("Prefix must not be null");
		}
		String normalizedPrefix = prefix.trim();
		if (normalizedPrefix.isBlank()) {
			throw new IllegalArgumentException("Prefix must not be blank");
		}
		if (!VALID_PREFIX.matcher(normalizedPrefix).matches()) {
			throw new IllegalArgumentException(
					"Prefix must contain only letters, numbers, underscores, or hyphens"
			);
		}
		return normalizedPrefix.toUpperCase(Locale.ROOT);
	}

	private void validateId(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID must not be null");
		}
		if (id <= 0) {
			throw new IllegalArgumentException("ID must be greater than zero");
		}
	}

	private void validatePaddingLength(int paddingLength) {
		if (paddingLength < MIN_PADDING_LENGTH || paddingLength > MAX_PADDING_LENGTH) {
			throw new IllegalArgumentException("Padding length must be between 1 and 12");
		}
	}

	private String normalizeSeparator(String separator) {
		if (separator == null) {
			throw new IllegalArgumentException("Separator must not be null");
		}
		String normalizedSeparator = separator.trim();
		if (normalizedSeparator.isBlank()) {
			throw new IllegalArgumentException("Separator must not be blank");
		}
		return normalizedSeparator;
	}
}
