package ru.practicum.shareit.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Test
	void createShouldPersistUser() {
		UserDto result = userService.create(UserDto.builder().name("Ivan").email("ivan@test.com").build());

		assertThat(result.getId()).isNotNull();
		assertThat(userRepository.findById(result.getId())).isPresent();
	}

	@Test
	void updateShouldPersistChanges() {
		UserDto created = userService.create(UserDto.builder().name("Ivan").email("ivan@test.com").build());

		UserDto result = userService.update(created.getId(), UserDto.builder().name("New").email("new@test.com").build());

		assertThat(result.getName()).isEqualTo("New");
		assertThat(userRepository.findById(created.getId())).get().extracting("email").isEqualTo("new@test.com");
	}

	@Test
	void getByIdShouldReadPersistedUser() {
		UserDto created = userService.create(UserDto.builder().name("Ivan").email("ivan@test.com").build());

		UserDto result = userService.getById(created.getId());

		assertThat(result.getEmail()).isEqualTo("ivan@test.com");
	}

	@Test
	void getAllShouldReturnPersistedUsers() {
		userService.create(UserDto.builder().name("A").email("a@test.com").build());
		userService.create(UserDto.builder().name("B").email("b@test.com").build());

		List<UserDto> result = userService.getAll();

		assertThat(result).hasSize(2);
	}

	@Test
	void deleteShouldRemoveUser() {
		UserDto created = userService.create(UserDto.builder().name("Ivan").email("ivan@test.com").build());

		userService.delete(created.getId());

		assertThat(userRepository.findById(created.getId())).isEmpty();
	}

	@Test
	void getUserOrThrowShouldFailForMissingUser() {
		assertThrows(ResponseStatusException.class, () -> userService.getUserOrThrow(999L));
	}
}
