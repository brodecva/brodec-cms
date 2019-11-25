package net.brodec.sandbox.cms.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface JsonPlaceholderClient {

	Mono<UserPlaceholder> getUser(long id);

	Flux<PostPlaceholder> listPostsForUser(long userId);

}
