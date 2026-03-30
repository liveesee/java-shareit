package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

public interface ItemService {
	ItemDto create(long ownerId, ItemCreateRequestDto itemDto);

	ItemDto update(long ownerId, long itemId, ItemUpdateRequestDto itemDto);

	ItemDto getById(long userId, long itemId);

	List<ItemDto> getAllByOwner(long ownerId);

	List<ItemDto> search(long userId, String text);
}
