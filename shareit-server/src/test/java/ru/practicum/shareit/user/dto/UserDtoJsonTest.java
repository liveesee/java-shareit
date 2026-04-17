package ru.practicum.shareit.user.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@JsonTest
class UserDtoJsonTest {
	@Autowired
	private JacksonTester<UserDto> json;

	@Autowired
	private ObjectMapper objectMapper;

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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

	@Test
	void shouldValidateEmailAndName() {
		UserDto dto = UserDto.builder()
				.name(" ")
				.email("bad")
				.build();

		Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

		assertThat(violations).hasSize(2);
	}
}
