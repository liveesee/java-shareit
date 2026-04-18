package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private Validator validator;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void createShouldSaveUser() {
		UserDto userDto = UserDto.builder().name("Ivan").email("ivan@test.com").build();
		when(validator.validate(userDto)).thenReturn(Set.of());
		when(userRepository.existsByEmail("ivan@test.com")).thenReturn(false);
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User user = invocation.getArgument(0);
			user.setId(1L);
			return user;
		});

		UserDto result = userService.create(userDto);

		assertEquals(1L, result.getId());
		assertEquals("Ivan", result.getName());
	}

	@Test
	void updateShouldChangeNameAndEmail() {
		User existing = User.builder().id(1L).name("Old").email("old@test.com").build();
		UserDto updateDto = UserDto.builder().name("New").email("new@test.com").build();
		when(validator.validate(any(UserDto.class))).thenReturn(Set.of());
		when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(userRepository.existsByEmailAndIdNot("new@test.com", 1L)).thenReturn(false);
		when(userRepository.save(existing)).thenReturn(existing);

		UserDto result = userService.update(1L, updateDto);

		assertEquals("New", result.getName());
		assertEquals("new@test.com", result.getEmail());
	}

	@Test
	void getByIdShouldReturnUser() {
		User existing = User.builder().id(1L).name("Ivan").email("ivan@test.com").build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

		UserDto result = userService.getById(1L);

		assertEquals(1L, result.getId());
		assertEquals("Ivan", result.getName());
	}

	@Test
	void getAllShouldReturnOrderedUsers() {
		when(userRepository.findAllByOrderByIdAsc()).thenReturn(List.of(
				User.builder().id(1L).name("A").email("a@test.com").build(),
				User.builder().id(2L).name("B").email("b@test.com").build()
		));

		List<UserDto> result = userService.getAll();

		assertEquals(2, result.size());
		assertEquals(1L, result.getFirst().getId());
		assertEquals(2L, result.get(1).getId());
	}

	@Test
	void deleteShouldRemoveExistingUser() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));

		userService.delete(1L);

		verify(userRepository).deleteById(1L);
	}

	@Test
	void getUserOrThrowShouldReturnUser() {
		User existing = User.builder().id(1L).build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

		User result = userService.getUserOrThrow(1L);

		assertSame(existing, result);
	}

	@Test
	void createShouldThrowConflictWhenEmailExists() {
		when(userRepository.existsByEmail("ivan@test.com")).thenReturn(true);
		when(validator.validate(org.mockito.ArgumentMatchers.any(UserDto.class))).thenReturn(Set.of());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.create(UserDto.builder().name("Ivan").email("ivan@test.com").build()));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void updateShouldThrowWhenDtoNull() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).name("Old").email("old@test.com").build()));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.update(1L, null));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void updateShouldThrowWhenNameBlank() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).name("Old").email("old@test.com").build()));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.update(1L, UserDto.builder().name("   ").build()));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}

	@Test
	void updateShouldThrowWhenEmailBlank() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).name("Old").email("old@test.com").build()));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.update(1L, UserDto.builder().email(" ").build()));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}

	@Test
	void updateShouldThrowConflictWhenEmailTaken() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).name("Old").email("old@test.com").build()));
		when(validator.validate(any(UserDto.class))).thenReturn(Set.of());
		when(userRepository.existsByEmailAndIdNot("taken@test.com", 1L)).thenReturn(true);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.update(1L, UserDto.builder().email("taken@test.com").build()));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
	}

	@Test
	void getByIdShouldThrowWhenMissing() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.getById(1L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void getUserOrThrowShouldThrowWhenMissing() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.getUserOrThrow(1L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void deleteShouldThrowWhenMissing() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.delete(1L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		verify(userRepository, never()).deleteById(any());
	}

	@Test
	@SuppressWarnings("unchecked")
	void createShouldThrowWhenValidatorReportsViolations() {
		ConstraintViolation<UserDto> violation = mock(ConstraintViolation.class);
		when(violation.getMessage()).thenReturn("bad field");
		when(validator.validate(any(UserDto.class))).thenReturn(Set.of(violation));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.create(UserDto.builder().name("Ivan").email("ivan@test.com").build()));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(userRepository, never()).save(any(User.class));
	}
}
