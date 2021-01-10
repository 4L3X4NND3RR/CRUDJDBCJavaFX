package com.alexander.dao;

import com.alexander.idao.IProduct;
import com.alexander.model.Product;
import com.alexander.util.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductImpl implements IProduct {
    private final ConnectionManager connectionManager;
    private Statement stmt;
    private PreparedStatement psst;
    private ResultSet rs;
    private Product currentProduct;
    private int generatedId;

    public ProductImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public ObservableList<Product> searchProduct(String keyword) throws SQLException {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT * FROM product WHERE name_product LIKE ?";
        psst = connectionManager.getConnection().prepareStatement(sql);
        psst.setString(1, keyword + "%");
        return readProducts(products);
    }

    @Override
    public ObservableList<Product> getProducts(int initPage, int sizePage) throws SQLException {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT * FROM product LIMIT ? OFFSET ?";
        psst = connectionManager.getConnection().prepareStatement(sql);
        psst.setInt(1, sizePage);
        psst.setInt(2, initPage);
        return readProducts(products);
    }

    @Override
    public Boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE product SET name_product=?, category_id=?, stock=?, price=? WHERE id_product=?";
        psst = connectionManager.getConnection().prepareStatement(sql);
        psst.setString(1, product.getNameProduct());
        psst.setInt(2, product.getIdCategory());
        psst.setInt(3, product.getStock());
        psst.setFloat(4, product.getPrice());
        psst.setInt(5, product.getIdProduct());
        return !psst.execute();
    }

    @Override
    public Boolean createProduct(Product product) throws SQLException {
        boolean band;
        String sql = "INSERT INTO product(name_product, category_id, stock, price) VALUES(?, ?, ?, ?)";
        psst = connectionManager.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        psst.setString(1, product.getNameProduct());
        psst.setInt(2, product.getIdCategory());
        psst.setInt(3, product.getStock());
        psst.setFloat(4, product.getPrice());
        band = !psst.execute();
        rs = psst.getGeneratedKeys();
        if (rs.next()) {
            generatedId = rs.getInt(1);
        }
        return band;
    }

    @Override
    public Boolean deleteProduct(Product product) throws SQLException {
        String sql = "DELETE FROM product WHERE id_product = ?";
        psst = connectionManager.getConnection().prepareStatement(sql);
        psst.setInt(1, product.getIdProduct());
        return !psst.execute();
    }

    public Integer getNFilas() throws SQLException {
        int nFilas = -1;
        stmt = connectionManager.getConnection().createStatement();
        rs = stmt.executeQuery("SELECT COUNT(1) FROM product");
        if (rs.next()) {
            nFilas = rs.getInt(1);
        }
        return nFilas;
    }

    public void closeConnections() {
        connectionManager.closeResultSet(rs);
        connectionManager.closeStatement(stmt);
        connectionManager.closePreparedStatement(psst);
    }

    private ObservableList<Product> readProducts(ObservableList<Product> products) throws SQLException {
        rs = psst.executeQuery();
        while (rs.next()) {
            Product product = new Product();
            product.setIdProduct(rs.getInt("id_product"));
            product.setNameProduct(rs.getString("name_product"));
            product.setIdCategory(rs.getInt("category_id"));
            product.setStock(rs.getInt("stock"));
            product.setPrice(rs.getFloat("price"));
            products.add(product);
        }
        return products;
    }

    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

    public int getGeneratedId() {
        return generatedId;
    }
}
