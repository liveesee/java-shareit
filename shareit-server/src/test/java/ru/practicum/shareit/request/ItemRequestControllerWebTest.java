package ru.practicum.shareit.request;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import ru.practicum.shareit.request.dto.ItemRequestDto;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerWebTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ItemRequestService itemRequestService;

	@Test
	void createShouldReturnRequest() throws Exception {
		when(itemRequestService.create(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any()))
				.thenReturn(ItemRequestDto.builder().id(1L).description("Need drill").build());

		mockMvc.perform(post("/requests")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"description\":\"Need drill\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void getOwnerRequestsShouldReturnRequests() throws Exception {
		when(itemRequestService.getOwnerRequests(1L)).thenReturn(List.of(ItemRequestDto.builder().id(1L).build()));

		mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1));
	}

	@Test
	void getAllShouldReturnRequests() throws Exception {
		when(itemRequestService.getAll(1L)).thenReturn(List.of(ItemRequestDto.builder().id(1L).build()));

		mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1));
	}

	@Test
	void getByIdShouldReturnRequest() throws Exception {
		when(itemRequestService.getById(1L, 2L)).thenReturn(ItemRequestDto.builder().id(2L).description("Need drill").build());

		mockMvc.perform(get("/requests/2").header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(2));
	}
}
