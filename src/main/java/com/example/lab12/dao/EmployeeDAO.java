package com.example.lab12.dao;

import com.example.lab12.model.Employee;
import java.util.List;

public interface EmployeeDAO {
    void add(Employee employee);
    List<Employee> getAll();
    void update(Employee employee);
    void delete(int id);
}
