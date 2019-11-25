package net.brodec.sandbox.cms;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.brodec.sandbox.cms.model.Post;
import net.brodec.sandbox.cms.model.User;
import net.brodec.sandbox.cms.resources.UserHandler;
import net.brodec.sandbox.cms.routers.UserRouter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class UserHandlerIntegrationTest {

	@Autowired
	private UserHandler handler;

	@Autowired
	private UserRouter router;

	private WebTestClient client;

	@Before
	public void init() {
		client = WebTestClient.bindToRouterFunction(router.get(handler)).build();
	}

	@After
	public void cleanUp() {
		client = null;
	}

	@Test
	public void testGetExpectCorrectBody() throws JsonMappingException, JsonProcessingException {
		final User expectedUser = new User("Leanne Graham", "Bret", "Sincere@april.biz",
				Arrays.asList(new ObjectMapper().readValue("[\r\n" + "  {\r\n" + "    \"id\": 1,\r\n"
						+ "    \"title\": \"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\"\r\n"
						+ "  },\r\n" + "  {\r\n" + "    \"id\": 2,\r\n" + "    \"title\": \"qui est esse\"\r\n"
						+ "  },\r\n" + "  {\r\n" + "    \"id\": 3,\r\n"
						+ "    \"title\": \"ea molestias quasi exercitationem repellat qui ipsa sit aut\"\r\n"
						+ "  },\r\n" + "  {\r\n" + "    \"id\": 4,\r\n" + "    \"title\": \"eum et est occaecati\"\r\n"
						+ "  },\r\n" + "  {\r\n" + "    \"id\": 5,\r\n" + "    \"title\": \"nesciunt quas odio\"\r\n"
						+ "  },\r\n" + "  {\r\n" + "    \"id\": 6,\r\n"
						+ "    \"title\": \"dolorem eum magni eos aperiam quia\"\r\n" + "  },\r\n" + "  {\r\n"
						+ "    \"id\": 7,\r\n" + "    \"title\": \"magnam facilis autem\"\r\n" + "  },\r\n" + "  {\r\n"
						+ "    \"id\": 8,\r\n" + "    \"title\": \"dolorem dolore est ipsam\"\r\n" + "  },\r\n"
						+ "  {\r\n" + "    \"id\": 9,\r\n"
						+ "    \"title\": \"nesciunt iure omnis dolorem tempora et accusantium\"\r\n" + "  },\r\n"
						+ "  {\r\n" + "    \"id\": 10,\r\n" + "    \"title\": \"optio molestias id quia eum\"\r\n"
						+ "  }\r\n" + "]\r\n" + "", Post[].class)));

		client.get().uri("/users/1").exchange().expectStatus().isOk().expectBody(User.class).isEqualTo(expectedUser);
	}

	@Test
	public void testGetWhenNonexistingIdExpectNotFound() {
		client.get().uri("/users/987654321").exchange().expectStatus().isNotFound();
	}

	@Test
	public void testGetWhenInvalidIdExpectBadRequest() {
		client.get().uri("/users/1zdf48w").exchange().expectStatus().isBadRequest();
	}

	@Test
	public void testGetWhenNoIdExpectNotFound() {
		client.get().uri("/users/").exchange().expectStatus().isNotFound();
	}

	@Test
	public void testGetWhenCachedAndLargeNumberOfRequestsForExistingExpectToKeepUp() {
		populateCacheWithExisting();

		final long start = System.nanoTime();

		Flux.range(1, 100000).map(index -> ThreadLocalRandom.current().nextLong(1, 11))
				.flatMap(id -> Mono.fromCallable(() -> {
					client.get().uri("/users/{id}", id).exchange().expectStatus().isOk();

					return id;
				}).subscribeOn(Schedulers.elastic()), 1000).blockLast();

		final double durationSeconds = elapsedSecondsSince(start);

		System.out.println("Elapsed time (s): " + durationSeconds);

		assertTrue(durationSeconds < 50);
	}

	@Test
	public void testGetWhenCachedAndLargeNumberOfRequestsForNonexistingExpectToKeepUp() {
		populateCacheWithNonexisting();

		final long start = System.nanoTime();

		Flux.range(1, 100000).map(index -> ThreadLocalRandom.current().nextLong(11, 21))
				.flatMap(id -> Mono.fromCallable(() -> {
					client.get().uri("/users/{id}", id).exchange().expectStatus().isNotFound();

					return id;
				}).subscribeOn(Schedulers.elastic()), 1000).blockLast();

		final double durationSeconds = elapsedSecondsSince(start);

		System.out.println("Elapsed time (s): " + durationSeconds);

		assertTrue(durationSeconds < 50);
	}

	private void populateCacheWithExisting() {
		Flux.range(1, 10).flatMap(id -> Mono.fromCallable(() -> {
			client.get().uri("/users/{id}", id).exchange().expectStatus().isOk();

			System.out.println("Warm-up for " + id + " is OK.");

			return id;
		}).subscribeOn(Schedulers.elastic()), 1).blockLast();
	}

	private void populateCacheWithNonexisting() {
		Flux.range(11, 10).flatMap(id -> Mono.fromCallable(() -> {
			client.get().uri("/users/{id}", id).exchange().expectStatus().isNotFound();

			System.out.println("Warm-up for " + id + " is OK.");

			return id;
		}).subscribeOn(Schedulers.elastic()), 1).blockLast();
	}

	private static double elapsedSecondsSince(final long startNanos) {
		final long endNanos = System.nanoTime();
		final long durationNanos = endNanos - startNanos;
		return ((double) durationNanos) / 1_000_000_000;
	}
}