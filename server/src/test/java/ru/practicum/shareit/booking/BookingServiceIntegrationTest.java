package ru.practicum.shareit.booking;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceIntegrationTest {
	@Autowired
	private BookingService bookingService;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void createShouldPersistBooking() {
		User owner = persistUser("Owner", "owner@test.com");
		User booker = persistUser("Booker", "booker@test.com");
		Item item = persistItem(owner, "Drill", "Cordless", true);

		BookingDto result = bookingService.create(booker.getId(), BookingCreateRequestDto.builder()
				.itemId(item.getId())
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.build());

		assertThat(result.getStatus()).isEqualTo(Status.WAITING);
		assertThat(bookingService.getById(booker.getId(), result.getId()).getId()).isEqualTo(result.getId());
	}

	@Test
	void getByIdShouldReturnPersistedBooking() {
		User owner = persistUser("Owner", "owner@test.com");
		User booker = persistUser("Booker", "booker@test.com");
		Item item = persistItem(owner, "Drill", "Cordless", true);
		Booking booking = persistBooking(item, booker,
				LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2),
				Status.WAITING);

		BookingDto result = bookingService.getById(booker.getId(), booking.getId());

		assertThat(result.getId()).isEqualTo(booking.getId());
	}

	@Test
	void getAllByBookerShouldFilterByState() {
		User owner = persistUser("Owner", "owner@test.com");
		User booker = persistUser("Booker", "booker@test.com");
		Item item = persistItem(owner, "Drill", "Cordless", true);
		persistBooking(item, booker,
				LocalDateTime.now().minusDays(2),
				LocalDateTime.now().minusDays(1),
				Status.APPROVED);

		List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), "PAST");

		assertThat(result).hasSize(1);
	}

	@Test
	void getAllByOwnerShouldReturnBookings() {
		User owner = persistUser("Owner", "owner@test.com");
		User booker = persistUser("Booker", "booker@test.com");
		Item item = persistItem(owner, "Drill", "Cordless", true);
		persistBooking(item, booker,
				LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2),
				Status.WAITING);

		List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), "ALL");

		assertThat(result).hasSize(1);
	}

	@Test
	void approveShouldPersistApprovedStatus() {
		User owner = persistUser("Owner", "owner@test.com");
		User booker = persistUser("Booker", "booker@test.com");
		Item item = persistItem(owner, "Drill", "Cordless", true);
		Booking booking = persistBooking(item, booker,
				LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2),
				Status.WAITING);

		BookingDto result = bookingService.approve(owner.getId(), booking.getId(), true);

		assertThat(result.getStatus()).isEqualTo(Status.APPROVED);
	}

	private User persistUser(String name, String email) {
		User user = User.builder().name(name).email(email).build();
		entityManager.persist(user);
		entityManager.flush();
		return user;
	}

	private Item persistItem(User owner, String name, String description, boolean available) {
		Item item = Item.builder()
				.name(name)
				.description(description)
				.available(available)
				.owner(owner)
				.build();
		entityManager.persist(item);
		entityManager.flush();
		return item;
	}

	private Booking persistBooking(Item item, User booker, LocalDateTime start, LocalDateTime end, Status status) {
		Booking booking = Booking.builder()
				.start(start)
				.end(end)
				.item(item)
				.booker(booker)
				.status(status)
				.build();
		entityManager.persist(booking);
		entityManager.flush();
		return booking;
	}
}
