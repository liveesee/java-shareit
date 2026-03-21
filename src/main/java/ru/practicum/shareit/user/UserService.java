package ru.practicum.shareit.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
public class UserService {
	private final Map<Long, User> users = new HashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);

	public UserDto create(UserDto userDto) {
		if (isEmailTaken(userDto.getEmail(), null)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
		}

		long id = nextId.getAndIncrement();
		User user = User.builder()
				.id(id)
				.name(userDto.getName())
				.email(userDto.getEmail())
				.build();
		users.put(id, user);
		return UserMapper.toUserDto(user);
	}

	public UserDto update(long userId, UserDto userDto) {
		User existing = getExisting(userId);
		if (userDto == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is null");
		}

		if (userDto.getName() != null) {
			if (!StringUtils.hasText(userDto.getName())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User name is blank");
			}
			existing.setName(userDto.getName());
		}

		if (userDto.getEmail() != null) {
			if (!StringUtils.hasText(userDto.getEmail())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User email is blank");
			}
			if (isEmailTaken(userDto.getEmail(), userId)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
			}
			existing.setEmail(userDto.getEmail());
		}

		users.put(userId, existing);
		return UserMapper.toUserDto(existing);
	}

	public UserDto getById(long userId) {
		return UserMapper.toUserDto(getExisting(userId));
	}

	public List<UserDto> getAll() {
		List<UserDto> result = new ArrayList<>();
		users.values().stream()
				.sorted(Comparator.comparing(User::getId))
				.map(UserMapper::toUserDto)
				.forEach(result::add);
		return result;
	}

	public void delete(long userId) {
		getExisting(userId);
		users.remove(userId);
	}

	public User getUserOrThrow(long userId) {
		return getExisting(userId);
	}

	private User getExisting(long userId) {
		User user = users.get(userId);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
		return user;
	}

	private boolean isEmailTaken(String email, Long userIdToIgnore) {
		Optional<Long> ownerId = users.values().stream()
				.filter(u -> Objects.equals(u.getEmail(), email))
				.map(User::getId)
				.findFirst();

		if (ownerId.isEmpty()) {
			return false;
		}
		return userIdToIgnore == null || !Objects.equals(ownerId.get(), userIdToIgnore);
	}
}
