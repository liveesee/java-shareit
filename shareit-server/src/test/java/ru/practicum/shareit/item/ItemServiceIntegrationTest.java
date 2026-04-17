package ru.practicum.shareit.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceIntegrationTest {
	@Autowired
	private ItemService itemService;
	@Autowired
	private UserService userService;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private ItemRequestRepository itemRequestRepository;
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private CommentRepository commentRepository;

	@Test
	void createShouldPersistItemWithRequest() {
		UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());
		ItemRequest request = itemRequestRepository.save(ItemRequest.builder()
				.description("Need drill")
				.requester(User.builder().id(owner.getId()).name(owner.getName()).email(owner.getEmail()).build())
				.created(LocalDateTime.now())
				.build());

		ItemDto result = itemService.create(owner.getId(), ItemCreateRequestDto.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.requestId(request.getId())
				.build());

		assertThat(itemRepository.findById(result.getId())).get()
				.extracting(item -> item.getRequest().getId())
				.isEqualTo(request.getId());
	}

	@Test
	void updateShouldPersistChangedFields() {
		UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());
		ItemDto created = itemService.create(owner.getId(), ItemCreateRequestDto.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.build());

		ItemDto result = itemService.update(owner.getId(), created.getId(), ItemUpdateRequestDto.builder().name("Updated").build());

		assertThat(result.getName()).isEqualTo("Updated");
	}

	@Test
	void getByIdShouldReturnPersistedItem() {
		UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());
		ItemDto created = itemService.create(owner.getId(), ItemCreateRequestDto.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.build());

		ItemDto result = itemService.getById(owner.getId(), created.getId());

		assertThat(result.getId()).isEqualTo(created.getId());
	}

	@Test
	void getAllByOwnerShouldReturnItemsWithComments() {
		UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());
		UserDto booker = userService.create(UserDto.builder().name("Booker").email("booker@test.com").build());
		Item item = itemRepository.save(Item.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.owner(User.builder().id(owner.getId()).name(owner.getName()).email(owner.getEmail()).build())
				.build());
		bookingRepository.save(Booking.builder()
				.start(LocalDateTime.now().minusDays(2))
				.end(LocalDateTime.now().minusDays(1))
				.item(item)
				.booker(User.builder().id(booker.getId()).name(booker.getName()).email(booker.getEmail()).build())
				.status(Status.APPROVED)
				.build());
		itemService.addComment(booker.getId(), item.getId(), CommentCreateRequestDto.builder().text("Nice").build());

		List<OwnerItemDto> result = itemService.getAllByOwner(owner.getId());

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getComments()).hasSize(1);
	}

	@Test
	void addCommentShouldPersistComment() {
		UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());
		UserDto booker = userService.create(UserDto.builder().name("Booker").email("booker@test.com").build());
		Item item = itemRepository.save(Item.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.owner(User.builder().id(owner.getId()).name(owner.getName()).email(owner.getEmail()).build())
				.build());
		bookingRepository.save(Booking.builder()
				.start(LocalDateTime.now().minusDays(2))
				.end(LocalDateTime.now().minusDays(1))
				.item(item)
				.booker(User.builder().id(booker.getId()).name(booker.getName()).email(booker.getEmail()).build())
				.status(Status.APPROVED)
				.build());

		itemService.addComment(booker.getId(), item.getId(), CommentCreateRequestDto.builder().text("Nice").build());

		assertThat(commentRepository.findAllByItemIdOrderByCreatedAsc(item.getId())).hasSize(1);
	}

	@Test
	void searchShouldReturnMatchingAvailableItems() {
		UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());
		itemService.create(owner.getId(), ItemCreateRequestDto.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.build());

		List<ItemDto> result = itemService.search(owner.getId(), "drill");

		assertThat(result).hasSize(1);
	}
}
