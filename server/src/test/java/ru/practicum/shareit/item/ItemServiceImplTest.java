package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.Validator;
import java.time.LocalDateTime;
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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
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

	@Test
	void updateShouldThrowWhenDtoNull() {
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> itemService.update(1L, 1L, null));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(itemRepository, never()).save(any(Item.class));
	}

	@Test
	void updateShouldThrowWhenCallerIsNotOwner() {
		Item existing = Item.builder().id(1L).owner(User.builder().id(2L).build()).build();
		when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> itemService.update(99L, 1L, ItemUpdateRequestDto.builder().name("New").build()));

		assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
		verify(itemRepository, never()).save(any(Item.class));
	}

	@Test
	void updateShouldThrowWhenNameBlank() {
		Item existing = Item.builder().id(1L).owner(User.builder().id(1L).build()).name("Old").build();
		when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> itemService.update(1L, 1L, ItemUpdateRequestDto.builder().name("  ").build()));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}

	@Test
	void updateShouldThrowWhenDescriptionBlank() {
		Item existing = Item.builder().id(1L).owner(User.builder().id(1L).build()).name("Old").build();
		when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> itemService.update(1L, 1L, ItemUpdateRequestDto.builder().description(" ").build()));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}

	@Test
	void getByIdShouldThrowWhenItemMissing() {
		when(userService.getUserOrThrow(1L)).thenReturn(User.builder().id(1L).build());
		when(itemRepository.findById(2L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> itemService.getById(1L, 2L));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void searchShouldReturnEmptyListForBlankText() {
		when(userService.getUserOrThrow(1L)).thenReturn(User.builder().id(1L).build());

		List<ItemDto> result = itemService.search(1L, "   ");

		assertTrue(result.isEmpty());
		verify(itemRepository, never()).searchAvailableByText(any());
	}

	@Test
	void searchShouldMapRepositoryResults() {
		when(userService.getUserOrThrow(1L)).thenReturn(User.builder().id(1L).build());
		Item item = Item.builder().id(3L).name("Drill").description("Cordless").available(true).build();
		when(itemRepository.searchAvailableByText("drill")).thenReturn(List.of(item));

		List<ItemDto> result = itemService.search(1L, "drill");

		assertEquals(1, result.size());
		assertEquals(3L, result.getFirst().getId());
	}

	@Test
	void getByIdShouldEnrichBookingsForOwner() {
		User owner = User.builder().id(1L).build();
		Item item = Item.builder().id(10L).name("Drill").owner(owner).build();
		when(userService.getUserOrThrow(1L)).thenReturn(owner);
		when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
		LocalDateTime now = LocalDateTime.now();
		when(bookingRepository.findAllByItemIdOrderByStartAsc(10L)).thenReturn(List.of(
				Booking.builder().id(1L).start(now.minusDays(1)).build(),
				Booking.builder().id(2L).start(now.plusDays(1)).build()
		));
		when(commentRepository.findAllByItemIdOrderByCreatedAsc(10L)).thenReturn(List.of());

		ItemDto dto = itemService.getById(1L, 10L);

		assertNotNull(dto.getLastBooking());
		assertNotNull(dto.getNextBooking());
	}

	@Test
	void getAllByOwnerShouldReturnEmptyWhenNoItems() {
		when(userService.getUserOrThrow(1L)).thenReturn(User.builder().id(1L).build());
		when(itemRepository.findAllByOwnerIdOrderByIdAsc(1L)).thenReturn(List.of());

		List<OwnerItemDto> result = itemService.getAllByOwner(1L);

		assertTrue(result.isEmpty());
	}

	@Test
	void addCommentShouldPersistWhenBookingCompleted() {
		User author = User.builder().id(1L).name("Ivan").build();
		Item item = Item.builder().id(2L).name("Drill").build();
		when(userService.getUserOrThrow(1L)).thenReturn(author);
		when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
		when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(eq(1L), eq(2L), eq(Status.APPROVED), any(LocalDateTime.class)))
				.thenReturn(true);
		when(commentRepository.saveAndFlush(any(Comment.class))).thenAnswer(invocation -> {
			Comment comment = invocation.getArgument(0);
			comment.setId(7L);
			return comment;
		});

		CommentDto result = itemService.addComment(1L, 2L, CommentCreateRequestDto.builder().text("Great").build());

		assertEquals(7L, result.getId());
		assertEquals("Great", result.getText());
	}

	@Test
	void addCommentShouldThrowWhenNoCompletedBooking() {
		when(userService.getUserOrThrow(1L)).thenReturn(User.builder().id(1L).build());
		when(itemRepository.findById(2L)).thenReturn(Optional.of(Item.builder().id(2L).build()));
		when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(eq(1L), eq(2L), eq(Status.APPROVED), any(LocalDateTime.class)))
				.thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> itemService.addComment(1L, 2L, CommentCreateRequestDto.builder().text("Hi").build()));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(commentRepository, never()).saveAndFlush(any(Comment.class));
	}
}
