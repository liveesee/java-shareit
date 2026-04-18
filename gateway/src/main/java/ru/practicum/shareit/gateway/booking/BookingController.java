package ru.practicum.shareit.gateway.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.gateway.booking.dto.BookingCreateRequestDto;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) @Positive long userId,
										 @Valid @RequestBody BookingCreateRequestDto bookingDto) {
		return bookingClient.create(userId, bookingDto);
	}

	@GetMapping
	public ResponseEntity<Object> getAllByBooker(@RequestHeader(USER_ID_HEADER) @Positive long userId,
												 @RequestParam(defaultValue = "ALL") String state) {
		return bookingClient.getAllByBooker(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_ID_HEADER) @Positive long userId,
												@RequestParam(defaultValue = "ALL") String state) {
		return bookingClient.getAllByOwner(userId, state);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) @Positive long userId,
										  @PathVariable @Positive long bookingId) {
		return bookingClient.getById(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approve(@RequestHeader(USER_ID_HEADER) @Positive long userId,
										  @PathVariable @Positive long bookingId,
										  @RequestParam boolean approved) {
		return bookingClient.approve(userId, bookingId, approved);
	}
}
