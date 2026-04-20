package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

class ItemMapperTest {
	@Test
	void toItemShouldMapCreateDto() {
		ItemCreateRequestDto dto = ItemCreateRequestDto.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.requestId(1L)
				.build();

		Item item = ItemMapper.toItem(dto);

		assertEquals("Drill", item.getName());
		assertEquals("Cordless", item.getDescription());
		assertTrue(item.getAvailable());
	}

	@Test
	void updateItemShouldApplyNonNullFields() {
		Item item = Item.builder().name("Old").description("Desc").available(true).build();
		ItemUpdateRequestDto updateDto = ItemUpdateRequestDto.builder().name("New").available(false).build();

		ItemMapper.updateItem(item, updateDto);

		assertEquals("New", item.getName());
		assertEquals("Desc", item.getDescription());
		assertEquals(false, item.getAvailable());
	}

	@Test
	void toItemDtoShouldMapEntity() {
		Item item = Item.builder()
				.id(1L)
				.name("Drill")
				.description("Cordless")
				.available(true)
				.owner(User.builder().id(2L).name("Owner").email("owner@test.com").build())
				.build();

		ItemDto dto = ItemMapper.toItemDto(item);

		assertEquals(1L, dto.getId());
		assertEquals(2L, dto.getOwner().getId());
		assertTrue(dto.getComments().isEmpty());
	}

	@Test
	void toOwnerItemDtoShouldMapEntity() {
		Item item = Item.builder().id(1L).name("Drill").description("Cordless").available(true).build();

		OwnerItemDto dto = ItemMapper.toOwnerItemDto(item);

		assertEquals(1L, dto.getId());
		assertEquals("Drill", dto.getName());
	}

	@Test
	void toCommentShouldMapDto() {
		Comment comment = ItemMapper.toComment(CommentCreateRequestDto.builder().text("Nice").build());

		assertEquals("Nice", comment.getText());
	}

	@Test
	void toCommentDtoShouldMapEntity() {
		Comment comment = Comment.builder()
				.id(1L)
				.text("Nice")
				.author(User.builder().name("Ivan").build())
				.created(LocalDateTime.now())
				.build();

		CommentDto dto = ItemMapper.toCommentDto(comment);

		assertEquals(1L, dto.getId());
		assertEquals("Ivan", dto.getAuthorName());
	}

	@Test
	void toItemShouldReturnNullForNullInput() {
		assertNull(ItemMapper.toItem(null));
	}

	@Test
	void updateItemShouldDoNothingWhenItemNull() {
		ItemMapper.updateItem(null, ItemUpdateRequestDto.builder().name("X").build());
	}

	@Test
	void updateItemShouldDoNothingWhenDtoNull() {
		Item item = Item.builder().name("Old").build();
		ItemMapper.updateItem(item, null);
		assertEquals("Old", item.getName());
	}

	@Test
	void toItemDtoShouldReturnNullForNullItem() {
		assertNull(ItemMapper.toItemDto(null));
	}

	@Test
	void toOwnerItemDtoShouldReturnNullForNullItem() {
		assertNull(ItemMapper.toOwnerItemDto(null));
	}

	@Test
	void toCommentShouldReturnNullForNullDto() {
		assertNull(ItemMapper.toComment(null));
	}

	@Test
	void toCommentDtoShouldReturnNullForNullComment() {
		assertNull(ItemMapper.toCommentDto(null));
	}

	@Test
	void toCommentDtoShouldMapWhenAuthorNull() {
		Comment comment = Comment.builder().id(1L).text("Hi").author(null).created(LocalDateTime.now()).build();

		CommentDto dto = ItemMapper.toCommentDto(comment);

		assertEquals(1L, dto.getId());
		assertNull(dto.getAuthorName());
	}
}
