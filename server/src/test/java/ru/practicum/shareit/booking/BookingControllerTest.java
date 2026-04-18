package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
	@Mock
	private BookingService bookingService;

	@InjectMocks
	private BookingController bookingController;

	@Test
	void createShouldDelegateToService() {
		BookingCreateRequestDto requestDto = BookingCreateRequestDto.builder().itemId(1L).build();
		BookingDto result = BookingDto.builder().id(1L).build();
		when(bookingService.create(1L, requestDto)).thenReturn(result);

		assertEquals(result, bookingController.create(1L, requestDto));
	}

	@Test
	void getAllByBookerShouldDelegateToService() {
		List<BookingDto> result = List.of(BookingDto.builder().id(1L).build());
		when(bookingService.getAllByBooker(1L, "ALL")).thenReturn(result);

		assertEquals(result, bookingController.getAllByBooker(1L, "ALL"));
	}

	@Test
	void getAllByOwnerShouldDelegateToService() {
		List<BookingDto> result = List.of(BookingDto.builder().id(1L).build());
		when(bookingService.getAllByOwner(1L, "ALL")).thenReturn(result);

		assertEquals(result, bookingController.getAllByOwner(1L, "ALL"));
	}

	@Test
	void getByIdShouldDelegateToService() {
		BookingDto result = BookingDto.builder().id(1L).build();
		when(bookingService.getById(1L, 2L)).thenReturn(result);

		assertEquals(result, bookingController.getById(1L, 2L));
	}

	@Test
	void approveShouldDelegateToService() {
		BookingDto result = BookingDto.builder().id(1L).build();
		when(bookingService.approve(1L, 2L, true)).thenReturn(result);

		assertEquals(result, bookingController.approve(1L, 2L, true));
	}
}
