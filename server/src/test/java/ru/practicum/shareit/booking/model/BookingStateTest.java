package ru.practicum.shareit.booking.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BookingStateTest {
	@Test
	void fromShouldReturnMatchingState() {
		assertEquals(BookingState.ALL, BookingState.from("ALL").orElseThrow());
	}

	@Test
	void fromShouldReturnEmptyForUnknownState() {
		assertTrue(BookingState.from("UNKNOWN").isEmpty());
	}
}
