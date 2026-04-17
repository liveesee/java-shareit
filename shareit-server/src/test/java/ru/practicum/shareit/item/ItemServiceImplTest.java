package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.Validator;
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
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private BookingRepository bookingRepository;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private ItemRequestRepository itemRequestRepository;
	@Mock
	private UserService userService;
	@Mock
	private Validator validator;

	@InjectMocks
	private ItemServiceImpl itemService;

	@Test
	void createShouldSaveItemWithRequestWhenRequestIdProvided() {
		User owner = User.builder().id(1L).name("Owner").email("owner@test.com").build();
		ItemRequest request = ItemRequest.builder().id(3L).description("Need drill").build();
		ItemCreateRequestDto createDto = ItemCreateRequestDto.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.requestId(3L)
				.build();

		when(userService.getUserOrThrow(1L)).thenReturn(owner);
		when(validator.validate(createDto)).thenReturn(Set.of());
		when(itemRequestRepository.findById(3L)).thenReturn(Optional.of(request));
		when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
			Item item = invocation.getArgument(0);
			item.setId(10L);
			return item;
		});

		ItemDto result = itemService.create(1L, createDto);

		ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
		verify(itemRepository).save(captor.capture());
		Item savedItem = captor.getValue();
		assertSame(owner, savedItem.getOwner());
		assertSame(request, savedItem.getRequest());
		assertEquals(10L, result.getId());
		assertEquals("Drill", result.getName());
	}

	@Test
	void createShouldSaveItemWithoutRequestWhenRequestIdMissing() {
		User owner = User.builder().id(1L).name("Owner").email("owner@test.com").build();
		ItemCreateRequestDto createDto = ItemCreateRequestDto.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.build();

		when(userService.getUserOrThrow(1L)).thenReturn(owner);
		when(validator.validate(createDto)).thenReturn(Set.of());
		when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ItemDto result = itemService.create(1L, createDto);

		ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
		verify(itemRepository).save(captor.capture());
		assertSame(owner, captor.getValue().getOwner());
		assertNull(captor.getValue().getRequest());
		verify(itemRequestRepository, never()).findById(any());
		assertEquals("Drill", result.getName());
	}

	@Test
	void createShouldThrowWhenRequestDoesNotExist() {
		User owner = User.builder().id(1L).name("Owner").email("owner@test.com").build();
		ItemCreateRequestDto createDto = ItemCreateRequestDto.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.requestId(99L)
				.build();

		when(userService.getUserOrThrow(1L)).thenReturn(owner);
		when(validator.validate(createDto)).thenReturn(Set.of());
		when(itemRequestRepository.findById(99L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> itemService.create(1L, createDto));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		verify(itemRepository, never()).save(any(Item.class));
	}
}
