package net.brodec.sandbox.cms;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class UserServerIntegrationTest {

	@LocalServerPort
	private int localServerPort;

	/**
	 * This test strongly depends on the limitations of the Netty and OS, which are
	 * not that easy to configure or circumvent.
	 * 
	 * Even in this state (and especially when increasing the number of attempts and
	 * parallelism), dropped connections or connection starvation can occur.
	 */
	@Test
	public void testGetWhenLargeNumberOfRequests() {
		populateCache();

		final long startNanos = System.nanoTime();

		Flux.range(1, 500).parallel(4).runOn(Schedulers.parallel())
				.map(index -> ThreadLocalRandom.current().nextLong(1, 21)).flatMap(id -> {
					final WebClient client = localPortWebClient();

					return client.get().uri("/users/{id}", id).accept(MediaType.APPLICATION_JSON).exchange();
				})
				.sequential().blockLast();

		final double durationSeconds = elapsedSecondsSince(startNanos);

		System.out.println("Elapsed time (s): " + durationSeconds);
	}

	private void populateCache() {
		Flux.range(1, 20).flatMap(id -> {
			final WebClient client = localPortWebClient();

			return client.get().uri("/users/{id}", id).accept(MediaType.APPLICATION_JSON).exchange();
		}).doOnNext(f -> {
			System.out.println("Running thread: " + Thread.currentThread().getName());
		}).doOnNext(System.out::println).blockLast();
	}

	private WebClient localPortWebClient() {
		return WebClient.create(String.format("http://localhost:%d", this.localServerPort));
	}

	private double elapsedSecondsSince(final long startNanos) {
		final long endNanos = System.nanoTime();
		final long durationNanos = endNanos - startNanos;
		return ((double) durationNanos) / 1_000_000_000;
	}
}