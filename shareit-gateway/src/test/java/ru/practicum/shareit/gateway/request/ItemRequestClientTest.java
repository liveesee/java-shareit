package ru.practicum.shareit.gateway.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDto;

class ItemRequestClientTest {
	private ItemRequestClient itemRequestClient;
	private MockRestServiceServer server;

	@BeforeEach
	void setUp() {
		itemRequestClient = new ItemRequestClient(new RestTemplateBuilder(), new ObjectMapper(), "http://localhost:9090");
		RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(itemRequestClient, "restTemplate");
		server = MockRestServiceServer.bindTo(restTemplate).build();
	}

	@Test
	void createShouldPostToRequests() {
		server.expect(requestTo("http://localhost:9090/requests"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess());

		assertEquals(200, itemRequestClient.create(1L, ItemRequestDto.builder().description("Need drill").build())
				.getStatusCode().value());
	}

	@Test
	void getOwnerRequestsShouldCallRequestsPath() {
		server.expect(requestTo("http://localhost:9090/requests"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess());

		assertEquals(200, itemRequestClient.getOwnerRequests(1L).getStatusCode().value());
	}

	@Test
	void getAllShouldCallAllPath() {
		server.expect(requestTo("http://localhost:9090/requests/all"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess());

		assertEquals(200, itemRequestClient.getAll(1L).getStatusCode().value());
	}

	@Test
	void getByIdShouldCallRequestPath() {
		server.expect(requestTo("http://localhost:9090/requests/2"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess());

		assertEquals(200, itemRequestClient.getById(1L, 2L).getStatusCode().value());
	}
}
