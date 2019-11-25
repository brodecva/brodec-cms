package net.brodec.sandbox.cms.services;

import net.brodec.sandbox.cms.model.User;
import reactor.core.publisher.Mono;

public interface UserService {

	Mono<User> get(long id);

}
