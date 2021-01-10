package com.alexander.viewcontrollers;

import com.alexander.dao.CategoryImpl;
import com.alexander.model.Category;
import com.alexander.util.MessageAlert;
import com.alexander.util.Method;
import com.alexander.util.Utilities;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class CategoryController {
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField txtName;
    @FXML
    private TableView<Category> tblCategory;
    @FXML
    private ProgressIndicator pIndicator;

    private final CategoryImpl categoryModel;
    private final Utilities utilities;

    public CategoryController(CategoryImpl categoryModel) {
        this.categoryModel = categoryModel;
        utilities = new Utilities();
    }

    public void initialize() {
        createTable();
    }

    private void createTable() {
        TableColumn<Category, Integer> idColumn = new TableColumn<>("Id");
        TableColumn<Category, String> nameColumn = new TableColumn<>("Nombre categoria");

        idColumn.setCellValueFactory(c -> c.getValue().idCategoryProperty().asObject());
        nameColumn.setCellValueFactory(c -> c.getValue().nameCategoryProperty());

        tblCategory.getColumns().add(idColumn);
        tblCategory.getColumns().add(nameColumn);

        utilities.setEditColumns(tblCategory, this::handlerEditCategory);
        utilities.setDeleteColumns(tblCategory, this::handlerDeleteCategory);

        loadTableView(null);
    }

    private void loadTableView(String keyword) {
        Task<ObservableList<Category>> task = new Task<ObservableList<Category>>() {
            @Override
            protected ObservableList<Category> call() throws SQLException {
                if (keyword == null) {
                    return categoryModel.getCategories();
                }
                return categoryModel.searchCategory(keyword);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                categoryModel.closeConnections();
                tblCategory.setItems(getValue());
            }

            @Override
            protected void failed() {
                super.failed();
                categoryModel.closeConnections();
                SQLException e = (SQLException) getException();
                MessageAlert.showAlertError(pane.getScene().getWindow(), "Error", "Algo salio mal con la base de datos", e);
            }
        };
        BooleanBinding binding = task.stateProperty().isEqualTo(Task.State.RUNNING);
        txtName.disableProperty().bind(binding);
        pIndicator.visibleProperty().bind(binding);

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void txtBuscarKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (txtName.getText().isEmpty()) {
                loadTableView(null);
            } else {
                loadTableView(txtName.getText().trim());
            }
        }
    }

    public void handlerBtnAddCategory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/CategoryRegister.fxml"));
            Parent root = loader.load();
            CategoryRegisterController categoryRC = loader.getController();
            categoryRC.setCategoryModel(categoryModel, tblCategory.getItems(), Method.CREATE);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Agregar categoria");
            stage.initOwner(pane.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            MessageAlert.showAlertError(pane.getScene().getWindow(), "Error", "Algo salio mal al cargar la ventana.", e);
        }
    }

    private void handlerEditCategory(int index) {
        categoryModel.setCurrentCategory(tblCategory.getItems().get(index));
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/CategoryRegister.fxml"));
            Parent root = loader.load();
            CategoryRegisterController categoryRC = loader.getController();
            categoryRC.setCategoryModel(categoryModel, tblCategory.getItems(), Method.UPDATE);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Editar categoria");
            stage.initOwner(pane.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            MessageAlert.showAlertError(pane.getScene().getWindow(), "Error", "Algo salio mal al cargar la ventana.", e);
        }
    }

    private void handlerDeleteCategory(int index) {
        if (MessageAlert.showAlertConfirm(pane.getScene().getWindow(), "Información", "¿Estas seguro de eliminar esta categoria?")) {
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    Category category = tblCategory.getItems().get(index);
                    return categoryModel.deleteCategory(category);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    categoryModel.closeConnections();
                    if (getValue()) {
                        tblCategory.getItems().remove(index);
                        MessageAlert.showInfromationAlert(pane.getScene().getWindow(), "Información", "Categoria eliminada exitosamente.");
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    categoryModel.closeConnections();
                    SQLException e = (SQLException) getException();
                    MessageAlert.showAlertError(pane.getScene().getWindow(), "Error", "Algo salio mal con la base de datos", e);
                }
            };
            BooleanBinding binding = task.stateProperty().isEqualTo(Task.State.RUNNING);
            txtName.disableProperty().bind(binding);
            pIndicator.visibleProperty().bind(binding);

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }
}
