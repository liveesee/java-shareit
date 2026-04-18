package ru.practicum.shareit.user;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import ru.practicum.shareit.user.dto.UserDto;

@WebMvcTest(UserController.class)
class UserControllerWebTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Test
	void createShouldReturnUser() throws Exception {
		when(userService.create(org.mockito.ArgumentMatchers.any(UserDto.class)))
				.thenReturn(UserDto.builder().id(1L).name("Ivan").email("ivan@test.com").build());

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Ivan\",\"email\":\"ivan@test.com\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.email").value("ivan@test.com"));
	}

	@Test
	void updateShouldReturnUser() throws Exception {
		when(userService.update(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any(UserDto.class)))
				.thenReturn(UserDto.builder().id(1L).name("New").email("new@test.com").build());

		mockMvc.perform(patch("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"New\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("New"));
	}

	@Test
	void getByIdShouldReturnUser() throws Exception {
		when(userService.getById(1L)).thenReturn(UserDto.builder().id(1L).name("Ivan").email("ivan@test.com").build());

		mockMvc.perform(get("/users/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void getAllShouldReturnUsers() throws Exception {
		when(userService.getAll()).thenReturn(List.of(UserDto.builder().id(1L).name("Ivan").email("ivan@test.com").build()));

		mockMvc.perform(get("/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1));
	}

	@Test
	void deleteShouldReturnOk() throws Exception {
		doNothing().when(userService).delete(1L);

		mockMvc.perform(delete("/users/1"))
				.andExpect(status().isOk());
	}
}
