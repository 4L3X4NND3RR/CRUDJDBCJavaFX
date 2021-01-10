package com.alexander.viewcontrollers;

import com.alexander.dao.CategoryImpl;
import com.alexander.dao.ProductImpl;
import com.alexander.util.ConnectionManager;
import com.alexander.util.MessageAlert;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    @FXML
    public BorderPane mainPanel;
    private Parent root;
    private ConnectionManager connectionManager;

    public void initialize() {
        try {
            root = FXMLLoader.load(getClass().getResource("../views/Home.fxml"));
            root.setId("Home");
            mainPanel.setCenter(root);
            connectionManager = new ConnectionManager();
        } catch (IOException e) {
            MessageAlert.showAlertError(mainPanel.getScene().getWindow(), "Error", "Algo salio mal al cargar la ventana.", e);
        }
    }

    public void handlerMenuItemSalir() {
        Platform.exit();
    }

    public void handlerBtnHome() {
        if (notExist("Home")) {
            HomeController homeController = new HomeController();
            loadWindow("Home", homeController);
        }
    }

    public void handlerBtnProduct() {
        if (notExist("Product")) {
            ProductImpl productDataSource = new ProductImpl(connectionManager);
            ProductController productController = new ProductController(productDataSource);
            loadWindow("Product", productController);
        }
    }

    public void handlerBtnCategory() {
        if (notExist("Category")) {
            CategoryImpl categoryModel = new CategoryImpl(connectionManager);
            CategoryController categoryController = new CategoryController(categoryModel);
            loadWindow("Category", categoryController);
        }
    }

    private boolean notExist(String id) {
        return !id.equals(mainPanel.getCenter().getId());
    }

    private void loadWindow(String name, Object controller) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../views/" + name + ".fxml"));
            fxmlLoader.setController(controller);
            root = fxmlLoader.load();
            root.setId(name);
            mainPanel.setCenter(root);
        } catch (IOException e) {
            MessageAlert.showAlertError(mainPanel.getScene().getWindow(), "Error", "Algo salio mal al cargar la ventana.", e);
        }
    }
}
