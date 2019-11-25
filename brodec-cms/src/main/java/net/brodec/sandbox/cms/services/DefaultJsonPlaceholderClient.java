package net.brodec.sandbox.cms.services;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DefaultJsonPlaceholderClient implements JsonPlaceholderClient {

	/**
	 * The end point address should be configurable. For the purposes of this demo,
	 * it is better to limit the complexity.
	 */
	private WebClient client = WebClient.create("http://jsonplaceholder.typicode.com");

	@Override
	public Mono<UserPlaceholder> getUser(final long id) {
		return this.client.get().uri("/users/{id}", id).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(UserPlaceholder.class)
				.onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty());
	}

	@Override
	public Flux<PostPlaceholder> listPostsForUser(final long userId) {
		return this.client.get().uri("/posts?userId={userId}", userId).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToFlux(PostPlaceholder.class);
	}
}
