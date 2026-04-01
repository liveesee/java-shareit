package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public final class BookingMapper {
	private BookingMapper() {
	}

	public static Booking toBooking(BookingCreateRequestDto dto) {
		if (dto == null) {
			return null;
		}
		return Booking.builder()
				.start(dto.getStart())
				.end(dto.getEnd())
				.build();
	}

	public static BookingDto toBookingDto(Booking booking) {
		if (booking == null) {
			return null;
		}
		return BookingDto.builder()
				.id(booking.getId())
				.start(booking.getStart())
				.end(booking.getEnd())
				.status(booking.getStatus())
				.item(toItemDto(booking.getItem()))
				.booker(toBookerDto(booking.getBooker()))
				.build();
	}

	private static BookingItemDto toItemDto(Item item) {
		if (item == null) {
			return null;
		}
		return BookingItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.build();
	}

	private static BookingBookerDto toBookerDto(User user) {
		if (user == null) {
			return null;
		}
		return BookingBookerDto.builder()
				.id(user.getId())
				.name(user.getName())
				.build();
	}
}
