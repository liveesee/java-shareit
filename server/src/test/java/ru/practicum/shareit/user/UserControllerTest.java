package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	@Test
	void createShouldDelegateToService() {
		UserDto userDto = UserDto.builder().id(1L).name("Ivan").email("ivan@test.com").build();
		when(userService.create(userDto)).thenReturn(userDto);

		assertEquals(userDto, userController.create(userDto));
	}

	@Test
	void updateShouldDelegateToService() {
		UserDto userDto = UserDto.builder().name("Ivan").email("ivan@test.com").build();
		when(userService.update(1L, userDto)).thenReturn(userDto);

		assertEquals(userDto, userController.update(1L, userDto));
	}

	@Test
	void getByIdShouldDelegateToService() {
		UserDto userDto = UserDto.builder().id(1L).build();
		when(userService.getById(1L)).thenReturn(userDto);

		assertEquals(userDto, userController.getById(1L));
	}

	@Test
	void getAllShouldDelegateToService() {
		List<UserDto> users = List.of(UserDto.builder().id(1L).build());
		when(userService.getAll()).thenReturn(users);

		assertEquals(users, userController.getAll());
	}

	@Test
	void deleteShouldDelegateToService() {
		userController.delete(1L);

		verify(userService).delete(1L);
	}
}
