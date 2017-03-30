package com.capgemini.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.capgemini.rest.AuthorizedRestTemplate;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class AuthorizedRestTemplateTest {

	private static final String EXPECTED_AUTH_HEADER = "Basic "
			+ Base64.encodeBase64URLSafeString("myuser:mypassword".getBytes());

	private final AuthorizedRestTemplate template = new AuthorizedRestTemplate("myuser", "mypassword");

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8198); // Random port

	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test
	public void testAuthHeaderIsAdded() throws Exception {
		// Set up wiremock stub to respond to the rest template
		stubFor(get(urlEqualTo("/some/url"))
				.willReturn(aResponse()
				.withStatus(200)
				.withBody("OK")));
		
		// Use the template to submit a request to wiremock
		ResponseEntity<String> result = template.getForEntity("http://localhost:8198/some/url", String.class);
		
		// Assert that wiremock responded but more importantly verify that the request wiremock received contained the expected auth header
		assertEquals("OK", result.getBody());
		verify(getRequestedFor(urlMatching("/some/url"))
				.withHeader("Authorization", containing(EXPECTED_AUTH_HEADER)));
	}

}