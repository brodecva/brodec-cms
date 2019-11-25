package net.brodec.sandbox.cms.routers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import net.brodec.sandbox.cms.resources.WelcomeHandler;

@Configuration
public class WelcomeRouter {

	@Bean
	public RouterFunction<ServerResponse> route(WelcomeHandler welcomeHandler) {
		return RouterFunctions.route(RequestPredicates.GET("/").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
				welcomeHandler::welcome);
	}
}