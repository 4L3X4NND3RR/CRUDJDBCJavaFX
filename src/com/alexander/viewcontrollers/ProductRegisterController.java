package com.alexander.viewcontrollers;

import com.alexander.dao.CategoryImpl;
import com.alexander.dao.ProductImpl;
import com.alexander.model.Category;
import com.alexander.model.Product;
import com.alexander.util.ConnectionManager;
import com.alexander.util.MessageAlert;
import com.alexander.util.Method;
import com.alexander.util.Utilities;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ProductRegisterController {
    @FXML
    private GridPane pane;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;
    @FXML
    private ComboBox<Category> cmbCategoria;
    @FXML
    private ProgressIndicator pIndicator;

    private ProductImpl productModel;
    private ObservableList<Product> products;
    private Method method;
    private final Utilities utilities;
    private final CategoryImpl categoryModel;
    private final ConnectionManager connectionManager;

    public ProductRegisterController() {
        utilities = new Utilities();
        connectionManager = new ConnectionManager();
        categoryModel = new CategoryImpl(connectionManager);
    }

    public void initialize() {
        loadCategories();
    }

    public void setProductModel(ProductImpl productModel, ObservableList<Product> products, Method method) {
        this.productModel = productModel;
        this.products = products;
        this.method = method;
    }

    private void loadCategories() {
        Task<ObservableList<Category>> task = new Task<ObservableList<Category>>() {
            @Override
            protected ObservableList<Category> call() throws SQLException {
                return categoryModel.getCategories();
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                productModel.closeConnections();
                cmbCategoria.setItems(getValue());
                if (method == Method.UPDATE) {
                    initData();
                }
            }
            @Override
            protected void failed() {
                super.failed();
                productModel.closeConnections();
                SQLException e = (SQLException) getException();
                MessageAlert.showAlertError(pane.getScene().getWindow(), "Error", "Algo salio mal con la base de datos", e);
            }

            private void initData() {
                Product product = productModel.getCurrentProduct();
                txtId.setText(""+product.getIdProduct());
                txtNombre.setText(product.getNameProduct());
                Category category = cmbCategoria.getItems().filtered(c -> c.getIdCategory() == product.getIdCategory()).get(0);
                cmbCategoria.setValue(category);
                txtStock.setText(""+product.getStock());
                txtPrecio.setText(""+product.getPrice());
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void handlerBtnSave() {
        if (utilities.isNotEmpty(pane.getChildren().filtered(node -> node instanceof TextField)) && cmbCategoria.getValue() != null) {
            Product product = new Product();
            product.setNameProduct(txtNombre.getText());
            product.setIdCategory(cmbCategoria.getValue().getIdCategory());
            product.setStock(Integer.parseInt(txtStock.getText()));
            product.setPrice(Float.parseFloat(txtPrecio.getText()));
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws SQLException {
                    switch (method) {
                        case UPDATE:
                            return productModel.updateProduct(product);
                        case CREATE:
                            return productModel.createProduct(product);
                        default:
                            return false;
                    }
                }
                @Override
                protected void succeeded() {
                    super.succeeded();
                    productModel.closeConnections();
                    if (getValue()) {
                        String content;
                        if (method == Method.UPDATE){
                            content = "Producto actualizado exitosamente.";
                            for (Product p : products) {
                                if (p.getIdProduct() == product.getIdProduct()){
                                    p.setIdProduct(product.getIdProduct());
                                    p.setNameProduct(product.getNameProduct());
                                    p.setIdCategory(product.getIdCategory());
                                    p.setStock(product.getStock());
                                    p.setPrice(product.getPrice());
                                    break;
                                }
                            }
                        }else {
                            content = "Producto registrado exitosamente.";
                            product.setIdProduct(productModel.getGeneratedId());
                            products.add(product);
                        }
                        MessageAlert.showInfromationAlert(pane.getScene().getWindow(), "Información", content);
                        Stage stage = (Stage) pane.getScene().getWindow();
                        stage.close();
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    productModel.closeConnections();
                    SQLException e = (SQLException) getException();
                    MessageAlert.showAlertError(pane.getScene().getWindow(), "Error", "Algo salio mal con la base de datos", e);
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

    public void handlerBtnSalir() {
        connectionManager.closeConnection();
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }
}
