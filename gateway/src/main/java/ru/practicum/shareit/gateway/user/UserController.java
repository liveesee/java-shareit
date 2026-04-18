package ru.practicum.shareit.gateway.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.gateway.user.dto.UserDto;

@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
	private final UserClient userClient;

	@PostMapping
	public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
		return userClient.create(userDto);
	}

	@PatchMapping("/{userId}")
	public ResponseEntity<Object> update(@PathVariable @Positive long userId,
										 @RequestBody UserDto userDto) {
		return userClient.update(userId, userDto);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<Object> getById(@PathVariable @Positive long userId) {
		return userClient.getById(userId);
	}

	@GetMapping
	public ResponseEntity<Object> getAll() {
		return userClient.getAll();
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Object> delete(@PathVariable @Positive long userId) {
		return userClient.delete(userId);
	}
}
