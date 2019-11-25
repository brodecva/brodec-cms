# Brodec CMS Demo

An attempt to implement a (readonly) web service with Spring Webflux.

It is my first encounter with Webflux/reactive programming, so please, do not judge me too harshly. :-)

## Usage

Use

```bash
mvn spring-boot:run
```

to run the service. Then you can query it like this:

```bash
curl -H "Accept:application/json" -i localhost:8080/users/5
```
, where 5 is an ID of the particular user.

Alternatively, you can just use the application as CLI command, by including user's ID as argument of '-u' option. In this case, no server is run, and only the IoC capabilities of the Spring are employed:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=-u=5
```
.

## Test

The tests (which include also attempt to verify the expected performance) can be run through

```bash
mvn test
```

.

## Contributing
This is just a throway project.

## License

This project is licensed under the terms of the MIT license.
