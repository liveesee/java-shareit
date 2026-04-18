package ru.practicum.shareit.gateway.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public abstract class BaseClient {
	protected static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	protected ResponseEntity<Object> get(String path) {
		return exchange(HttpMethod.GET, path, null, null);
	}

	protected ResponseEntity<Object> get(String path, Long userId) {
		return exchange(HttpMethod.GET, path, userId, null);
	}

	protected ResponseEntity<Object> post(String path, Long userId, Object body) {
		return exchange(HttpMethod.POST, path, userId, body);
	}

	protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
		return exchange(HttpMethod.PATCH, path, userId, body);
	}

	protected ResponseEntity<Object> delete(String path) {
		return exchange(HttpMethod.DELETE, path, null, null);
	}

	private ResponseEntity<Object> exchange(HttpMethod method, String path, Long userId, Object body) {
		HttpHeaders headers = new HttpHeaders();
		if (userId != null) {
			headers.set(USER_ID_HEADER, userId.toString());
		}
		if (body != null) {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}

		HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
		try {
			return restTemplate.exchange(path, method, requestEntity, Object.class);
		} catch (HttpStatusCodeException exception) {
			return ResponseEntity.status(exception.getStatusCode())
					.body(parseBody(exception.getResponseBodyAsString()));
		}
	}

	private Object parseBody(String responseBody) {
		if (responseBody == null || responseBody.isBlank()) {
			return null;
		}

		try {
			return objectMapper.readValue(responseBody, Object.class);
		} catch (JsonProcessingException exception) {
			return responseBody;
		}
	}
}
