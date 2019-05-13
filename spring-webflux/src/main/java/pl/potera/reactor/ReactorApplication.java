package pl.potera.reactor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.potera.reactor.handler.EmployeeHandler;
import pl.potera.reactor.model.Employee;
import pl.potera.reactor.repository.EmployeesRepository;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class ReactorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactorApplication.class, args);
	}

	@Bean
	CommandLineRunner init(EmployeesRepository repository) {
		return args -> {
			Flux<Employee> employees = Flux.just(
					new Employee(null, "Dominik", 10.0d),
					new Employee(null, "John", 250.0d),
					new Employee(null, "Mike", 100.0d),
					new Employee(null, "Ben", 700.0d)
			).flatMap(repository::save);

			employees.thenMany(repository.findAll())
					.subscribe(System.out::println);
		};
	}

//	@Bean
	RouterFunction<ServerResponse> router(EmployeeHandler handler) {
		return route(GET("/employees").and(accept(APPLICATION_JSON)), handler::getAllEmployees)
				.andRoute(POST("/employees").and(contentType(APPLICATION_JSON)), handler::saveEmployee)
				.andRoute(GET("/employees/interval").and(accept(TEXT_EVENT_STREAM)), handler::employeesInterval)
				.andRoute(GET("/employees/{id}").and(accept(APPLICATION_JSON)), handler::getEmployee)
				.andRoute(PUT("/employees/{id}").and(contentType(APPLICATION_JSON)), handler::updateEmployee)
				.andRoute(DELETE("/employees/{id}").and(accept(APPLICATION_JSON)), handler::deleteEmployee);
	}

	@Bean
	RouterFunction<ServerResponse> routerNest(EmployeeHandler handler) {
		return nest(path("/employees"),
				nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)).or(accept(TEXT_EVENT_STREAM)),
						route(GET("/"), handler::getAllEmployees)
								.andRoute(method(HttpMethod.POST), handler::saveEmployee)
								.andRoute(GET("/count"), handler::countEmployees)
								.andRoute(GET("/interval"), handler::employeesInterval)
								.andNest(path("/{id}"),
										route(method(HttpMethod.GET), handler::getEmployee)
												.andRoute(method(HttpMethod.PUT), handler::updateEmployee)))
												.andRoute(method(HttpMethod.DELETE), handler::deleteEmployee)
		);
	}
}
