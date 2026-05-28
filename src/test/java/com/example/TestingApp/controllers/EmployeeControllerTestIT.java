package com.example.TestingApp.controllers;

import com.example.TestingApp.TestContainerConfiguration;
import com.example.TestingApp.dto.EmployeeDto;
import com.example.TestingApp.entities.Employee;
import com.example.TestingApp.repositories.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@AutoConfigureWebTestClient(timeout = "100000")    // it is used to auto configure WebTestClient
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)   // start the application on random port
//@Import(TestContainerConfiguration.class)
class EmployeeControllerTestIT extends AbstractIntegrationTest {

//    @Autowired
//    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;

//    private Employee testEmployee;
//
//    private EmployeeDto testEmployeeDto;



    @BeforeEach
    void setUp(){
//        testEmployee = Employee.builder()
////                .id(1L)
//                .email("abhi@gmail.com")
//                .name("abhi")
//                .salary(100000L)
//                .build();
//
//        testEmployeeDto=EmployeeDto.builder()
////                .id(1L)
//                .email("abhi@gmail.com")
//                .name("abhi")
//                .salary(100000L)
//                .build();

        employeeRepository.deleteAll(); // to clear the database before each test case run
    }

    @Test
    void testGetEmployeeById_success(){
        Employee savedEmployee = employeeRepository.save(testEmployee); // firstly if we have to test this
        // getEmployeeById() we need to save the employee in the database before
        testEmployeeDto.setId(savedEmployee.getId());

        webTestClient.get()
                .uri("/employees/{id}",savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .isEqualTo(testEmployeeDto)
//                .value(employeeDdto -> {
//                    assertThat(employeeDdto.getEmail()).isEqualTo(savedEmployee.getEmail());
//                    assertThat(employeeDdto.getId()).isEqualTo(savedEmployee.getId());
//                })
        ;
    }

    @Test
    void testGetEmployeeById_fail(){
        webTestClient.get()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();


    }

    @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExists_thenThrowException(){
        Employee savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();


    }

    @Test
    void testCreateNewEmployee_whenEmployeeDoesNotExists_thenCreateNewemployee(){

        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(testEmployeeDto.getEmail())
                .jsonPath("$.name").isEqualTo(testEmployeeDto.getName());
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException(){
        webTestClient.put()
                .uri("/employees/999")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void testUpdateEmployee_whenAttemptingToUpdateTheEmail_thenThrowException(){
        Employee savedEmployee = employeeRepository.save(testEmployee);
        testEmployeeDto.setName("random");
        testEmployeeDto.setEmail("random@gmail.com");

        webTestClient.put()
                .uri("/employees/{id}",savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();

    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenUpdateEmployee(){
        Employee savedEmployee = employeeRepository.save(testEmployee);
        testEmployeeDto.setName("random");
        testEmployeeDto.setSalary(109999L);
        testEmployeeDto.setId(savedEmployee.getId());

        webTestClient.put()
                .uri("/employees/{id}",savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .isEqualTo(testEmployeeDto);
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException(){
        webTestClient.delete()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployee_whenEmployeeExists_thenDeleteEmployee(){
        Employee savedEmployee = employeeRepository.save(testEmployee);

        webTestClient.delete()
                .uri("/employees/{id}",savedEmployee.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(void.class);

        webTestClient.delete()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }














}