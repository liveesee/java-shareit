package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

class UserMapperTest {
	@Test
	void toUserShouldMapDto() {
		UserDto dto = UserDto.builder().id(1L).name("Ivan").email("ivan@test.com").build();

		User user = UserMapper.toUser(dto);

		assertEquals(1L, user.getId());
		assertEquals("Ivan", user.getName());
		assertEquals("ivan@test.com", user.getEmail());
	}

	@Test
	void toUserDtoShouldHandleNull() {
		assertNull(UserMapper.toUserDto(null));
	}

	@Test
	void toUserShouldHandleNull() {
		assertNull(UserMapper.toUser(null));
	}
}
