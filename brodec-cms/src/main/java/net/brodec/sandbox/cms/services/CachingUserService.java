package net.brodec.sandbox.cms.services;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import net.brodec.sandbox.cms.model.Post;
import net.brodec.sandbox.cms.model.User;
import reactor.cache.CacheMono;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CachingUserService implements UserService {

	private final LoadingCache<Long, Object> cache = Caffeine.newBuilder().maximumSize(10_000)
			.expireAfterWrite(30, TimeUnit.MINUTES).refreshAfterWrite(30, TimeUnit.MINUTES)
			.build(key -> remoteQuery(key));

	private final JsonPlaceholderClient client;

	public CachingUserService(final JsonPlaceholderClient client) {
		this.client = client;
	}

	@Override
	public Mono<User> get(final long id) {
		return CacheMono.lookup(cache.asMap(), id, User.class).onCacheMissResume(remoteQuery(id));
	}

	private Mono<User> remoteQuery(final long id) {
		final Mono<UserPlaceholder> userPlaceholder = client.getUser(id);

		final Flux<Post> posts = client.listPostsForUser(id).map(CachingUserService::mapToPost);

		return userPlaceholder.zipWith(posts.collectList(), CachingUserService::mapToUser);
	}

	private static Post mapToPost(final PostPlaceholder placeholder) {
		return new Post(placeholder.getId(), placeholder.getTitle());
	}

	private static User mapToUser(final UserPlaceholder userPlaceholder, final List<Post> posts) {
		return new User(userPlaceholder.getName(), userPlaceholder.getUsername(), userPlaceholder.getEmail(), posts);
	}
}
