package ru.practicum.shareit.gateway.request;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import ru.practicum.shareit.gateway.request.dto.ItemRequestDto;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
	@Mock
	private ItemRequestClient itemRequestClient;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders.standaloneSetup(new ItemRequestController(itemRequestClient))
				.setValidator(validator)
				.build();
	}

	@Test
	void createShouldRejectBlankDescription() throws Exception {
		ItemRequestDto requestDto = ItemRequestDto.builder().description(" ").build();

		mockMvc.perform(post("/requests")
						.header("X-Sharer-User-Id", 1)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemRequestClient);
	}

	@Test
	void getAllShouldDelegateToClient() throws Exception {
		when(itemRequestClient.getAll(any(Long.class))).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(get("/requests/all")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk());

		verify(itemRequestClient).getAll(1L);
	}

	@Test
	void createShouldDelegateToClient() throws Exception {
		ItemRequestDto requestDto = ItemRequestDto.builder().description("Need drill").build();
		when(itemRequestClient.create(any(Long.class), any(ItemRequestDto.class)))
				.thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(post("/requests")
						.header("X-Sharer-User-Id", 1)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isOk());

		verify(itemRequestClient).create(org.mockito.ArgumentMatchers.eq(1L),
				argThat(request -> "Need drill".equals(request.getDescription())));
	}

	@Test
	void getOwnerRequestsShouldDelegateToClient() throws Exception {
		when(itemRequestClient.getOwnerRequests(1L)).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(get("/requests")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk());

		verify(itemRequestClient).getOwnerRequests(1L);
	}

	@Test
	void getByIdShouldDelegateToClient() throws Exception {
		when(itemRequestClient.getById(1L, 2L)).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(get("/requests/2")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk());

		verify(itemRequestClient).getById(1L, 2L);
	}
}
