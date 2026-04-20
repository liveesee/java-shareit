package ru.practicum.shareit.booking.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class BookingCreateRequestDtoJsonTest {
	@Autowired
	private JacksonTester<BookingCreateRequestDto> json;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldSerializeAndDeserializeBookingCreateRequestDto() throws Exception {
		LocalDateTime start = LocalDateTime.of(2030, 1, 2, 10, 0);
		LocalDateTime end = LocalDateTime.of(2030, 1, 3, 10, 0);
		BookingCreateRequestDto dto = BookingCreateRequestDto.builder()
				.itemId(1L)
				.start(start)
				.end(end)
				.build();

		assertThat(json.write(dto)).extractingJsonPathStringValue("$.start").startsWith("2030-01-02T10:00:00");
		assertThat(objectMapper.readValue(json.write(dto).getJson(), BookingCreateRequestDto.class).getEnd()).isEqualTo(end);
	}
}
