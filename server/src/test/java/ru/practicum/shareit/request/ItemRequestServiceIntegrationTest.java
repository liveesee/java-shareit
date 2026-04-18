package ru.practicum.shareit.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIntegrationTest {
	@Autowired
	private ItemRequestService itemRequestService;
	@Autowired
	private UserService userService;
	@Autowired
	private ItemRequestRepository itemRequestRepository;
	@Autowired
	private ItemRepository itemRepository;

	@Test
	void createShouldPersistRequest() {
		UserDto requester = userService.create(UserDto.builder().name("Requester").email("req@test.com").build());

		ItemRequestDto result = itemRequestService.create(requester.getId(), ItemRequestDto.builder().description("Need drill").build());

		assertThat(result.getId()).isNotNull();
		assertThat(itemRequestRepository.findById(result.getId())).isPresent();
	}

	@Test
	void getByIdShouldReturnRequestWithItems() {
		UserDto requester = userService.create(UserDto.builder().name("Requester").email("req@test.com").build());
		UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());
		ItemRequest request = itemRequestRepository.save(ItemRequest.builder()
				.description("Need drill")
				.requester(User.builder().id(requester.getId()).name(requester.getName()).email(requester.getEmail()).build())
				.created(LocalDateTime.now())
				.build());
		itemRepository.save(Item.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.owner(User.builder().id(owner.getId()).name(owner.getName()).email(owner.getEmail()).build())
				.request(request)
				.build());

		ItemRequestDto result = itemRequestService.getById(requester.getId(), request.getId());

		assertThat(result.getItems()).hasSize(1);
	}

	@Test
	void getOwnerRequestsShouldReturnOwnRequests() {
		UserDto requester = userService.create(UserDto.builder().name("Requester").email("req@test.com").build());
		itemRequestService.create(requester.getId(), ItemRequestDto.builder().description("Need drill").build());

		List<ItemRequestDto> result = itemRequestService.getOwnerRequests(requester.getId());

		assertThat(result).hasSize(1);
	}

	@Test
	void getAllShouldReturnOtherUsersRequests() {
		UserDto requester = userService.create(UserDto.builder().name("Requester").email("req@test.com").build());
		UserDto viewer = userService.create(UserDto.builder().name("Viewer").email("viewer@test.com").build());
		itemRequestService.create(requester.getId(), ItemRequestDto.builder().description("Need drill").build());

		List<ItemRequestDto> result = itemRequestService.getAll(viewer.getId());

		assertThat(result).hasSize(1);
	}
}
