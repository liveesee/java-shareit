package ru.practicum.shareit.gateway.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.booking.dto.BookingCreateRequestDto;

class BookingClientTest {
	private BookingClient bookingClient;
	private MockRestServiceServer server;

	@BeforeEach
	void setUp() {
		bookingClient = new BookingClient(new RestTemplateBuilder(), new ObjectMapper(), "http://localhost:9090");
		RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(bookingClient, "restTemplate");
		server = MockRestServiceServer.bindTo(restTemplate).build();
	}

	@Test
	void createShouldPostToBookings() {
		server.expect(requestTo("http://localhost:9090/bookings"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess());

		assertEquals(200, bookingClient.create(1L, BookingCreateRequestDto.builder()
				.itemId(1L)
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.build()).getStatusCode().value());
	}

	@Test
	void getAllByBookerShouldCallBookingsPath() {
		server.expect(requestTo("http://localhost:9090/bookings?state=ALL"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess());

		assertEquals(200, bookingClient.getAllByBooker(1L, "ALL").getStatusCode().value());
	}

	@Test
	void getAllByOwnerShouldCallOwnerPath() {
		server.expect(requestTo("http://localhost:9090/bookings/owner?state=ALL"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess());

		assertEquals(200, bookingClient.getAllByOwner(1L, "ALL").getStatusCode().value());
	}

	@Test
	void getByIdShouldCallBookingPath() {
		server.expect(requestTo("http://localhost:9090/bookings/2"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess());

		assertEquals(200, bookingClient.getById(1L, 2L).getStatusCode().value());
	}

	@Test
	void approveShouldPatchBookingPath() {
		server.expect(requestTo("http://localhost:9090/bookings/2?approved=true"))
				.andExpect(method(HttpMethod.PATCH))
				.andRespond(withSuccess());

		assertEquals(200, bookingClient.approve(1L, 2L, true).getStatusCode().value());
	}
}
