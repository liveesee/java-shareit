package ru.practicum.shareit.gateway.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.gateway.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateRequestDto;

class ItemClientTest {
	private ItemClient itemClient;
	private MockRestServiceServer server;

	@BeforeEach
	void setUp() {
		itemClient = new ItemClient(new RestTemplateBuilder(), new ObjectMapper(), "http://localhost:9090");
		RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(itemClient, "restTemplate");
		server = MockRestServiceServer.bindTo(restTemplate).build();
	}

	@Test
	void createShouldPostToItemsWithHeader() {
		server.expect(requestTo("http://localhost:9090/items"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header("X-Sharer-User-Id", "1"))
				.andRespond(withSuccess());

		assertEquals(200, itemClient.create(1L, ItemCreateRequestDto.builder().name("Drill").build())
				.getStatusCode().value());
	}

	@Test
	void updateShouldPatchItem() {
		server.expect(requestTo("http://localhost:9090/items/2"))
				.andExpect(method(HttpMethod.PATCH))
				.andRespond(withSuccess());

		assertEquals(200, itemClient.update(1L, 2L, ItemUpdateRequestDto.builder().name("New").build())
				.getStatusCode().value());
	}

	@Test
	void addCommentShouldPostToCommentPath() {
		server.expect(requestTo("http://localhost:9090/items/2/comment"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess());

		assertEquals(200, itemClient.addComment(1L, 2L, CommentCreateRequestDto.builder().text("Nice").build())
				.getStatusCode().value());
	}

	@Test
	void getByIdShouldCallItemPath() {
		server.expect(requestTo("http://localhost:9090/items/2"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess());

		assertEquals(200, itemClient.getById(1L, 2L).getStatusCode().value());
	}

	@Test
	void getAllByOwnerShouldCallItemsPath() {
		server.expect(requestTo("http://localhost:9090/items"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess());

		assertEquals(200, itemClient.getAllByOwner(1L).getStatusCode().value());
	}

	@Test
	void searchShouldEncodeQueryString() {
		server.expect(requestTo("http://localhost:9090/items/search?text=drill"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess());

		assertEquals(200, itemClient.search(1L, "drill").getStatusCode().value());
	}

	@Test
	void createShouldReturnParsedErrorBody() {
		server.expect(requestTo("http://localhost:9090/items"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.NOT_FOUND)
						.contentType(MediaType.APPLICATION_JSON)
						.body("{\"error\":\"Request not found\"}"));

		Object body = itemClient.create(1L, ItemCreateRequestDto.builder().name("Drill").build()).getBody();

		assertEquals(Map.of("error", "Request not found"), body);
	}
}
