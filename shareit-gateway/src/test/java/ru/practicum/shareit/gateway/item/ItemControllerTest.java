package ru.practicum.shareit.gateway.item;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.practicum.shareit.gateway.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateRequestDto;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
	@Mock
	private ItemClient itemClient;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders.standaloneSetup(new ItemController(itemClient))
				.setValidator(validator)
				.build();
	}

	@Test
	void createShouldAllowMissingRequestId() throws Exception {
		ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
				.name("Drill")
				.description("Cordless")
				.available(true)
				.build();

		when(itemClient.create(any(Long.class), any(ItemCreateRequestDto.class)))
				.thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isOk());

		verify(itemClient).create(org.mockito.ArgumentMatchers.eq(1L),
				argThat(item -> "Drill".equals(item.getName())
						&& "Cordless".equals(item.getDescription())
						&& item.getRequestId() == null));
	}

	@Test
	void createShouldRejectBlankName() throws Exception {
		ItemCreateRequestDto requestDto = ItemCreateRequestDto.builder()
				.name(" ")
				.description("Cordless")
				.available(true)
				.requestId(3L)
				.build();

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemClient);
	}

	@Test
	void updateShouldDelegateToClient() throws Exception {
		ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder().name("Updated").build();
		when(itemClient.update(any(Long.class), any(Long.class), any(ItemUpdateRequestDto.class)))
				.thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(patch("/items/2")
						.header("X-Sharer-User-Id", 1)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isOk());

		verify(itemClient).update(org.mockito.ArgumentMatchers.eq(1L),
				org.mockito.ArgumentMatchers.eq(2L),
				argThat(item -> "Updated".equals(item.getName())));
	}

	@Test
	void addCommentShouldDelegateToClient() throws Exception {
		when(itemClient.addComment(any(Long.class), any(Long.class), any()))
				.thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(post("/items/2/comment")
						.header("X-Sharer-User-Id", 1)
						.contentType("application/json")
						.content("{\"text\":\"Nice\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void getByIdShouldDelegateToClient() throws Exception {
		when(itemClient.getById(1L, 2L)).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(get("/items/2")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk());

		verify(itemClient).getById(1L, 2L);
	}

	@Test
	void getAllByOwnerShouldDelegateToClient() throws Exception {
		when(itemClient.getAllByOwner(1L)).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(get("/items")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk());

		verify(itemClient).getAllByOwner(1L);
	}

	@Test
	void searchShouldDelegateToClient() throws Exception {
		when(itemClient.search(1L, "drill")).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(get("/items/search")
						.header("X-Sharer-User-Id", 1)
						.param("text", "drill"))
				.andExpect(status().isOk());

		verify(itemClient).search(1L, "drill");
	}
}
