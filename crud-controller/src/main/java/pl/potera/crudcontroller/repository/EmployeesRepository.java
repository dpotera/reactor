package pl.potera.crudcontroller.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.potera.crudcontroller.model.Employee;

public interface EmployeesRepository extends MongoRepository<Employee, String> {
}
