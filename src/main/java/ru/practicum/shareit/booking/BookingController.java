package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final BookingService bookingService;

	@PostMapping
	public BookingDto create(@RequestHeader(USER_ID_HEADER) long userId,
							 @Valid @RequestBody BookingCreateRequestDto bookingDto) {
		return bookingService.create(userId, bookingDto);
	}

	@GetMapping
	public List<BookingDto> getAllByBooker(@RequestHeader(USER_ID_HEADER) long userId,
										   @RequestParam(defaultValue = "ALL") String state) {
		return bookingService.getAllByBooker(userId, state);
	}

	@GetMapping("/owner")
	public List<BookingDto> getAllByOwner(@RequestHeader(USER_ID_HEADER) long userId,
										  @RequestParam(defaultValue = "ALL") String state) {
		return bookingService.getAllByOwner(userId, state);
	}

	@GetMapping("/{bookingId}")
	public BookingDto getById(@RequestHeader(USER_ID_HEADER) long userId,
							  @PathVariable long bookingId) {
		return bookingService.getById(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public BookingDto approve(@RequestHeader(USER_ID_HEADER) long userId,
							  @PathVariable long bookingId,
							  @RequestParam boolean approved) {
		return bookingService.approve(userId, bookingId, approved);
	}
}
