package modelo.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Employee;

public interface EmployeeDao {
	void insert(Employee obj);

	void update(Employee obj);

	void deleteById(Integer id);

	Employee findById(Integer id);

	List<Employee> findAll();

	List<Employee> findByDepartment(Department department);
}
