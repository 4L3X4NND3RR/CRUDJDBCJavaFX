package com.alexander.util;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;

import java.util.function.Consumer;

public class ActionTableCell<S> extends TableCell<S, Void> {
    private final Button button;

    public ActionTableCell(String url, Consumer<Integer> action) {
        button = new Button();
        button.setGraphic(new ImageView(url));
        button.setOnAction(e -> action.accept(getIndex()));
        setAlignment(Pos.CENTER);
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        setGraphic(null);
        if (!empty) {
            setGraphic(button);
        }
    }
}
