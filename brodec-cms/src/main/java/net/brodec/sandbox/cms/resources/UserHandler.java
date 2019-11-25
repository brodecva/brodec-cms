package net.brodec.sandbox.cms.resources;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import net.brodec.sandbox.cms.model.User;
import net.brodec.sandbox.cms.services.UserService;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

	private final UserService userService;

	public UserHandler(final UserService userService) {
		this.userService = userService;
	}

	public Mono<ServerResponse> get(final ServerRequest request) {
		final Mono<User> user = Mono.defer(() -> Mono.just(request.pathVariable("id")))
				.map(idText -> Long.parseLong(idText)).flatMap(id -> this.userService.get(id));

		return user.flatMap(u -> ok().contentType(MediaType.APPLICATION_JSON).body(fromPublisher(user, User.class)))
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(NumberFormatException.class, e -> ServerResponse.badRequest().build())
				.onErrorResume(IllegalArgumentException.class, e -> ServerResponse.badRequest().build());
	}
}