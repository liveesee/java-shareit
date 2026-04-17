package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;

public interface ItemService {
	ItemDto create(long ownerId, ItemCreateRequestDto itemDto);

	ItemDto update(long ownerId, long itemId, ItemUpdateRequestDto itemDto);

	ItemDto getById(long userId, long itemId);

	List<OwnerItemDto> getAllByOwner(long ownerId);

	CommentDto addComment(long userId, long itemId, CommentCreateRequestDto commentDto);

	List<ItemDto> search(long userId, String text);
}
