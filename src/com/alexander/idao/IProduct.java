package com.alexander.idao;

import com.alexander.model.Product;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public interface IProduct {
    ObservableList<Product> searchProduct(String keyword) throws SQLException;
    ObservableList<Product> getProducts(int initPage, int sizePage) throws SQLException;
    Boolean updateProduct(Product product) throws SQLException;
    Boolean createProduct(Product product) throws SQLException;
    Boolean deleteProduct(Product product) throws SQLException;
}
