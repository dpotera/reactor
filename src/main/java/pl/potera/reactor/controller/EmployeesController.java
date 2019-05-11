package pl.potera.reactor.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.potera.reactor.model.Employee;
import pl.potera.reactor.repository.EmployeesRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/employees")
@AllArgsConstructor
public class EmployeesController {

    private EmployeesRepository repository;

    @GetMapping
    public Flux<Employee> getAllEmployees() {
        return repository.findAll();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Employee>> getEmployee(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Employee> saveEmployee(@RequestBody Employee employee) {
        return repository.save(employee);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Employee>> updateEmployee(@PathVariable(value = "id") String id,
                                                         @RequestBody Employee employee) {
        return repository.findById(id)
                .flatMap(existingEmployee -> {
                    existingEmployee.setName(employee.getName());
                    existingEmployee.setPoints(employee.getPoints());
                    return repository.save(existingEmployee);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteEmployee(@PathVariable(value = "id") String id) {
        return repository.findById(id)
                .flatMap(employee ->
                    repository.delete(employee)
                            .then(Mono.just(ResponseEntity.ok().<Void>build()))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/interval", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Employee> employeesInterval() {
        return repository.findAll()
                .delayElements(Duration.ofSeconds(1));
    }
}
