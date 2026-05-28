package com.example.lab12.service;

import com.example.lab12.dao.EmployeeDAO;
import com.example.lab12.dao.EmployeeDbDAOImpl;
import com.example.lab12.model.Employee;

import java.util.List;
import java.util.logging.Logger;

public class EmployeeService {
    private static final Logger logger = Logger.getLogger(EmployeeService.class.getName());
    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        employeeDAO = new EmployeeDbDAOImpl();
        logger.info("Service created");
    }

    public void addEmployee(String name, String position, String salaryText) {
        double salary = parseSalary(salaryText);
        checkText(name, "Ім'я");
        checkText(position, "Посада");

        Employee employee = new Employee(0, name.trim(), position.trim(), salary);
        employeeDAO.add(employee);
        logger.info("Service: employee added");
    }

    public List<Employee> getAllEmployees() {
        logger.info("Service: get all employees");
        return employeeDAO.getAll();
    }

    public void updateEmployee(int id, String name, String position, String salaryText) {
        double salary = parseSalary(salaryText);
        checkText(name, "Ім'я");
        checkText(position, "Посада");

        Employee employee = new Employee(id, name.trim(), position.trim(), salary);
        employeeDAO.update(employee);
        logger.info("Service: employee updated, id=" + id);
    }

    public void deleteEmployee(int id) {
        employeeDAO.delete(id);
        logger.info("Service: employee deleted, id=" + id);
    }

    private void checkText(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            logger.warning("Validation error: empty text field");
            throw new IllegalArgumentException(fieldName + " не може бути порожнім");
        }
    }

    private double parseSalary(String salaryText) {
        try {
            double salary = Double.parseDouble(salaryText.trim().replace(',', '.'));

            if (salary <= 0) {
                logger.warning("Validation error: salary <= 0");
                throw new IllegalArgumentException("Зарплата повинна бути більше 0");
            }

            return salary;
        } catch (NumberFormatException e) {
            logger.warning("Validation error: salary is not a number");
            throw new IllegalArgumentException("Зарплата повинна бути числом");
        }
    }
}