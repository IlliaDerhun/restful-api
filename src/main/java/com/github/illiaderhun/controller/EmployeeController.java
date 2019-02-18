package com.github.illiaderhun.controller;

import com.github.illiaderhun.assembler.EmployeeResourceAssembler;
import com.github.illiaderhun.entity.Employee;
import com.github.illiaderhun.exception.EmployeeNotFoundException;
import com.github.illiaderhun.repository.EmployeeRepository;
import io.swagger.annotations.Api;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Api(value = "Employee controller", description = "The simple employee controller")
public class EmployeeController {

    private final EmployeeRepository repository;

    private final EmployeeResourceAssembler assembler;

    public EmployeeController(EmployeeRepository repository, EmployeeResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/employee")
    public Resources<Resource<Employee>> all() {

        /*List<Resource<Employee>> employees = repository.findAll().stream()
            .map(employee -> new Resource<>(employee,
                                            linkTo(methodOn(EmployeeController.class).findById(employee.getId())).withSelfRel(),
                                            linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
            .collect(Collectors.toList());*/

        List<Resource<Employee>> employees = repository.findAll().stream()
            .map(assembler::toResource)
            .collect(Collectors.toList());

        return new Resources<>(employees,
                               linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    @PostMapping("/employees")
    public ResponseEntity<?> newEmployee(@RequestBody Employee theEmployee) throws URISyntaxException {

        Resource<Employee> resource = assembler.toResource(repository.save(theEmployee));
        return ResponseEntity
            .created(new URI(resource.getId().expand().getHref()))
            .body(resource);
    }

    @GetMapping("/employees/{id}")
    public Resource<Employee> findById(@PathVariable Long id) {
        Employee theEmployee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toResource(theEmployee);
        /*return new Resource<>(theEmployee,
                              linkTo(methodOn(EmployeeController.class).findById(id)).withSelfRel(),
                              linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));*/
    }

    @PutMapping("/employees/{id}")
    public Employee replaceEmployee(@RequestBody Employee theEmployee, @PathVariable Long id) {
        return repository.findById(id)
            .map(employee -> {
                employee.setName(theEmployee.getName());
                employee.setRole(theEmployee.getRole());
                return repository.save(employee);
            })
            .orElseGet(() -> {
                theEmployee.setId(id);
                return repository.save(theEmployee);
            });
    }

    @DeleteMapping("/employees/{id}")
    public void deleteEmployeeById(@PathVariable Long id) {
        repository.deleteById(id);
    }

}
