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
	void toBookingShouldReturnNullForNullDto() {
		assertNull(BookingMapper.toBooking(null));
	}

	@Test
	void toBookingShouldMapDates() {
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusDays(1);
		BookingCreateRequestDto dto = BookingCreateRequestDto.builder().itemId(1L).start(start).end(end).build();

		Booking booking = BookingMapper.toBooking(dto);

		assertEquals(start, booking.getStart());
		assertEquals(end, booking.getEnd());
	}

	@Test
	void toBookingDtoShouldReturnNullForNullBooking() {
		assertNull(BookingMapper.toBookingDto(null));
	}

	@Test
	void toBookingDtoShouldMapNestedNulls() {
		Booking booking = Booking.builder()
				.id(1L)
				.start(LocalDateTime.now())
				.end(LocalDateTime.now().plusDays(1))
				.status(Status.WAITING)
				.item(null)
				.booker(null)
				.build();

		BookingDto dto = BookingMapper.toBookingDto(booking);

		assertEquals(1L, dto.getId());
		assertNull(dto.getItem());
		assertNull(dto.getBooker());
	}

	@Test
	void toBookingDtoShouldMapItemAndBooker() {
		Booking booking = Booking.builder()
				.id(2L)
				.start(LocalDateTime.now())
				.end(LocalDateTime.now().plusDays(1))
				.status(Status.APPROVED)
				.item(Item.builder().id(3L).name("Drill").build())
				.booker(User.builder().id(4L).name("Ivan").build())
				.build();

		BookingDto dto = BookingMapper.toBookingDto(booking);

		assertEquals(3L, dto.getItem().getId());
		assertEquals("Drill", dto.getItem().getName());
		assertEquals(4L, dto.getBooker().getId());
		assertEquals("Ivan", dto.getBooker().getName());
	}
}
