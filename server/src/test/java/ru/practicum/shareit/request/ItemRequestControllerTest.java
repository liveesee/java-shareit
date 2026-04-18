package ru.practicum.shareit.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
	@Mock
	private ItemRequestService itemRequestService;

	@InjectMocks
	private ItemRequestController itemRequestController;

	@Test
	void createShouldDelegateToService() {
		ItemRequestDto requestDto = ItemRequestDto.builder().description("Need drill").build();
		when(itemRequestService.create(1L, requestDto)).thenReturn(requestDto);

		assertEquals(requestDto, itemRequestController.create(1L, requestDto));
	}

	@Test
	void getOwnerRequestsShouldDelegateToService() {
		List<ItemRequestDto> result = List.of(ItemRequestDto.builder().id(1L).build());
		when(itemRequestService.getOwnerRequests(1L)).thenReturn(result);

		assertEquals(result, itemRequestController.getOwnerRequests(1L));
	}

	@Test
	void getAllShouldDelegateToService() {
		List<ItemRequestDto> result = List.of(ItemRequestDto.builder().id(1L).build());
		when(itemRequestService.getAll(1L)).thenReturn(result);

		assertEquals(result, itemRequestController.getAll(1L));
	}

	@Test
	void getByIdShouldDelegateToService() {
		ItemRequestDto result = ItemRequestDto.builder().id(1L).build();
		when(itemRequestService.getById(1L, 2L)).thenReturn(result);

		assertEquals(result, itemRequestController.getById(1L, 2L));
	}
}
