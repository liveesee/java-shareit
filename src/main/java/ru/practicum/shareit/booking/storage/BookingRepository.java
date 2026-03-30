package ru.practicum.shareit.booking.storage;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

	List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

	List<Booking> findAllByItemIdOrderByStartAsc(Long itemId);

	boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId, Long itemId, Status status, LocalDateTime end);
}
