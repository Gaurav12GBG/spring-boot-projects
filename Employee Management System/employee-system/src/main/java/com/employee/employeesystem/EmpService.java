package com.employee.employeesystem;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpService {

	@Autowired
	private EmpRepo repo;

	public void createEmp(EmpEntity e) {
		repo.save(e);
	}

	public List<EmpEntity> getAllEmp() {
		return repo.findAll();
	}

	public EmpEntity getEmpById(int id) {
		Optional<EmpEntity> e = repo.findById(id);
		if (e.isPresent()) {
			return e.get();
		}

		return null;
	}

	public void deleteEmp(int id) {
		repo.deleteById(id);
	}
}
