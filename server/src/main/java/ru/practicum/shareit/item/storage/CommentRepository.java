package ru.practicum.shareit.item.storage;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findAllByItemIdOrderByCreatedAsc(Long itemId);

	List<Comment> findAllByItemIdInOrderByItemIdAscCreatedAsc(List<Long> itemIds);
}
