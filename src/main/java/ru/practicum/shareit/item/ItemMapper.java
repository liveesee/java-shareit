package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.Item;

public final class ItemMapper {
	private ItemMapper() {
	}

	public static Item toItem(ItemCreateRequestDto itemDto) {
		if (itemDto == null) {
			return null;
		}
		return Item.builder()
				.name(itemDto.getName())
				.description(itemDto.getDescription())
				.available(itemDto.getAvailable())
				.build();
	}

	public static void updateItem(Item item, ItemUpdateRequestDto itemDto) {
		if (item == null || itemDto == null) {
			return;
		}

		if (itemDto.getName() != null) {
			item.setName(itemDto.getName());
		}

		if (itemDto.getDescription() != null) {
			item.setDescription(itemDto.getDescription());
		}

		if (itemDto.getAvailable() != null) {
			item.setAvailable(itemDto.getAvailable());
		}
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
