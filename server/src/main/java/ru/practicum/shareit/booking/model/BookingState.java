package ru.practicum.shareit.booking.model;

import java.util.Arrays;
import java.util.Optional;

public enum BookingState {
	ALL,
	CURRENT,
	PAST,
	FUTURE,
	WAITING,
	REJECTED;

	public static Optional<BookingState> from(String state) {
		if (state == null) {
			return Optional.empty();
		}
		return Arrays.stream(values())
				.filter(value -> value.name().equals(state))
				.findFirst();
	}
}
