package com.web.alpha.common.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CorrelationIdFilterTest {

	private final CorrelationIdFilter filter = new CorrelationIdFilter();

	@Test
	void preservesValidRequestIdAndAddsResponseHeader() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/admin/genres");
		request.addHeader(CorrelationIdFilter.HEADER_NAME, "request-123");
		MockHttpServletResponse response = new MockHttpServletResponse();

		filter.doFilter(request, response, new MockFilterChain());

		assertEquals("request-123", response.getHeader(CorrelationIdFilter.HEADER_NAME));
		assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
	}

	@Test
	void replacesUnsafeRequestId() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/admin/genres");
		request.addHeader(CorrelationIdFilter.HEADER_NAME, "unsafe request id");
		MockHttpServletResponse response = new MockHttpServletResponse();

		filter.doFilter(request, response, new MockFilterChain());

		String requestId = response.getHeader(CorrelationIdFilter.HEADER_NAME);
		assertNotNull(requestId);
		assertEquals(36, requestId.length());
		assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
	}
}
