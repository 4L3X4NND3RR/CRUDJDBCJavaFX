package com.alexander.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Category {
    private final SimpleIntegerProperty idCategory;
    private final SimpleStringProperty nameCategory;

    public Category() {
        idCategory = new SimpleIntegerProperty();
        nameCategory = new SimpleStringProperty();
    }

    public int getIdCategory() {
        return idCategory.get();
    }

    public SimpleIntegerProperty idCategoryProperty() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory.set(idCategory);
    }

    public String getNameCategory() {
        return nameCategory.get();
    }

    public SimpleStringProperty nameCategoryProperty() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory.set(nameCategory);
    }

    @Override
    public String toString() {
        return nameCategory.get();
    }
}
