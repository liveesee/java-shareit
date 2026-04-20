package ru.practicum.shareit.item.storage;

import java.util.Collection;
import java.util.Optional;
import ru.practicum.shareit.item.model.Item;

public interface ItemDao {
	Item save(Item item);

	Optional<Item> findById(long itemId);

	Collection<Item> findAll();
}

