package ru.practicum.shareit.request.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@JsonTest
class ItemRequestDtoJsonTest {
	@Autowired
	private JacksonTester<ItemRequestDto> json;

	@Autowired
	private ObjectMapper objectMapper;

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void shouldSerializeAndDeserializeItemRequestDto() throws Exception {
		ItemRequestDto dto = ItemRequestDto.builder()
				.id(1L)
				.description("Need drill")
				.requestorId(2L)
				.created(LocalDateTime.of(2030, 1, 2, 10, 0))
				.items(List.of(ItemRequestAnswerDto.builder().id(3L).name("Drill").ownerId(4L).build()))
				.build();

		assertThat(json.write(dto)).extractingJsonPathStringValue("$.description").isEqualTo("Need drill");
		assertThat(objectMapper.readValue(json.write(dto).getJson(), ItemRequestDto.class).getItems()).hasSize(1);
	}

	@Test
	void shouldValidateDescription() {
		ItemRequestDto dto = ItemRequestDto.builder().description(" ").build();

		Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

		assertThat(violations).hasSize(1);
	}
}
