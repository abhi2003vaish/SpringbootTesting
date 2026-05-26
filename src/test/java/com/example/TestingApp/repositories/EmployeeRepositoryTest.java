package com.example.TestingApp.repositories;

import com.example.TestingApp.TestContainerConfiguration;
import com.example.TestingApp.entities.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestContainerConfiguration.class)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp(){
        employee = Employee.builder()
//                .id(1L)
                .name("abhishek")
                .email("abhivaish@gmail.ccom")
                .salary(100L)
                .build();
    }

    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnEmployee() {
//        Arrange , Given
        employeeRepository.save(employee);

//        Act , when
        List<Employee> employeeList=employeeRepository.findByEmail(employee.getEmail());

//        Assert , then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isNotEmpty();
        assertThat(employeeList.get(0).getEmail()).isEqualTo(employee.getEmail());

    }

    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyEmployeeList() {

//        Given
        String email="notPresent.123@gmail.com";

//        when
        List<Employee>  employeeList=employeeRepository.findByEmail(email);

//        assert
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isEmpty();//employeelist should be empty so it run smoothly

    }
}