package pl.potera.crudcontroller.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.potera.crudcontroller.model.Employee;
import pl.potera.crudcontroller.repository.EmployeesRepository;

import java.util.List;

@RestController
@RequestMapping("/employees")
@AllArgsConstructor
public class EmployeesController {

    private final EmployeesRepository repository;

    @GetMapping
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable String id) {
        return repository.findById(id)
                .map(employee -> ResponseEntity.ok().body(employee))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee saveEmployee(@RequestBody Employee employee) {
        return repository.save(employee);
    }

    @GetMapping("/count")
    public Long count() {
        return repository.count();
    }
}
