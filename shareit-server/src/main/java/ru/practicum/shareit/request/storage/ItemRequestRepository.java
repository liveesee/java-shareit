package ru.practicum.shareit.request.storage;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
	List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);

	List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Long requesterId);
}
