package pl.potera.reactor.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pl.potera.reactor.model.Employee;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {
}
