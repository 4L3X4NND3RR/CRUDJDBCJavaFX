package com.alexander.model;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Product {
    private final SimpleIntegerProperty idProduct;
    private final SimpleStringProperty nameProduct;
    private final SimpleIntegerProperty idCategory;
    private final SimpleIntegerProperty stock;
    private final SimpleFloatProperty price;

    public Product() {
        idProduct = new SimpleIntegerProperty();
        nameProduct = new SimpleStringProperty();
        idCategory = new SimpleIntegerProperty();
        stock = new SimpleIntegerProperty();
        price = new SimpleFloatProperty();
    }

    public int getIdProduct() {
        return idProduct.get();
    }

    public SimpleIntegerProperty idProductProperty() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct.set(idProduct);
    }

    public String getNameProduct() {
        return nameProduct.get();
    }

    public SimpleStringProperty nameProductProperty() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct.set(nameProduct);
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

    public int getStock() {
        return stock.get();
    }

    public SimpleIntegerProperty stockProperty() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public float getPrice() {
        return price.get();
    }

    public SimpleFloatProperty priceProperty() {
        return price;
    }

    public void setPrice(float price) {
        this.price.set(price);
    }

    @Override
    public String toString() {
        return "Product{" +
                "idProduct=" + idProduct +
                ", nameProduct=" + nameProduct +
                ", idCategory=" + idCategory +
                ", stock=" + stock +
                ", price=" + price +
                '}';
    }
}
