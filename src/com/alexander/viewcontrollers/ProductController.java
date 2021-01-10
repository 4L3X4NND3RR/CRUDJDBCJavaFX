package com.alexander.viewcontrollers;

import com.alexander.dao.ProductImpl;
import com.alexander.model.Product;
import com.alexander.util.MessageAlert;
import com.alexander.util.Method;
import com.alexander.util.Utilities;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.SQLException;

public class ProductController {
    @FXML
    private AnchorPane pane;
    @FXML
    private ButtonBar btnBar;
    @FXML
    private TextField txtName;
    @FXML
    private TableView<Product> tblProduct;
    @FXML
    private ProgressIndicator pIndicator;

    private final ProductImpl productModel;
    private final Utilities utilities;
    private int pagina;

    public ProductController(ProductImpl productModel) {
        this.productModel = productModel;
        utilities = new Utilities();
    }

    public void initialize() {
        createTable();
        pagina = 1;
        createPagination(pagina);
    }

    private void createTable() {
        TableColumn<Product, Integer> idColumn = new TableColumn<>("Id");
        TableColumn<Product, String> nameColumn = new TableColumn<>("Nombre");
        TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock");
        TableColumn<Product, Float> priceColumn = new TableColumn<>("Precio");

        idColumn.setCellValueFactory(c -> c.getValue().idProductProperty().asObject());
        nameColumn.setCellValueFactory(c -> c.getValue().nameProductProperty());
        stockColumn.setCellValueFactory(c -> c.getValue().stockProperty().asObject());
        priceColumn.setCellValueFactory(c -> c.getValue().priceProperty().asObject());
        tblProduct.getColumns().add(idColumn);
        tblProduct.getColumns().add(nameColumn);
        tblProduct.getColumns().add(stockColumn);
        tblProduct.getColumns().add(priceColumn);
        utilities.setEditColumns(tblProduct, this::handlerEditProduct);
        utilities.setDeleteColumns(tblProduct, this::handlerDeleteProduct);
        idColumn.prefWidthProperty().bind(tblProduct.prefWidthProperty().multiply(0.1));
        nameColumn.prefWidthProperty().bind(tblProduct.prefWidthProperty().multiply(0.3));
        stockColumn.prefWidthProperty().bind(tblProduct.prefWidthProperty().multiply(0.2));
        priceColumn.prefWidthProperty().bind(tblProduct.prefWidthProperty().multiply(0.2));
    }

    private void createPagination(int pagina) {
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return productModel.getNFilas();
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                int nFilas = getValue();
                if (getValue() > 0) {
                    btnBar.getButtons().clear();
                    int tamanioPagina = 5;
                    int totalPaginas = (nFilas % tamanioPagina) == 0 ? (nFilas / tamanioPagina) : (nFilas / tamanioPagina) + 1;
                    int paginaInicio = (pagina - 1) * tamanioPagina;
                    for (int i = 1; i <= totalPaginas; i++) {
                        Hyperlink hyperlink = new Hyperlink("" + i);
                        if (i == pagina) hyperlink.setVisited(true);
                        hyperlink.setOnAction(e -> handlerPage(e));
                        btnBar.getButtons().add(hyperlink);
                    }
                    loadTableView(paginaInicio, tamanioPagina);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadTableView(String keyword) {
        Task<ObservableList<Product>> task = new Task<ObservableList<Product>>() {
            @Override
            protected ObservableList<Product> call() throws SQLException {
                return productModel.searchProduct(keyword);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                productModel.closeConnections();
                tblProduct.setItems(getValue());
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
        txtName.disableProperty().bind(binding);
        pIndicator.visibleProperty().bind(binding);

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadTableView(int paginaInicio, int tamanioPagina) {
        Task<ObservableList<Product>> task = new Task<ObservableList<Product>>() {
            @Override
            protected ObservableList<Product> call() throws SQLException {
                return productModel.getProducts(paginaInicio, tamanioPagina);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                productModel.closeConnections();
                tblProduct.setItems(getValue());
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
        txtName.disableProperty().bind(binding);
        pIndicator.visibleProperty().bind(binding);

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void handlerPage(Event event) {
        Hyperlink hyperlink = (Hyperlink) event.getSource();
        int newPage = Integer.parseInt(hyperlink.getText());
        if (newPage != pagina) {
            pagina = newPage;
            createPagination(pagina);
        }
    }

    public void txtBuscarKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (txtName.getText().isEmpty()) {
                pagina = 1;
                loadTableView(pagina, 5);
            } else {
                loadTableView(txtName.getText());
            }
        }
    }

    public void handlerBtnAddProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/ProductRegister.fxml"));
            Parent root = loader.load();
            ProductRegisterController prController = loader.getController();
            prController.setProductModel(productModel, tblProduct.getItems(), Method.CREATE);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Agregar producto");
            stage.initOwner(pane.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            MessageAlert.showAlertError(pane.getScene().getWindow(), "Error", "Algo salio mal al cargar la ventana.", e);
        }
    }

    private void handlerEditProduct(int index) {
        productModel.setCurrentProduct(tblProduct.getItems().get(index));
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/ProductRegister.fxml"));
            Parent root = loader.load();
            ProductRegisterController prController = loader.getController();
            prController.setProductModel(productModel, tblProduct.getItems(), Method.UPDATE);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Editar producto");
            stage.initOwner(pane.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            MessageAlert.showAlertError(pane.getScene().getWindow(),"Error", "Algo salio mal al cargar la ventana.", e);
        }
    }

    private void handlerDeleteProduct(int index) {
        if (MessageAlert.showAlertConfirm(pane.getScene().getWindow(),"Información", "¿Estas seguro de eliminar este producto?")) {
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    Product product = tblProduct.getItems().get(index);
                    return productModel.deleteProduct(product);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    productModel.closeConnections();
                    if (getValue()) {
                        tblProduct.getItems().remove(index);
                        MessageAlert.showInfromationAlert(pane.getScene().getWindow(),"Información", "Producto eliminado exitosamente.");
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    productModel.closeConnections();
                    SQLException e = (SQLException) getException();
                    MessageAlert.showAlertError(pane.getScene().getWindow(),"Error", "Algo salio mal con la base de datos", e);
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
