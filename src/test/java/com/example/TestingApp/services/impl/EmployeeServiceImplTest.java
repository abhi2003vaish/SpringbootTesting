package com.example.TestingApp.services.impl;

import com.example.TestingApp.TestContainerConfiguration;
import com.example.TestingApp.dto.EmployeeDto;
import com.example.TestingApp.entities.Employee;
import com.example.TestingApp.exceptions.ResourceNotFoundException;
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

import static org.assertj.core.api.Assertions.*;
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
    void testGetEmployeeById_whenEmployeeIsNotPresent_ThenThrowException(){

        //arrange
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act & assert

        assertThatThrownBy(()->employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
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

    @Test
    void testCreateNewEmployee_whenAttemptingToCreateEmployeeWithExistingEmail_thenThrowException(){

        //arrange
        when(employeeRepository.findByEmail(mockEmployeeDto.getEmail())).thenReturn(List.of(mockEmployee));

        //act & assert

        assertThatThrownBy(() -> employeeService.createNewEmployee(mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: "+mockEmployeeDto.getEmail());

        verify(employeeRepository).findByEmail(mockEmployeeDto.getEmail());
        verify(employeeRepository,never()).save(any()); // pass bec save() never invoke bec exception occurred earlier
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException(){

        //arrange
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act & assert
        assertThatThrownBy(() -> employeeService.updateEmployee(1L,mockEmployeeDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: "+mockEmployeeDto.getId());
        verify(employeeRepository).findById(1L);
        verify(employeeRepository,never()).save(any());
    }


    @Test
    void testUpdateEmployee_whenAttemptToUpdateEmail_thenThrowException(){

        //arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setEmail("random@gmail.com");
        mockEmployeeDto.setName("random");

//        act & assert
        assertThatThrownBy(() -> employeeService.updateEmployee(mockEmployeeDto.getId(),mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockEmployeeDto.getId());
        verify(employeeRepository,never()).save(any());


    }

    @Test
    void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee(){

        //arrange
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setSalary(10000000L);
        mockEmployeeDto.setName("random");

        Employee newEmployee=modelMapper.map(mockEmployeeDto,Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);

        //act
        EmployeeDto updatedEmployeeDto=employeeService.updateEmployee(mockEmployeeDto.getId(),mockEmployeeDto);

        //assert
        assertThat(updatedEmployeeDto).isEqualTo(mockEmployeeDto);
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());


    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException(){

        //arrange
        when(employeeRepository.existsById(1L)).thenReturn(false);

//        act & assert
        assertThatThrownBy(() -> employeeService.deleteEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: "+1L);

        verify(employeeRepository,never()).deleteById(anyLong());
    }

    @Test
    void testDeleteEmployee_whenEmployeeIsValid_thenDeleteEmployee(){
        //arrange
        when(employeeRepository.existsById(1L)).thenReturn(true);

        //assert
        assertThatCode(() -> employeeService.deleteEmployee(1L))
                .doesNotThrowAnyException();
        verify(employeeRepository).existsById(1L);
    }

}