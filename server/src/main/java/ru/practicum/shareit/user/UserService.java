package ru.practicum.shareit.user;

import java.util.List;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {
	UserDto create(UserDto userDto);

	UserDto update(long userId, UserDto userDto);

	UserDto getById(long userId);

	List<UserDto> getAll();

	void delete(long userId);

	User getUserOrThrow(long userId);
}
