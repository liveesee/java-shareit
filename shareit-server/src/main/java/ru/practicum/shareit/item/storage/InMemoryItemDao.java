package ru.practicum.shareit.item.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

@Repository
public class InMemoryItemDao implements ItemDao {
	private final Map<Long, Item> items = new HashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);

	@Override
	public Item save(Item item) {
		if (item.getId() == null) {
			item.setId(nextId.getAndIncrement());
		}
		items.put(item.getId(), item);
		return item;
	}

	@Override
	public Optional<Item> findById(long itemId) {
		return Optional.ofNullable(items.get(itemId));
	}

	@Override
	public Collection<Item> findAll() {
		return items.values();
	}
}

