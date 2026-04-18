package ru.practicum.shareit.gateway.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import ru.practicum.shareit.gateway.user.dto.UserDto;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
	@Mock
	private UserClient userClient;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userClient))
				.setValidator(validator)
				.build();
	}

	@Test
	void createShouldDelegateToClient() throws Exception {
		UserDto dto = UserDto.builder().name("Ivan").email("ivan@test.com").build();
		when(userClient.create(any(UserDto.class))).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(post("/users")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk());

		verify(userClient).create(argThat(user -> "Ivan".equals(user.getName())
				&& "ivan@test.com".equals(user.getEmail())));
	}

	@Test
	void createShouldRejectInvalidEmail() throws Exception {
		UserDto dto = UserDto.builder().name("Ivan").email("bad").build();

		mockMvc.perform(post("/users")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(userClient);
	}

	@Test
	void updateShouldDelegateToClient() throws Exception {
		when(userClient.update(any(Long.class), any(UserDto.class))).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(patch("/users/1")
						.contentType("application/json")
						.content("{\"name\":\"New\"}"))
				.andExpect(status().isOk());

		verify(userClient).update(any(Long.class), any(UserDto.class));
	}

	@Test
	void getByIdShouldDelegateToClient() throws Exception {
		when(userClient.getById(1L)).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(get("/users/1"))
				.andExpect(status().isOk());
	}

	@Test
	void getAllShouldDelegateToClient() throws Exception {
		when(userClient.getAll()).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(get("/users"))
				.andExpect(status().isOk());
	}

	@Test
	void deleteShouldDelegateToClient() throws Exception {
		when(userClient.delete(1L)).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(delete("/users/1"))
				.andExpect(status().isOk());
	}
}
