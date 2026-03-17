package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {
	ItemDto create(long ownerId, ItemDto itemDto);

	ItemDto update(long ownerId, long itemId, ItemDto itemDto);

	ItemDto getById(long userId, long itemId);

	List<ItemDto> getAllByOwner(long ownerId);

	List<ItemDto> search(long userId, String text);
}

