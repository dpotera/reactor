package pl.potera.reactor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.potera.reactor.model.Employee;
import pl.potera.reactor.repository.EmployeesRepository;
import reactor.core.publisher.Flux;

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

}
