package com.example.TestingApp.repositories;

import com.example.TestingApp.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
//    user-defined method gonna 
    List<Employee> findByEmail(String email);
}
