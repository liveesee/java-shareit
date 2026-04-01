package ru.practicum.shareit.booking.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum BookingState {
	ALL,
	CURRENT,
	PAST,
	FUTURE,
	WAITING,
	REJECTED;

	public static BookingState from(String state) {
		try {
			return BookingState.valueOf(state);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown state: " + state);
		}
	}
}
