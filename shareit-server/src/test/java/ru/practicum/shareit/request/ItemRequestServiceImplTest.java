package ru.practicum.shareit.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
	@Mock
	private ItemRequestRepository itemRequestRepository;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private UserService userService;
	@Mock
	private Validator validator;

	@InjectMocks
	private ItemRequestServiceImpl itemRequestService;

	@Test
	void createShouldSaveRequestWithRequesterAndCreated() {
		User requester = User.builder().id(1L).name("Requester").email("req@test.com").build();
		ItemRequestDto requestDto = ItemRequestDto.builder().description("Need drill").build();

		when(validator.validate(requestDto)).thenReturn(Set.of());
		when(userService.getUserOrThrow(1L)).thenReturn(requester);
		when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
			ItemRequest itemRequest = invocation.getArgument(0);
			itemRequest.setId(5L);
			return itemRequest;
		});

		ItemRequestDto result = itemRequestService.create(1L, requestDto);

		ArgumentCaptor<ItemRequest> captor = ArgumentCaptor.forClass(ItemRequest.class);
		verify(itemRequestRepository).save(captor.capture());
		ItemRequest savedRequest = captor.getValue();
		assertEquals("Need drill", savedRequest.getDescription());
		assertEquals(requester, savedRequest.getRequester());
		assertNotNull(savedRequest.getCreated());
		assertEquals(5L, result.getId());
		assertTrue(result.getItems().isEmpty());
	}

	@Test
	void getByIdShouldReturnRequestWithAnswers() {
		User requester = User.builder().id(1L).name("Requester").email("req@test.com").build();
		ItemRequest itemRequest = ItemRequest.builder()
				.id(7L)
				.description("Need ladder")
				.requester(requester)
				.build();
		Item item = Item.builder()
				.id(11L)
				.name("Ladder")
				.owner(User.builder().id(3L).build())
				.request(itemRequest)
				.build();

		when(userService.getUserOrThrow(1L)).thenReturn(requester);
		when(itemRequestRepository.findById(7L)).thenReturn(Optional.of(itemRequest));
		when(itemRepository.findAllByRequestIdOrderByIdAsc(7L)).thenReturn(List.of(item));

		ItemRequestDto result = itemRequestService.getById(1L, 7L);

		assertEquals(7L, result.getId());
		assertEquals("Need ladder", result.getDescription());
		assertEquals(1, result.getItems().size());
		assertEquals(11L, result.getItems().getFirst().getId());
		assertEquals(3L, result.getItems().getFirst().getOwnerId());
	}

	@Test
	void getByIdShouldThrowWhenRequestMissing() {
		when(userService.getUserOrThrow(1L)).thenReturn(User.builder().id(1L).build());
		when(itemRequestRepository.findById(99L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> itemRequestService.getById(1L, 99L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void getAllShouldReturnRequestsWithItemsForOtherUsers() {
		User requester = User.builder().id(2L).name("Requester").email("req@test.com").build();
		ItemRequest first = ItemRequest.builder().id(10L).description("Need saw").requester(requester).build();
		ItemRequest second = ItemRequest.builder().id(20L).description("Need hammer").requester(requester).build();
		Item firstItem = Item.builder()
				.id(100L)
				.name("Saw")
				.owner(User.builder().id(7L).build())
				.request(first)
				.build();

		when(userService.getUserOrThrow(1L)).thenReturn(User.builder().id(1L).build());
		when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(1L)).thenReturn(List.of(first, second));
		when(itemRepository.findAllByRequestIdInOrderByRequestIdAscIdAsc(List.of(10L, 20L)))
				.thenReturn(List.of(firstItem));

		List<ItemRequestDto> result = itemRequestService.getAll(1L);

		assertEquals(2, result.size());
		assertEquals(10L, result.getFirst().getId());
		assertEquals(1, result.getFirst().getItems().size());
		assertEquals(20L, result.get(1).getId());
		assertTrue(result.get(1).getItems().isEmpty());
	}
}
