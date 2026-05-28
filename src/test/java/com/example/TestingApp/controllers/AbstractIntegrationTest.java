package com.example.TestingApp.controllers;

import com.example.TestingApp.TestContainerConfiguration;
import com.example.TestingApp.dto.EmployeeDto;
import com.example.TestingApp.entities.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient(timeout = "100000")    // it is used to auto configure WebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)   // start the application on random port
@Import(TestContainerConfiguration.class)
public class AbstractIntegrationTest {

    @Autowired
    public WebTestClient webTestClient;

    Employee testEmployee = Employee.builder()
//                .id(1L)
            .email("abhi@gmail.com")
                .name("abhi")
                .salary(100000L)
                .build();

    EmployeeDto testEmployeeDto=EmployeeDto.builder()
//                .id(1L)
            .email("abhi@gmail.com")
                .name("abhi")
                .salary(100000L)
                .build();
}
