package ru.practicum.shareit.item;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final ItemService itemService;

	@PostMapping
	public ItemDto create(@RequestHeader(USER_ID_HEADER) long userId,
						  @RequestBody ItemCreateRequestDto itemDto) {
		return itemService.create(userId, itemDto);
	}

	@PatchMapping("/{itemId}")
	public ItemDto update(@RequestHeader(USER_ID_HEADER) long userId,
						  @PathVariable long itemId,
						  @RequestBody ItemUpdateRequestDto itemDto) {
		return itemService.update(userId, itemId, itemDto);
	}

	@PostMapping("/{itemId}/comment")
	public CommentDto addComment(@RequestHeader(USER_ID_HEADER) long userId,
								 @PathVariable long itemId,
								 @RequestBody CommentCreateRequestDto commentDto) {
		return itemService.addComment(userId, itemId, commentDto);
	}

	@GetMapping("/{itemId}")
	public ItemDto getById(@RequestHeader(USER_ID_HEADER) long userId,
						   @PathVariable long itemId) {
		return itemService.getById(userId, itemId);
	}

	@GetMapping
	public List<OwnerItemDto> getAllByOwner(@RequestHeader(USER_ID_HEADER) long userId) {
		return itemService.getAllByOwner(userId);
	}

	@GetMapping("/search")
	public List<ItemDto> search(@RequestHeader(USER_ID_HEADER) long userId,
								@RequestParam(name = "text") String text) {
		return itemService.search(userId, text);
	}
}
