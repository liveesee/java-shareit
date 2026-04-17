package ru.practicum.shareit.gateway.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDto;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final ItemRequestClient itemRequestClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) @Positive long userId,
										 @Valid @RequestBody ItemRequestDto itemRequestDto) {
		return itemRequestClient.create(userId, itemRequestDto);
	}

	@GetMapping
	public ResponseEntity<Object> getOwnerRequests(@RequestHeader(USER_ID_HEADER) @Positive long userId) {
		return itemRequestClient.getOwnerRequests(userId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER) @Positive long userId) {
		return itemRequestClient.getAll(userId);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) @Positive long userId,
										  @PathVariable @Positive long requestId) {
		return itemRequestClient.getById(userId, requestId);
	}
}
