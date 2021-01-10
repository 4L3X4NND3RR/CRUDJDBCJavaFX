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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CategoryRegisterController {

    @FXML
    public AnchorPane pane;
    @FXML
    public TextField txtId;
    @FXML
    public TextField txtNameCategory;
    @FXML
    public ProgressIndicator pIndicator;

    private CategoryImpl categoryModel;
    private ObservableList<Category> categories;
    private Method method;
    private final Utilities utilities;

    public CategoryRegisterController() {
        utilities = new Utilities();
    }

    public void setCategoryModel(CategoryImpl categoryModel, ObservableList<Category> categories, Method method) {
        this.categoryModel = categoryModel;
        this.categories = categories;
        this.method = method;
        if (method == Method.UPDATE){
            Category category = categoryModel.getCurrentCategory();
            txtId.setText(""+category.getIdCategory());
            txtNameCategory.setText(category.getNameCategory());
        }
    }

    public void handlerBtnSalir() {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

    public void handlerBtnSave() {
        if (utilities.isNotEmpty(pane.getChildren().filtered(node -> node instanceof TextField))) {
            Category category = new Category();
            category.setNameCategory(txtNameCategory.getText());
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws SQLException {
                    switch (method) {
                        case UPDATE:
                            return categoryModel.updateCategory(category);
                        case CREATE:
                            return categoryModel.createCategory(category);
                        default:
                            return false;
                    }
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    categoryModel.closeConnections();
                    if (getValue()) {
                        String content;
                        if (method == Method.UPDATE){
                            content = "Categoria actualizada exitosamente.";
                            for (Category c : categories) {
                                if (c.getIdCategory() == category.getIdCategory()){
                                    c.setIdCategory(category.getIdCategory());
                                    c.setNameCategory(category.getNameCategory());
                                    break;
                                }
                            }
                        }else {
                            content = "Categoria registrada exitosamente.";
                            category.setIdCategory(categoryModel.getGeneratedId());
                            categories.add(category);
                        }
                        MessageAlert.showInfromationAlert(pane.getScene().getWindow(), "Información", content);
                        Stage stage = (Stage) pane.getScene().getWindow();
                        stage.close();
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    categoryModel.closeConnections();
                    SQLException e = (SQLException) getException();
                    MessageAlert.showAlertError(pane.getScene().getWindow(),"Error", "Algo salio mal con la base de datos", e);
                }
            };

            BooleanBinding binding = task.stateProperty().isEqualTo(Task.State.RUNNING);
            pIndicator.visibleProperty().bind(binding);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        } else {
            MessageAlert.showInfromationAlert(pane.getScene().getWindow(), "Información", "Debe rellenar todos los campos.");
        }
    }
}
