package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
				.owner(toOwnerCopy(item.getOwner()))
				.lastBooking(null)
				.nextBooking(null)
				.comments(List.of())
				.build();
	}

	public static OwnerItemDto toOwnerItemDto(Item item) {
		if (item == null) {
			return null;
		}
		return OwnerItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.owner(toOwnerCopy(item.getOwner()))
				.comments(List.of())
				.build();
	}

	public static Comment toComment(CommentCreateRequestDto commentDto) {
		if (commentDto == null) {
			return null;
		}
		return Comment.builder()
				.text(commentDto.getText())
				.build();
	}

	public static CommentDto toCommentDto(Comment comment) {
		if (comment == null) {
			return null;
		}
		return CommentDto.builder()
				.id(comment.getId())
				.text(comment.getText())
				.authorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null)
				.created(comment.getCreated())
				.build();
	}

	private static User toOwnerCopy(User owner) {
		if (owner == null) {
			return null;
		}
		return User.builder()
				.id(owner.getId())
				.name(owner.getName())
				.email(owner.getEmail())
				.build();
	}
}
