package ru.practicum.shareit.gateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.gateway.client.BaseClient;
import ru.practicum.shareit.gateway.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.gateway.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateRequestDto;

@Component
public class ItemClient extends BaseClient {
	public ItemClient(RestTemplateBuilder builder,
					  ObjectMapper objectMapper,
					  @Value("${shareit-server.url}") String serverUrl) {
		super(createRestTemplate(builder, serverUrl), objectMapper);
	}

	public ResponseEntity<Object> create(long userId, ItemCreateRequestDto itemDto) {
		return post("/items", userId, itemDto);
	}

	public ResponseEntity<Object> update(long userId, long itemId, ItemUpdateRequestDto itemDto) {
		return patch("/items/" + itemId, userId, itemDto);
	}

	public ResponseEntity<Object> addComment(long userId, long itemId, CommentCreateRequestDto commentDto) {
		return post("/items/" + itemId + "/comment", userId, commentDto);
	}

	public ResponseEntity<Object> getById(long userId, long itemId) {
		return get("/items/" + itemId, userId);
	}

	public ResponseEntity<Object> getAllByOwner(long userId) {
		return get("/items", userId);
	}

	public ResponseEntity<Object> search(long userId, String text) {
		String path = UriComponentsBuilder.fromPath("/items/search")
				.queryParam("text", text)
				.build()
				.encode()
				.toUriString();
		return get(path, userId);
	}

	private static RestTemplate createRestTemplate(RestTemplateBuilder builder, String serverUrl) {
		return builder.rootUri(serverUrl).build();
	}
}
