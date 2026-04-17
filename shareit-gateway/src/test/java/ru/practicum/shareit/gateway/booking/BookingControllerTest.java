package ru.practicum.shareit.gateway.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.practicum.shareit.gateway.booking.dto.BookingCreateRequestDto;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
	@Mock
	private BookingClient bookingClient;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper().findAndRegisterModules();
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders.standaloneSetup(new BookingController(bookingClient))
				.setValidator(validator)
				.build();
	}

	@Test
	void createShouldDelegateToClient() throws Exception {
		BookingCreateRequestDto dto = BookingCreateRequestDto.builder()
				.itemId(1L)
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.build();
		when(bookingClient.create(any(Long.class), any(BookingCreateRequestDto.class)))
				.thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(post("/bookings")
						.header("X-Sharer-User-Id", 1)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk());
	}

	@Test
	void getAllByBookerShouldDelegateToClient() throws Exception {
		when(bookingClient.getAllByBooker(1L, "ALL")).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(get("/bookings")
						.header("X-Sharer-User-Id", 1)
						.param("state", "ALL"))
				.andExpect(status().isOk());

		verify(bookingClient).getAllByBooker(1L, "ALL");
	}

	@Test
	void getAllByOwnerShouldDelegateToClient() throws Exception {
		when(bookingClient.getAllByOwner(1L, "ALL")).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(get("/bookings/owner")
						.header("X-Sharer-User-Id", 1)
						.param("state", "ALL"))
				.andExpect(status().isOk());
	}

	@Test
	void getByIdShouldDelegateToClient() throws Exception {
		when(bookingClient.getById(1L, 2L)).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(get("/bookings/2")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk());
	}

	@Test
	void approveShouldDelegateToClient() throws Exception {
		when(bookingClient.approve(1L, 2L, true)).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(patch("/bookings/2")
						.header("X-Sharer-User-Id", 1)
						.param("approved", "true"))
				.andExpect(status().isOk());
	}
}
