package ru.practicum.shareit.gateway.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.client.BaseClient;
import ru.practicum.shareit.gateway.user.dto.UserDto;

@Component
public class UserClient extends BaseClient {
	public UserClient(RestTemplateBuilder builder,
					  ObjectMapper objectMapper,
					  @Value("${shareit-server.url}") String serverUrl) {
		super(createRestTemplate(builder, serverUrl), objectMapper);
	}

	public ResponseEntity<Object> create(UserDto userDto) {
		return post("/users", null, userDto);
	}

	public ResponseEntity<Object> update(long userId, UserDto userDto) {
		return patch("/users/" + userId, null, userDto);
	}

	public ResponseEntity<Object> getById(long userId) {
		return get("/users/" + userId);
	}

	public ResponseEntity<Object> getAll() {
		return get("/users");
	}

	public ResponseEntity<Object> delete(long userId) {
		return delete("/users/" + userId);
	}

	private static RestTemplate createRestTemplate(RestTemplateBuilder builder, String serverUrl) {
		return builder.rootUri(serverUrl).build();
	}
}
