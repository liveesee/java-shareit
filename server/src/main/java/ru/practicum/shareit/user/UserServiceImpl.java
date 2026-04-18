package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final Validator validator;

	@Override
	public UserDto create(UserDto userDto) {
		validateUserDto(userDto);
		if (userRepository.existsByEmail(userDto.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
		}

		User user = UserMapper.toUser(userDto);
		return UserMapper.toUserDto(userRepository.save(user));
	}

	@Override
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
			validateUserDto(UserDto.builder().name(existing.getName()).email(userDto.getEmail()).build());
			if (userRepository.existsByEmailAndIdNot(userDto.getEmail(), userId)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
			}
			existing.setEmail(userDto.getEmail());
		}

		return UserMapper.toUserDto(userRepository.save(existing));
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getById(long userId) {
		return UserMapper.toUserDto(getExisting(userId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> getAll() {
		return userRepository.findAllByOrderByIdAsc().stream()
				.map(UserMapper::toUserDto)
				.toList();
	}

	@Override
	public void delete(long userId) {
		getExisting(userId);
		userRepository.deleteById(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserOrThrow(long userId) {
		return getExisting(userId);
	}

	@Transactional(readOnly = true)
	private User getExisting(long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}

	private void validateUserDto(UserDto userDto) {
		if (userDto == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is null");
		}

		Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
		if (!violations.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, violations.iterator().next().getMessage());
		}
	}
}
