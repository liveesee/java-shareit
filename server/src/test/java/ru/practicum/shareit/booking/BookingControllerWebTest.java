package ru.practicum.shareit.booking;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

@WebMvcTest(BookingController.class)
class BookingControllerWebTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookingService bookingService;

	@Test
	void createShouldReturnBooking() throws Exception {
		when(bookingService.create(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any()))
				.thenReturn(BookingDto.builder().id(1L).status(ru.practicum.shareit.booking.model.Status.WAITING).build());

		mockMvc.perform(post("/bookings")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"itemId\":1,\"start\":\"2030-01-02T10:00:00\",\"end\":\"2030-01-03T10:00:00\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void getAllByBookerShouldReturnBookings() throws Exception {
		when(bookingService.getAllByBooker(1L, "ALL")).thenReturn(List.of(BookingDto.builder().id(1L).build()));

		mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 1).param("state", "ALL"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1));
	}

	@Test
	void getAllByOwnerShouldReturnBookings() throws Exception {
		when(bookingService.getAllByOwner(1L, "ALL")).thenReturn(List.of(BookingDto.builder().id(1L).build()));

		mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1).param("state", "ALL"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1));
	}

	@Test
	void getByIdShouldReturnBooking() throws Exception {
		when(bookingService.getById(1L, 2L)).thenReturn(BookingDto.builder().id(2L).build());

		mockMvc.perform(get("/bookings/2").header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(2));
	}

	@Test
	void approveShouldReturnBooking() throws Exception {
		when(bookingService.approve(1L, 2L, true)).thenReturn(BookingDto.builder().id(2L).build());

		mockMvc.perform(patch("/bookings/2").header("X-Sharer-User-Id", 1).param("approved", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(2));
	}
}
