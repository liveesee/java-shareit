package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

class BookingMapperTest {
	@Test
	void toBookingShouldMapCreateDto() {
		LocalDateTime start = LocalDateTime.now().plusDays(1);
		LocalDateTime end = start.plusDays(1);

		Booking booking = BookingMapper.toBooking(BookingCreateRequestDto.builder()
				.itemId(1L)
				.start(start)
				.end(end)
				.build());

		assertEquals(start, booking.getStart());
		assertEquals(end, booking.getEnd());
	}

	@Test
	void toBookingDtoShouldMapBooking() {
		Booking booking = Booking.builder()
				.id(1L)
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.status(Status.WAITING)
				.item(Item.builder().id(2L).name("Drill").build())
				.booker(User.builder().id(3L).name("Ivan").build())
				.build();

		BookingDto dto = BookingMapper.toBookingDto(booking);

		assertEquals(1L, dto.getId());
		assertEquals(2L, dto.getItem().getId());
		assertEquals(3L, dto.getBooker().getId());
	}

	@Test
	void toBookingShouldReturnNullForNullInput() {
		assertNull(BookingMapper.toBooking(null));
	}
}
