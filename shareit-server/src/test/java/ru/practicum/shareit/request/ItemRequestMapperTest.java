package ru.practicum.shareit.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

class ItemRequestMapperTest {
	@Test
	void toItemRequestShouldMapDto() {
		ItemRequestDto dto = ItemRequestDto.builder().description("Need drill").build();

		ItemRequest request = ItemRequestMapper.toItemRequest(dto);

		assertEquals("Need drill", request.getDescription());
	}

	@Test
	void toItemRequestDtoShouldMapEntityAndItems() {
		ItemRequest request = ItemRequest.builder()
				.id(1L)
				.description("Need drill")
				.requester(User.builder().id(2L).build())
				.created(LocalDateTime.now())
				.build();
		List<ItemRequestAnswerDto> items = List.of(ItemRequestAnswerDto.builder().id(3L).name("Drill").ownerId(4L).build());

		ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request, items);

		assertEquals(1L, dto.getId());
		assertEquals(2L, dto.getRequestorId());
		assertEquals(1, dto.getItems().size());
	}

	@Test
	void toItemRequestAnswerDtoShouldMapItem() {
		Item item = Item.builder()
				.id(5L)
				.name("Hammer")
				.owner(User.builder().id(6L).build())
				.build();

		ItemRequestAnswerDto dto = ItemRequestMapper.toItemRequestAnswerDto(item);

		assertEquals(5L, dto.getId());
		assertEquals(6L, dto.getOwnerId());
	}

	@Test
	void toItemRequestShouldReturnNullForNullInput() {
		assertNull(ItemRequestMapper.toItemRequest(null));
	}
}
