package com.example.lab12.controller;

import com.example.lab12.model.Employee;
import com.example.lab12.service.EmployeeService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.logging.Logger;

public class MainController {
    private static final Logger logger = Logger.getLogger(MainController.class.getName());

    private EmployeeService employeeService;

    private TableView<Employee> tableView;

    private TextField nameField;
    private TextField positionField;
    private TextField salaryField;

    private ProgressBar progressBar;
    private Label statusLabel;

    public void show(Stage stage) {
        logger.info("Main window opened");

        try {
            employeeService = new EmployeeService();
            logger.info("MySQL connection is successful");
        } catch (Exception e) {
            logger.severe("MySQL connection error");
            showError("Не вдалося підключитися до MySQL. Перевірте, чи запущений MySQL і чи правильний пароль у Database.java");
            return;
        }

        Label titleLabel = new Label("Система обліку співробітників (MySQL + Task + Logger)");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        nameField = new TextField();
        nameField.setPromptText("Наприклад: Іван");

        positionField = new TextField();
        positionField.setPromptText("Наприклад: Менеджер");

        salaryField = new TextField();
        salaryField.setPromptText("Наприклад: 25000");

        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(15);

        formPane.add(new Label("Ім'я:"), 0, 0);
        formPane.add(nameField, 1, 0);

        formPane.add(new Label("Посада:"), 0, 1);
        formPane.add(positionField, 1, 1);

        formPane.add(new Label("Зарплата:"), 0, 2);
        formPane.add(salaryField, 1, 2);

        Button addButton = new Button("Додати");
        Button editButton = new Button("Редагувати");
        Button deleteButton = new Button("Видалити");
        Button clearButton = new Button("Очистити");

        addButton.setOnAction(e -> addEmployee());
        editButton.setOnAction(e -> editEmployee());
        deleteButton.setOnAction(e -> deleteEmployee());
        clearButton.setOnAction(e -> clearFields());

        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, clearButton);

        Button validateButton = new Button("Перевірити співробітників");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(270);

        statusLabel = new Label("Очікування");

        validateButton.setOnAction(e -> validateEmployees());

        HBox taskBox = new HBox(15, validateButton, progressBar, statusLabel);

        tableView = new TableView<>();

        TableColumn<Employee, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getId())
        );
        idColumn.setPrefWidth(90);

        TableColumn<Employee, String> nameColumn = new TableColumn<>("Ім'я");
        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName())
        );
        nameColumn.setPrefWidth(240);

        TableColumn<Employee, String> positionColumn = new TableColumn<>("Посада");
        positionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPosition())
        );
        positionColumn.setPrefWidth(260);

        TableColumn<Employee, String> salaryColumn = new TableColumn<>("Зарплата");
        salaryColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f грн", data.getValue().getSalary()))
        );
        salaryColumn.setPrefWidth(230);

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(positionColumn);
        tableView.getColumns().add(salaryColumn);

        tableView.setPrefHeight(370);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                nameField.setText(newValue.getName());
                positionField.setText(newValue.getPosition());
                salaryField.setText(String.valueOf(newValue.getSalary()));

                logger.info("Employee selected, id=" + newValue.getId());
            }
        });

        VBox root = new VBox(20);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(
                titleLabel,
                formPane,
                buttonBox,
                taskBox,
                tableView
        );

        Scene scene = new Scene(root, 930, 730);

        stage.setTitle("ЛР12 - Employee System");
        stage.setScene(scene);
        stage.show();

        refreshTable();
    }

    private void addEmployee() {
        try {
            logger.info("Add button clicked");

            employeeService.addEmployee(
                    nameField.getText(),
                    positionField.getText(),
                    salaryField.getText()
            );

            refreshTable();
            clearFields();

            statusLabel.setText("Співробітника додано");
        } catch (Exception e) {
            logger.warning("Add error");
            showError(e.getMessage());
        }
    }

    private void editEmployee() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Оберіть співробітника для редагування");
            return;
        }

        try {
            logger.info("Update button clicked, id=" + selected.getId());

            employeeService.updateEmployee(
                    selected.getId(),
                    nameField.getText(),
                    positionField.getText(),
                    salaryField.getText()
            );

            refreshTable();
            clearFields();

            statusLabel.setText("Співробітника оновлено");
        } catch (Exception e) {
            logger.warning("Update error");
            showError(e.getMessage());
        }
    }

    private void deleteEmployee() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Оберіть співробітника для видалення");
            return;
        }

        try {
            logger.info("Delete button clicked, id=" + selected.getId());

            employeeService.deleteEmployee(selected.getId());

            refreshTable();
            clearFields();

            statusLabel.setText("Співробітника видалено");
        } catch (Exception e) {
            logger.warning("Delete error");
            showError(e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            tableView.setItems(FXCollections.observableArrayList(employees));
        } catch (Exception e) {
            logger.severe("Refresh table error");
            showError("Не вдалося оновити таблицю");
        }
    }

    private Task<List<Employee>> createValidationTask() {
        return new Task<>() {
            @Override
            protected List<Employee> call() throws Exception {
                logger.info("Task: employee validation started");

                List<Employee> employees = employeeService.getAllEmployees();
                int total = employees.size();

                if (total == 0) {
                    logger.info("Task: employee list is empty");
                    updateProgress(0, 1);
                    updateMessage("Немає записів для перевірки");
                    return employees;
                }

                for (int i = 0; i < total; i++) {
                    Employee employee = employees.get(i);

                    if (employee.getName() == null || employee.getName().trim().isEmpty()) {
                        logger.warning("Task: empty employee name, id=" + employee.getId());
                    }

                    if (employee.getPosition() == null || employee.getPosition().trim().isEmpty()) {
                        logger.warning("Task: empty employee position, id=" + employee.getId());
                    }

                    if (employee.getSalary() <= 0) {
                        logger.warning("Task: invalid employee salary, id=" + employee.getId());
                    }

                    logger.info("Task: checked employee, id=" + employee.getId());

                    Thread.sleep(500);

                    updateProgress(i + 1, total);
                    updateMessage("Перевірено " + (i + 1) + " із " + total);
                }

                logger.info("Task: employee validation finished");

                return employees;
            }
        };
    }

    private void validateEmployees() {
        logger.info("Background employee validation started by user");

        Task<List<Employee>> task = createValidationTask();

        progressBar.progressProperty().unbind();
        statusLabel.textProperty().unbind();

        progressBar.setProgress(0);
        statusLabel.setText("Початок перевірки");

        progressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(e -> {
            progressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();

            tableView.setItems(FXCollections.observableArrayList(task.getValue()));

            progressBar.setProgress(1);
            statusLabel.setText("Перевірка завершена");

            logger.info("Background employee validation completed successfully");
        });

        task.setOnFailed(e -> {
            progressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();

            progressBar.setProgress(0);
            statusLabel.setText("Помилка перевірки");

            logger.severe("Background validation error");
            showError("Помилка під час фонової перевірки");
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void clearFields() {
        nameField.clear();
        positionField.clear();
        salaryField.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}