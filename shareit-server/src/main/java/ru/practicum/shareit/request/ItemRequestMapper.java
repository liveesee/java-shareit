package ru.practicum.shareit.request;

import java.util.List;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public final class ItemRequestMapper {
	private ItemRequestMapper() {
	}

	public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
		if (itemRequestDto == null) {
			return null;
		}
		return ItemRequest.builder()
				.description(itemRequestDto.getDescription())
				.build();
	}

	public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemRequestAnswerDto> items) {
		if (itemRequest == null) {
			return null;
		}
		return ItemRequestDto.builder()
				.id(itemRequest.getId())
				.description(itemRequest.getDescription())
				.requestorId(itemRequest.getRequester() != null ? itemRequest.getRequester().getId() : null)
				.created(itemRequest.getCreated())
				.items(items != null ? items : List.of())
				.build();
	}

	public static ItemRequestAnswerDto toItemRequestAnswerDto(Item item) {
		if (item == null) {
			return null;
		}
		return ItemRequestAnswerDto.builder()
				.id(item.getId())
				.name(item.getName())
				.ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
				.build();
	}
}
