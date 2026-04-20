package ru.practicum.shareit.item;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;

@WebMvcTest(ItemController.class)
class ItemControllerWebTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ItemService itemService;

	@Test
	void createShouldReturnItem() throws Exception {
		when(itemService.create(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any()))
				.thenReturn(ItemDto.builder().id(1L).name("Drill").build());

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Drill\",\"description\":\"Cordless\",\"available\":true}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void updateShouldReturnItem() throws Exception {
		when(itemService.update(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq(2L), org.mockito.ArgumentMatchers.any()))
				.thenReturn(ItemDto.builder().id(2L).name("Updated").build());

		mockMvc.perform(patch("/items/2")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Updated\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated"));
	}

	@Test
	void addCommentShouldReturnComment() throws Exception {
		when(itemService.addComment(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq(2L), org.mockito.ArgumentMatchers.any()))
				.thenReturn(CommentDto.builder().id(3L).text("Nice").authorName("Ivan").build());

		mockMvc.perform(post("/items/2/comment")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"text\":\"Nice\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(3));
	}

	@Test
	void getByIdShouldReturnItem() throws Exception {
		when(itemService.getById(1L, 2L)).thenReturn(ItemDto.builder().id(2L).name("Drill").build());

		mockMvc.perform(get("/items/2").header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(2));
	}

	@Test
	void getAllByOwnerShouldReturnItems() throws Exception {
		when(itemService.getAllByOwner(1L)).thenReturn(List.of(OwnerItemDto.builder().id(2L).name("Drill").build()));

		mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(2));
	}

	@Test
	void searchShouldReturnItems() throws Exception {
		when(itemService.search(1L, "drill")).thenReturn(List.of(ItemDto.builder().id(2L).name("Drill").build()));

		mockMvc.perform(get("/items/search").header("X-Sharer-User-Id", 1).param("text", "drill"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Drill"));
	}
}
