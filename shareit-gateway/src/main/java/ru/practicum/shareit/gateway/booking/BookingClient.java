package ru.practicum.shareit.gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.gateway.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.gateway.client.BaseClient;

@Component
public class BookingClient extends BaseClient {
	public BookingClient(RestTemplateBuilder builder,
						 ObjectMapper objectMapper,
						 @Value("${shareit-server.url}") String serverUrl) {
		super(createRestTemplate(builder, serverUrl), objectMapper);
	}

	public ResponseEntity<Object> create(long userId, BookingCreateRequestDto bookingDto) {
		return post("/bookings", userId, bookingDto);
	}

	public ResponseEntity<Object> getAllByBooker(long userId, String state) {
		String path = UriComponentsBuilder.fromPath("/bookings")
				.queryParam("state", state)
				.build()
				.encode()
				.toUriString();
		return get(path, userId);
	}

	public ResponseEntity<Object> getAllByOwner(long userId, String state) {
		String path = UriComponentsBuilder.fromPath("/bookings/owner")
				.queryParam("state", state)
				.build()
				.encode()
				.toUriString();
		return get(path, userId);
	}

	public ResponseEntity<Object> getById(long userId, long bookingId) {
		return get("/bookings/" + bookingId, userId);
	}

	public ResponseEntity<Object> approve(long userId, long bookingId, boolean approved) {
		String path = UriComponentsBuilder.fromPath("/bookings/" + bookingId)
				.queryParam("approved", approved)
				.build()
				.encode()
				.toUriString();
		return patch(path, userId, null);
	}

	private static RestTemplate createRestTemplate(RestTemplateBuilder builder, String serverUrl) {
		return builder.rootUri(serverUrl).build();
	}
}
