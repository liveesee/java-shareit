package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
	@Mock
	private BookingRepository bookingRepository;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private UserService userService;

	@InjectMocks
	private BookingServiceImpl bookingService;

	@Test
	void createShouldSaveWaitingBooking() {
		LocalDateTime start = LocalDateTime.now().plusDays(1);
		LocalDateTime end = start.plusDays(1);
		BookingCreateRequestDto dto = BookingCreateRequestDto.builder().itemId(2L).start(start).end(end).build();
		User user = User.builder().id(1L).name("Booker").build();
		Item item = Item.builder().id(2L).available(true).owner(User.builder().id(3L).build()).name("Drill").build();

		when(userService.getUserOrThrow(1L)).thenReturn(user);
		when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
		when(bookingRepository.existsOverlappingBooking(2L, start, end, List.of(Status.APPROVED))).thenReturn(false);
		when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
			Booking booking = invocation.getArgument(0);
			booking.setId(10L);
			return booking;
		});

		BookingDto result = bookingService.create(1L, dto);

		assertEquals(10L, result.getId());
		assertEquals(Status.WAITING, result.getStatus());
	}

	@Test
	void getByIdShouldReturnBookingForBooker() {
		Booking booking = Booking.builder()
				.id(1L)
				.booker(User.builder().id(1L).name("Booker").build())
				.item(Item.builder().id(2L).owner(User.builder().id(3L).build()).name("Drill").build())
				.status(Status.WAITING)
				.start(LocalDateTime.now())
				.end(LocalDateTime.now().plusDays(1))
				.build();
		when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

		BookingDto result = bookingService.getById(1L, 1L);

		assertEquals(1L, result.getId());
	}

	@Test
	void getAllByBookerShouldFilterByState() {
		Booking pastBooking = Booking.builder()
				.id(1L)
				.booker(User.builder().id(1L).name("Booker").build())
				.item(Item.builder().id(2L).name("Drill").build())
				.status(Status.APPROVED)
				.start(LocalDateTime.now().minusDays(2))
				.end(LocalDateTime.now().minusDays(1))
				.build();
		when(userService.getUserOrThrow(1L)).thenReturn(User.builder().id(1L).build());
		when(bookingRepository.findAllByBookerIdOrderByStartDesc(1L)).thenReturn(List.of(pastBooking));

		List<BookingDto> result = bookingService.getAllByBooker(1L, "PAST");

		assertEquals(1, result.size());
	}

	@Test
	void getAllByOwnerShouldRejectUnknownState() {
		when(userService.getUserOrThrow(1L)).thenReturn(User.builder().id(1L).build());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> bookingService.getAllByOwner(1L, "BAD"));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}

	@Test
	void approveShouldSetApprovedStatus() {
		Booking booking = Booking.builder()
				.id(1L)
				.item(Item.builder().owner(User.builder().id(5L).build()).name("Drill").build())
				.booker(User.builder().id(2L).build())
				.status(Status.WAITING)
				.start(LocalDateTime.now())
				.end(LocalDateTime.now().plusDays(1))
				.build();
		when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
		when(bookingRepository.save(booking)).thenReturn(booking);

		BookingDto result = bookingService.approve(5L, 1L, true);

		assertEquals(Status.APPROVED, result.getStatus());
	}
}
