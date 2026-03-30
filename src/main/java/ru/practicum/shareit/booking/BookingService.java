package ru.practicum.shareit.booking;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {
	BookingDto create(long userId, BookingCreateRequestDto bookingDto);

	BookingDto getById(long userId, long bookingId);

	List<BookingDto> getAllByBooker(long userId, String state);

	List<BookingDto> getAllByOwner(long userId, String state);

	BookingDto approve(long userId, long bookingId, boolean approved);
}
