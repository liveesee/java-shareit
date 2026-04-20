package ru.practicum.shareit.item.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

class InMemoryItemDaoTest {
	@Test
	void saveShouldAssignIdWhenMissing() {
		InMemoryItemDao dao = new InMemoryItemDao();
		Item item = Item.builder().name("Drill").description("Cordless").available(true).build();

		Item saved = dao.save(item);

		assertEquals(1L, saved.getId());
	}

	@Test
	void findByIdShouldReturnSavedItem() {
		InMemoryItemDao dao = new InMemoryItemDao();
		Item item = dao.save(Item.builder().name("Drill").description("Cordless").available(true).build());

		assertEquals(item, dao.findById(item.getId()).orElseThrow());
	}

	@Test
	void findAllShouldReturnAllSavedItems() {
		InMemoryItemDao dao = new InMemoryItemDao();
		dao.save(Item.builder().name("Drill").description("Cordless").available(true).build());

		assertEquals(1, dao.findAll().size());
		assertTrue(dao.findAll().iterator().hasNext());
	}
}
