package ru.practicum.shareit.gateway.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.gateway.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.gateway.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateRequestDto;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final ItemClient itemClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) @Positive long userId,
										 @Valid @RequestBody ItemCreateRequestDto itemDto) {
		return itemClient.create(userId, itemDto);
	}

	@PatchMapping("/{itemId}")
	public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER) @Positive long userId,
										 @PathVariable @Positive long itemId,
										 @RequestBody ItemUpdateRequestDto itemDto) {
		return itemClient.update(userId, itemId, itemDto);
	}

	@PostMapping("/{itemId}/comment")
	public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) @Positive long userId,
											 @PathVariable @Positive long itemId,
											 @Valid @RequestBody CommentCreateRequestDto commentDto) {
		return itemClient.addComment(userId, itemId, commentDto);
	}

	@GetMapping("/{itemId}")
	public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) @Positive long userId,
										  @PathVariable @Positive long itemId) {
		return itemClient.getById(userId, itemId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_ID_HEADER) @Positive long userId) {
		return itemClient.getAllByOwner(userId);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> search(@RequestHeader(USER_ID_HEADER) @Positive long userId,
										 @RequestParam(name = "text") String text) {
		return itemClient.search(userId, text);
	}
}
