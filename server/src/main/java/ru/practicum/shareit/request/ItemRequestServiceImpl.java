package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
	private final ItemRequestRepository itemRequestRepository;
	private final ItemRepository itemRepository;
	private final UserService userService;

	@Override
	public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
		validateItemRequestDto(itemRequestDto);
		ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
		itemRequest.setRequester(userService.getUserOrThrow(userId));
		itemRequest.setCreated(LocalDateTime.now());
		return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), List.of());
	}

	@Override
	@Transactional(readOnly = true)
	public ItemRequestDto getById(long userId, long requestId) {
		userService.getUserOrThrow(userId);
		ItemRequest itemRequest = getExisting(requestId);
		List<ItemRequestAnswerDto> items = itemRepository.findAllByRequestIdOrderByIdAsc(requestId).stream()
				.map(ItemRequestMapper::toItemRequestAnswerDto)
				.toList();
		return ItemRequestMapper.toItemRequestDto(itemRequest, items);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemRequestDto> getAll(long userId) {
		userService.getUserOrThrow(userId);
		List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
		return toItemRequestDtos(requests);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemRequestDto> getOwnerRequests(long userId) {
		userService.getUserOrThrow(userId);
		List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
		return toItemRequestDtos(requests);
	}

	private List<ItemRequestDto> toItemRequestDtos(List<ItemRequest> requests) {
		Map<Long, List<ItemRequestAnswerDto>> itemsByRequestId = getItemsByRequestId(requests);
		return requests.stream()
				.map(request -> ItemRequestMapper.toItemRequestDto(
						request,
						itemsByRequestId.getOrDefault(request.getId(), List.of())))
				.toList();
	}

	private Map<Long, List<ItemRequestAnswerDto>> getItemsByRequestId(List<ItemRequest> requests) {
		List<Long> requestIds = requests.stream()
				.map(ItemRequest::getId)
				.toList();
		if (requestIds.isEmpty()) {
			return Collections.emptyMap();
		}
		return itemRepository.findAllByRequestIdInOrderByRequestIdAscIdAsc(requestIds).stream()
				.collect(Collectors.groupingBy(
						item -> item.getRequest().getId(),
						Collectors.mapping(ItemRequestMapper::toItemRequestAnswerDto, Collectors.toList())
				));
	}

	private ItemRequest getExisting(long requestId) {
		return itemRequestRepository.findById(requestId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
	}

	private void validateItemRequestDto(ItemRequestDto itemRequestDto) {
		if (itemRequestDto == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is null");
		}
	}
}
