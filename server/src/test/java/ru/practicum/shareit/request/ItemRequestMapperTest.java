package ru.practicum.shareit.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

class ItemRequestMapperTest {
	@Test
	void toItemRequestShouldReturnNullForNullDto() {
		assertNull(ItemRequestMapper.toItemRequest(null));
	}

	@Test
	void toItemRequestDtoShouldReturnNullForNullRequest() {
		assertNull(ItemRequestMapper.toItemRequestDto(null, List.of()));
	}

	@Test
	void toItemRequestDtoShouldUseEmptyListWhenItemsNull() {
		ItemRequest request = ItemRequest.builder()
				.id(1L)
				.description("Need drill")
				.requester(User.builder().id(2L).build())
				.build();

		ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request, null);

		assertEquals(1L, dto.getId());
		assertNotNull(dto.getItems());
		assertTrue(dto.getItems().isEmpty());
	}

	@Test
	void toItemRequestAnswerDtoShouldReturnNullForNullItem() {
		assertNull(ItemRequestMapper.toItemRequestAnswerDto(null));
	}

	@Test
	void toItemRequestAnswerDtoShouldMapOwnerIdWhenPresent() {
		Item item = Item.builder()
				.id(10L)
				.name("Saw")
				.owner(User.builder().id(7L).build())
				.build();

		ItemRequestAnswerDto answer = ItemRequestMapper.toItemRequestAnswerDto(item);

		assertEquals(10L, answer.getId());
		assertEquals("Saw", answer.getName());
		assertEquals(7L, answer.getOwnerId());
	}

	@Test
	void toItemRequestAnswerDtoShouldAllowNullOwner() {
		Item item = Item.builder().id(11L).name("Hammer").owner(null).build();

		ItemRequestAnswerDto answer = ItemRequestMapper.toItemRequestAnswerDto(item);

		assertEquals(11L, answer.getId());
		assertNull(answer.getOwnerId());
	}
}
