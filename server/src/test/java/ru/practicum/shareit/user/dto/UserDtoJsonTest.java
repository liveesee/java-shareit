package ru.practicum.shareit.user.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class UserDtoJsonTest {
	@Autowired
	private JacksonTester<UserDto> json;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldSerializeAndDeserializeUserDto() throws Exception {
		UserDto dto = UserDto.builder()
				.id(1L)
				.name("Ivan")
				.email("ivan@test.com")
				.build();

		assertThat(json.write(dto)).extractingJsonPathStringValue("$.name").isEqualTo("Ivan");
		assertThat(objectMapper.readValue(json.write(dto).getJson(), UserDto.class).getEmail()).isEqualTo("ivan@test.com");
	}
}
