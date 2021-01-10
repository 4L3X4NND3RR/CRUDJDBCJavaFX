package com.alexander.dao;

import com.alexander.idao.ICategory;
import com.alexander.model.Category;
import com.alexander.util.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CategoryImpl implements ICategory {
    private final ConnectionManager connectionManager;
    private Statement stmt;
    private PreparedStatement psst;
    private ResultSet rs;
    private Category currentCategory;
    private int generatedId;

    public CategoryImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public ObservableList<Category> searchCategory(String keyword) throws SQLException {
        ObservableList<Category> categories = FXCollections.observableArrayList();
        String sql = "SELECT * FROM category WHERE name_category LIKE ?";
        psst = connectionManager.getConnection().prepareStatement(sql);
        psst.setString(1, keyword + "%");
        rs = psst.executeQuery();
        return readCategories(categories, rs);
    }

    @Override
    public ObservableList<Category> getCategories() throws SQLException {
        ObservableList<Category> categories = FXCollections.observableArrayList();
        String sql = "SELECT * FROM category";
        stmt = connectionManager.getConnection().createStatement();
        rs = stmt.executeQuery(sql);
        return readCategories(categories, rs);
    }

    @Override
    public Boolean updateCategory(Category category) throws SQLException {
        String sql = "UPDATE category SET name_category=? WHERE id_category=?";
        psst = connectionManager.getConnection().prepareStatement(sql);
        psst.setString(1, category.getNameCategory());
        psst.setInt(2, category.getIdCategory());
        return !psst.execute();
    }

    @Override
    public Boolean createCategory(Category category) throws SQLException {
        boolean band;
        String sql = "INSERT INTO category(name_category) VALUES(?)";
        psst = connectionManager.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        psst.setString(1, category.getNameCategory());
        band = !psst.execute();
        rs = psst.getGeneratedKeys();
        if (rs.next()){
            generatedId = rs.getInt(1);
        }
        return band;
    }

    @Override
    public Boolean deleteCategory(Category category) throws SQLException {
        String sql = "DELETE FROM category WHERE id_category=?";
        psst = connectionManager.getConnection().prepareStatement(sql);
        psst.setInt(1, category.getIdCategory());
        return !psst.execute();
    }

    private ObservableList<Category> readCategories(ObservableList<Category> categories, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Category category = new Category();
            category.setIdCategory(rs.getInt("id_category"));
            category.setNameCategory(rs.getString("name_category"));
            categories.add(category);
        }
        return categories;
    }

    public void closeConnections() {
        connectionManager.closeResultSet(rs);
        connectionManager.closeStatement(stmt);
        connectionManager.closePreparedStatement(psst);
    }

    public Category getCurrentCategory() {
        return currentCategory;
    }

    public void setCurrentCategory(Category currentCategory) {
        this.currentCategory = currentCategory;
    }

    public int getGeneratedId() {
        return generatedId;
    }
}
