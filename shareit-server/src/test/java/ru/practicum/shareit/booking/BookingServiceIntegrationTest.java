package ru.practicum.shareit.booking;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceIntegrationTest {
	@Autowired
	private BookingService bookingService;
	@Autowired
	private UserService userService;
	@Autowired
	private ru.practicum.shareit.item.ItemService itemService;
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private ItemRepository itemRepository;

	@Test
	void createShouldPersistBooking() {
		UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());
		UserDto booker = userService.create(UserDto.builder().name("Booker").email("booker@test.com").build());
		ItemDto item = itemService.create(owner.getId(), ItemCreateRequestDto.builder().name("Drill").description("Cordless").available(true).build());

		BookingDto result = bookingService.create(booker.getId(), BookingCreateRequestDto.builder()
				.itemId(item.getId())
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.build());

		assertThat(result.getStatus()).isEqualTo(Status.WAITING);
		assertThat(bookingRepository.findById(result.getId())).isPresent();
	}

	@Test
	void getByIdShouldReturnPersistedBooking() {
		UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());
		UserDto booker = userService.create(UserDto.builder().name("Booker").email("booker@test.com").build());
		Item item = itemRepository.save(Item.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.owner(User.builder().id(owner.getId()).name(owner.getName()).email(owner.getEmail()).build())
				.build());
		Booking booking = bookingRepository.save(Booking.builder()
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.item(item)
				.booker(User.builder().id(booker.getId()).name(booker.getName()).email(booker.getEmail()).build())
				.status(Status.WAITING)
				.build());

		BookingDto result = bookingService.getById(booker.getId(), booking.getId());

		assertThat(result.getId()).isEqualTo(booking.getId());
	}

	@Test
	void getAllByBookerShouldFilterByState() {
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

		List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), "PAST");

		assertThat(result).hasSize(1);
	}

	@Test
	void getAllByOwnerShouldReturnBookings() {
		UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());
		UserDto booker = userService.create(UserDto.builder().name("Booker").email("booker@test.com").build());
		Item item = itemRepository.save(Item.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.owner(User.builder().id(owner.getId()).name(owner.getName()).email(owner.getEmail()).build())
				.build());
		bookingRepository.save(Booking.builder()
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.item(item)
				.booker(User.builder().id(booker.getId()).name(booker.getName()).email(booker.getEmail()).build())
				.status(Status.WAITING)
				.build());

		List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), "ALL");

		assertThat(result).hasSize(1);
	}

	@Test
	void approveShouldPersistApprovedStatus() {
		UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());
		UserDto booker = userService.create(UserDto.builder().name("Booker").email("booker@test.com").build());
		Item item = itemRepository.save(Item.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.owner(User.builder().id(owner.getId()).name(owner.getName()).email(owner.getEmail()).build())
				.build());
		Booking booking = bookingRepository.save(Booking.builder()
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.item(item)
				.booker(User.builder().id(booker.getId()).name(booker.getName()).email(booker.getEmail()).build())
				.status(Status.WAITING)
				.build());

		BookingDto result = bookingService.approve(owner.getId(), booking.getId(), true);

		assertThat(result.getStatus()).isEqualTo(Status.APPROVED);
	}
}
