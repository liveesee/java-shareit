package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
	@Mock
	private ItemService itemService;

	@InjectMocks
	private ItemController itemController;

	@Test
	void createShouldDelegateToService() {
		ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder().name("Drill").build();
		ItemDto result = ItemDto.builder().id(1L).build();
		when(itemService.create(1L, requestDto)).thenReturn(result);

		assertEquals(result, itemController.create(1L, requestDto));
	}

	@Test
	void updateShouldDelegateToService() {
		ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder().name("Drill").build();
		ItemDto result = ItemDto.builder().id(1L).build();
		when(itemService.update(1L, 2L, requestDto)).thenReturn(result);

		assertEquals(result, itemController.update(1L, 2L, requestDto));
	}

	@Test
	void addCommentShouldDelegateToService() {
		CommentCreateRequestDto requestDto = CommentCreateRequestDto.builder().text("Nice").build();
		CommentDto result = CommentDto.builder().id(1L).build();
		when(itemService.addComment(1L, 2L, requestDto)).thenReturn(result);

		assertEquals(result, itemController.addComment(1L, 2L, requestDto));
	}

	@Test
	void getByIdShouldDelegateToService() {
		ItemDto result = ItemDto.builder().id(1L).build();
		when(itemService.getById(1L, 2L)).thenReturn(result);

		assertEquals(result, itemController.getById(1L, 2L));
	}

	@Test
	void getAllByOwnerShouldDelegateToService() {
		List<OwnerItemDto> result = List.of(OwnerItemDto.builder().id(1L).build());
		when(itemService.getAllByOwner(1L)).thenReturn(result);

		assertEquals(result, itemController.getAllByOwner(1L));
	}

	@Test
	void searchShouldDelegateToService() {
		List<ItemDto> result = List.of(ItemDto.builder().id(1L).build());
		when(itemService.search(1L, "drill")).thenReturn(result);

		assertEquals(result, itemController.search(1L, "drill"));
	}
}
