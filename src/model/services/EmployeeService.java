package model.services;

import java.util.List;

import model.entities.Employee;
import modelo.dao.DaoFactory;
import modelo.dao.EmployeeDao;

public class EmployeeService {
	
	private EmployeeDao dao = DaoFactory.createEmployeeDao();
	public List<Employee> findAll() {
	    return dao.findAll();
		}
		
		public void saveOrUpdate(Employee obj) {
			if(obj.getId()==null) {
				dao.insert(obj);
			}else {
				dao.update(obj);
			}
		}
		
		public void remove(Employee obj) {
			dao.deleteById(obj.getId());
		}
}
