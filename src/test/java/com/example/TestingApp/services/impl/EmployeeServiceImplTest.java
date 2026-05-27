package com.example.TestingApp.services.impl;

import com.example.TestingApp.TestContainerConfiguration;
import com.example.TestingApp.dto.EmployeeDto;
import com.example.TestingApp.entities.Employee;
import com.example.TestingApp.repositories.EmployeeRepository;
import com.example.TestingApp.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@DataJpaTest
//@SpringBootTest
@Import(TestContainerConfiguration.class)   // not required bec do not import t
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee mockEmployee;
    private EmployeeDto mockEmployeeDto;

    @BeforeEach
    void setUp(){
        mockEmployee = Employee.builder()
                .id(1L)
                .email("abhi@gmail.com")
                .name("abhi")
                .salary(100000L)
                .build();

        mockEmployeeDto= modelMapper.map(mockEmployee, EmployeeDto.class);
    }

    @Test
    void testGetEmployeeById_WhenEmployeeIdIsPresent_ThenReturnEmployeeDto(){

        //assign
        Long id=mockEmployee.getId();
//        Employee mockemployee=Employee.builder()
//                .id(id)
//                .email("abhi@gmail.com")
//                .name("abhi")
//                .salary(100000l)
//                .build();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));  //Stubbing

        //act

        EmployeeDto employeeDto=employeeService.getEmployeeById(id);

        //assert
        assertThat(employeeDto.getId()).isEqualTo(id);
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());
//        verify(employeeRepository).findById(id);  // verify that findById method is called with this id
//        verify(employeeRepository).save(null);  // it should failed bec this save() method never invoked
//        verify(employeeRepository).findById(2L); // it should fail bec for id 2L never invoked , for 1L it get invoked
//        verify(employeeRepository,times(2)).findById(id);// fail bec called only once not two
        verify(employeeRepository,atLeast(1)).findById(id); // pass bec allowed (n>=1) and n here is 1
        verify(employeeRepository,atMost(2)).findById(id); // pass bec 1<2 and allowed (n<=2)
        verify(employeeRepository,only()).findById(id); // pass bec only findById() method invoke in employeeService
    }

    @Test
    void testCreateNewEmployee_WhenValidEmployee_ThenCreatNewEmployee(){

        //assign

        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        //act

        EmployeeDto employeeDto=employeeService.createNewEmployee(mockEmployeeDto);


        //assert
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployeeDto.getEmail());

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
//        verify(employeeRepository).save(any(Employee.class));
        verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee captureEmployee = employeeArgumentCaptor.getValue();
        assertThat(captureEmployee.getEmail()).isEqualTo(mockEmployee.getEmail());


    }

}