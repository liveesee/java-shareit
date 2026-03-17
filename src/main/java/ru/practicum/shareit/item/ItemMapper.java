package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public final class ItemMapper {
	private ItemMapper() {
	}

	public static Item toItem(ItemDto itemDto) {
		if (itemDto == null) {
			return null;
		}
		return Item.builder()
				.id(itemDto.getId())
				.name(itemDto.getName())
				.description(itemDto.getDescription())
				.available(itemDto.getAvailable())
				.owner(itemDto.getOwner())
				.build();
	}

	public static ItemDto toItemDto(Item item) {
		if (item == null) {
			return null;
		}
		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.owner(item.getOwner())
				.build();
	}
}
