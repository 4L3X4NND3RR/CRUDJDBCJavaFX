package com.alexander.util;

import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class Utilities {

    public boolean isNotEmpty(FilteredList<Node> nodes) {
        for (Node node : nodes) {
            TextField textField = (TextField) node;
            if (textField.getText().isEmpty() && !textField.getId().equals("txtId")){
                return false;
            }
        }
        return true;
    }

    public void setEditColumns(TableView tableView, Consumer<Integer> action) {
        TableColumn<?, Void> updateColumn = new TableColumn<>("Editar");
        String urlUpdate = getClass().getResource("../images/edit.png").toExternalForm();
        updateColumn.setCellFactory(c -> new ActionTableCell<>(urlUpdate, action));
        updateColumn.prefWidthProperty().bind(tableView.prefWidthProperty().multiply(0.1));
        tableView.getColumns().add(updateColumn);
    }

    public void setDeleteColumns(TableView tableView, Consumer<Integer> action) {
        TableColumn<?, Void> deleteColumn = new TableColumn<>("Eliminar");
        String urlDelete = getClass().getResource("../images/delete.png").toExternalForm();
        deleteColumn.setCellFactory(c -> new ActionTableCell<>(urlDelete, action));
        deleteColumn.prefWidthProperty().bind(tableView.prefWidthProperty().multiply(0.1));
        tableView.getColumns().add(deleteColumn);
    }
}
