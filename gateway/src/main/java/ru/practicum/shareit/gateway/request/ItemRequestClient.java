package ru.practicum.shareit.gateway.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.client.BaseClient;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDto;

@Component
public class ItemRequestClient extends BaseClient {
	public ItemRequestClient(RestTemplateBuilder builder,
							 ObjectMapper objectMapper,
							 @Value("${shareit-server.url}") String serverUrl) {
		super(createRestTemplate(builder, serverUrl), objectMapper);
	}

	public ResponseEntity<Object> create(long userId, ItemRequestDto itemRequestDto) {
		return post("/requests", userId, itemRequestDto);
	}

	public ResponseEntity<Object> getOwnerRequests(long userId) {
		return get("/requests", userId);
	}

	public ResponseEntity<Object> getAll(long userId) {
		return get("/requests/all", userId);
	}

	public ResponseEntity<Object> getById(long userId, long requestId) {
		return get("/requests/" + requestId, userId);
	}

	private static RestTemplate createRestTemplate(RestTemplateBuilder builder, String serverUrl) {
		return builder.rootUri(serverUrl).build();
	}
}
