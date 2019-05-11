package pl.potera.reactor.handler;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.potera.reactor.model.Employee;
import pl.potera.reactor.repository.EmployeesRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
@AllArgsConstructor
public class EmployeeHandler {

    EmployeesRepository repository;

    public Mono<ServerResponse> getAllEmployees(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(repository.findAll(), Employee.class);
    }

    public Mono<ServerResponse> getEmployee(ServerRequest request) {
        String id = request.pathVariable("id");
        return repository.findById(id)
                .flatMap(employee ->
                        ServerResponse.ok()
                                .contentType(APPLICATION_JSON)
                                .body(fromObject(employee)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> saveEmployee(ServerRequest request) {
        Mono<Employee> employeeMono = request.bodyToMono(Employee.class);
        return employeeMono.flatMap(employee ->
                ServerResponse.status(HttpStatus.CREATED)
                        .contentType(APPLICATION_JSON)
                        .body(repository.save(employee), Employee.class)
        );
    }

    public Mono<ServerResponse> updateEmployee(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Employee> employeeMono = request.bodyToMono(Employee.class);
        Mono<Employee> existingEmployeMono = repository.findById(id);

        return employeeMono.zipWith(existingEmployeMono,
                (employee, existingEmployee) -> new Employee(
                        existingEmployee.getId(),
                        employee.getName(),
                        employee.getPoints()
                )
        ).flatMap(employee ->
                ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(repository.save(employee), Employee.class)
        ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteEmployee(ServerRequest request) {
        String id = request.pathVariable("id");
        return repository.findById(id)
                .flatMap(employee ->
                        repository.delete(employee)
                                .then(ServerResponse.ok().build())
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> employeesInterval(ServerRequest request) {
        Flux<Employee> employeeFlux = repository.findAll().delayElements(Duration.ofSeconds(1));
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(employeeFlux, Employee.class);
    }
}
