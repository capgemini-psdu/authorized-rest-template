package com.capgemini.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * This class is responsible for making REST HTTP Client calls using Basic Auth.
 * 
 * It is a simple wrapper around the Spring RestTemplate that provides a simple
 * way to configure HTTP basic authentication.
 */
public class AuthorizedRestTemplate extends RestTemplate {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizedRestTemplate.class);

	public AuthorizedRestTemplate(String username, String password) {
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			LOGGER.error("Invalid/empty credentials were specified in AuthorizedRestTemplate.");
		}
		addAuthentication(username, password);
	}

	private void addAuthentication(String username, String password) {
		if (username == null) {
			return;
		}
		List<ClientHttpRequestInterceptor> interceptors = Collections
				.<ClientHttpRequestInterceptor> singletonList(new BasicAuthorizationInterceptor(username, password));

		List<ClientHttpRequestInterceptor> interceptorList = this.getInterceptors();
		if (interceptorList != null) {
			interceptorList.addAll(interceptors);
		} else {
			this.setInterceptors(interceptors);
		}

	}

	/**
	 * The responsibility of this class is to add an Authorisation header into
	 * intercepted HttpRequests.
	 */
	private static class BasicAuthorizationInterceptor implements ClientHttpRequestInterceptor {

		private final String username;
		private final String password;

		public BasicAuthorizationInterceptor(String username, String password) {
			this.username = username;
			this.password = (password == null ? "" : password);
		}

		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {

			String plainCreds = username + ":" + password;
			byte[] plainCredsBytes = plainCreds.getBytes();
			byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
			String authString = "Basic " + new String(base64CredsBytes);

			request.getHeaders().add("Authorization", authString);

			LOGGER.debug("Adding authorization header to HTTP request");
			return execution.execute(request, body);
		}

	}

}
