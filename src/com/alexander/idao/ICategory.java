package com.alexander.idao;

import com.alexander.model.Category;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public interface ICategory {
    ObservableList<Category> searchCategory(String keyword) throws SQLException;
    ObservableList<Category> getCategories() throws SQLException;
    Boolean updateCategory(Category category) throws SQLException;
    Boolean createCategory(Category category) throws SQLException;
    Boolean deleteCategory(Category category) throws SQLException;
}
