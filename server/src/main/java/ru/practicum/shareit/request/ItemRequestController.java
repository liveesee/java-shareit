package ru.practicum.shareit.request;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";
	private final ItemRequestService itemRequestService;

	@PostMapping
	public ItemRequestDto create(@RequestHeader(USER_ID_HEADER) long userId,
								 @RequestBody ItemRequestDto itemRequestDto) {
		return itemRequestService.create(userId, itemRequestDto);
	}

	@GetMapping
	public List<ItemRequestDto> getOwnerRequests(@RequestHeader(USER_ID_HEADER) long userId) {
		return itemRequestService.getOwnerRequests(userId);
	}

	@GetMapping("/all")
	public List<ItemRequestDto> getAll(@RequestHeader(USER_ID_HEADER) long userId) {
		return itemRequestService.getAll(userId);
	}

	@GetMapping("/{requestId}")
	public ItemRequestDto getById(@RequestHeader(USER_ID_HEADER) long userId,
								  @PathVariable long requestId) {
		return itemRequestService.getById(userId, requestId);
	}
}
