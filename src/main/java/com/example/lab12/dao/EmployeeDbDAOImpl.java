package com.example.lab12.dao;

import com.example.lab12.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EmployeeDbDAOImpl implements EmployeeDAO {
    private static final Logger logger = Logger.getLogger(EmployeeDbDAOImpl.class.getName());

    public EmployeeDbDAOImpl() {
        createTable();
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS employees (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    position VARCHAR(100) NOT NULL,
                    salary DOUBLE NOT NULL
                )
                """;

        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {
            logger.info("DAO SQL: CREATE TABLE IF NOT EXISTS employees");
            statement.execute(sql);
        } catch (SQLException e) {
            logger.severe("DAO error: cannot create table: " + e.getMessage());
            throw new RuntimeException("Помилка створення таблиці employees", e);
        }
    }

    @Override
    public void add(Employee employee) {
        String sql = "INSERT INTO employees (name, position, salary) VALUES (?, ?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            logger.info("DAO SQL: INSERT INTO employees");
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getPosition());
            statement.setDouble(3, employee.getSalary());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("DAO error: cannot add employee: " + e.getMessage());
            throw new RuntimeException("Помилка додавання співробітника", e);
        }
    }

    @Override
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, name, position, salary FROM employees ORDER BY id";

        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            logger.info("DAO SQL: SELECT id, name, position, salary FROM employees ORDER BY id");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String position = resultSet.getString("position");
                double salary = resultSet.getDouble("salary");

                employees.add(new Employee(id, name, position, salary));
            }
        } catch (SQLException e) {
            logger.severe("DAO error: cannot get employee list: " + e.getMessage());
            throw new RuntimeException("Помилка отримання списку співробітників", e);
        }

        return employees;
    }

    @Override
    public void update(Employee employee) {
        String sql = "UPDATE employees SET name = ?, position = ?, salary = ? WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            logger.info("DAO SQL: UPDATE employees WHERE id=" + employee.getId());
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getPosition());
            statement.setDouble(3, employee.getSalary());
            statement.setInt(4, employee.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("DAO error: cannot update employee: " + e.getMessage());
            throw new RuntimeException("Помилка редагування співробітника", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            logger.info("DAO SQL: DELETE FROM employees WHERE id=" + id);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("DAO error: cannot delete employee: " + e.getMessage());
            throw new RuntimeException("Помилка видалення співробітника", e);
        }
    }
}