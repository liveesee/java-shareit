package ru.practicum.shareit.booking;

import java.util.List;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final ItemRepository itemRepository;
	private final UserService userService;

	@Override
	public BookingDto create(long userId, BookingCreateRequestDto bookingDto) {
		validateBookingDates(bookingDto);

		User booker = userService.getUserOrThrow(userId);
		Item item = itemRepository.findById(bookingDto.getItemId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

		if (!Boolean.TRUE.equals(item.getAvailable())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is not available for booking");
		}
		if (item.getOwner() != null && item.getOwner().getId() != null && item.getOwner().getId().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner cannot book own item");
		}
		if (bookingRepository.existsOverlappingBooking(
				item.getId(),
				bookingDto.getStart(),
				bookingDto.getEnd(),
				List.of(Status.WAITING, Status.APPROVED))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking dates overlap with existing booking");
		}

		Booking booking = BookingMapper.toBooking(bookingDto);
		booking.setBooker(booker);
		booking.setItem(item);
		booking.setStatus(Status.WAITING);

		return BookingMapper.toBookingDto(bookingRepository.save(booking));
	}

	@Override
	@Transactional(readOnly = true)
	public BookingDto getById(long userId, long bookingId) {
		Booking booking = getExisting(bookingId);

		boolean isBooker = booking.getBooker() != null
				&& booking.getBooker().getId() != null
				&& booking.getBooker().getId().equals(userId);
		boolean isOwner = booking.getItem() != null
				&& booking.getItem().getOwner() != null
				&& booking.getItem().getOwner().getId() != null
				&& booking.getItem().getOwner().getId().equals(userId);

		if (!isBooker && !isOwner) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
		}

		return BookingMapper.toBookingDto(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookingDto> getAllByBooker(long userId, String state) {
		userService.getUserOrThrow(userId);
		BookingState bookingState = BookingState.from(state);
		LocalDateTime now = LocalDateTime.now();

		return bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
				.filter(booking -> matchesState(booking, bookingState, now))
				.map(BookingMapper::toBookingDto)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookingDto> getAllByOwner(long userId, String state) {
		userService.getUserOrThrow(userId);
		BookingState bookingState = BookingState.from(state);
		LocalDateTime now = LocalDateTime.now();

		return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId).stream()
				.filter(booking -> matchesState(booking, bookingState, now))
				.map(BookingMapper::toBookingDto)
				.toList();
	}

	@Override
	public BookingDto approve(long userId, long bookingId, boolean approved) {
		Booking booking = getExisting(bookingId);

		if (booking.getItem() == null || booking.getItem().getOwner() == null
				|| booking.getItem().getOwner().getId() == null
				|| !booking.getItem().getOwner().getId().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can approve booking");
		}

		if (booking.getStatus() != Status.WAITING) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking has already been processed");
		}

		booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
		return BookingMapper.toBookingDto(bookingRepository.save(booking));
	}

	private void validateBookingDates(BookingCreateRequestDto bookingDto) {
		if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking end must be after start");
		}
	}

	private boolean matchesState(Booking booking, BookingState state, LocalDateTime now) {
		return switch (state) {
			case ALL -> true;
			case CURRENT -> isCurrent(booking, now);
			case PAST -> booking.getEnd().isBefore(now);
			case FUTURE -> booking.getStart().isAfter(now);
			case WAITING -> booking.getStatus() == Status.WAITING;
			case REJECTED -> booking.getStatus() == Status.REJECTED;
		};
	}

	private boolean isCurrent(Booking booking, LocalDateTime now) {
		return (booking.getStart().isBefore(now) || booking.getStart().isEqual(now))
				&& (booking.getEnd().isAfter(now) || booking.getEnd().isEqual(now));
	}

	private Booking getExisting(long bookingId) {
		return bookingRepository.findById(bookingId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
	}
}
