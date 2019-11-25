package net.brodec.sandbox.cms;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.brodec.sandbox.cms.services.UserService;
import reactor.core.Exceptions;

@SpringBootApplication
public class Application {

	private static final String USER_ID_SPECIFICATION_OPTION_NAME = "u";

	public static void main(final String[] arguments) {
		try {
			final CommandLine commandLine = parseArguments(arguments);

			if (commandLine.hasOption(USER_ID_SPECIFICATION_OPTION_NAME)) {
				executeAsCommand(arguments, commandLine);
			} else {
				runAsService(arguments);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void executeAsCommand(final String[] arguments, final CommandLine commandLine) {
		turnOffStdoutLogging();

		final long userId = parseUserId(commandLine);

		System.out.println(userToJsonString(userId, arguments));
	}

	private static void runAsService(final String[] arguments) {
		SpringApplication.run(Application.class, arguments);
	}

	private static CommandLine parseArguments(final String[] arguments) throws ParseException {
		final Options options = new Options().addOption(USER_ID_SPECIFICATION_OPTION_NAME, true,
				"retrieve details of the user identified by a number in the associated argument");

		return new DefaultParser().parse(options, arguments);
	}

	private static long parseUserId(final CommandLine commandLine) {
		return Long.parseLong(commandLine.getOptionValue(USER_ID_SPECIFICATION_OPTION_NAME));
	}

	private static String userToJsonString(final long userId, final String[] arguments) {
		return getUserService(arguments).get(userId).map(user -> {
			try {
				return writeToJson(user);
			} catch (final JsonProcessingException e) {
				throw Exceptions.propagate(e);
			}
		}).block();
	}

	private static UserService getUserService(final String[] arguments) {
		return new SpringApplicationBuilder(Application.class).bannerMode(Mode.OFF).logStartupInfo(false)
				.web(WebApplicationType.NONE).run(arguments).getBean(UserService.class);
	}

	private static void turnOffStdoutLogging() {
		System.getProperties().setProperty("logging.pattern.console", "");
	}

	private static String writeToJson(final Object object) throws JsonProcessingException {
		return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
	}
}
