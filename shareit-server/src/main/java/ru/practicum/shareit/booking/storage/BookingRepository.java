package ru.practicum.shareit.booking.storage;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

	List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

	List<Booking> findAllByItemIdOrderByStartAsc(Long itemId);

	List<Booking> findAllByItemIdInOrderByItemIdAscStartAsc(List<Long> itemIds);

	boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId, Long itemId, Status status, LocalDateTime end);

	@Query("""
			select count(b) > 0
			from Booking b
			where b.item.id = ?1
				and b.status in ?4
				and b.start < ?3
				and b.end > ?2
			""")
	boolean existsOverlappingBooking(Long itemId, LocalDateTime start, LocalDateTime end, List<Status> statuses);
}
