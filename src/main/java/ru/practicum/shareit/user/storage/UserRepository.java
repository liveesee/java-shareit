package ru.practicum.shareit.user.storage;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);

	List<User> findAllByOrderByIdAsc();
}
