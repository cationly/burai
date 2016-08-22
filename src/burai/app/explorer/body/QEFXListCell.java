/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body;

import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import burai.app.explorer.body.contextmenu.QEFXContextMenu;
import burai.app.icon.QEFXIcon;

public class QEFXListCell extends ListCell<QEFXIcon> {

    private QEFXExplorerListView root;

    private double size;

    public QEFXListCell(QEFXExplorerListView root, double size) {
        if (root == null) {
            throw new IllegalArgumentException("root is null.");
        }

        if (size <= 0.0) {
            throw new IllegalArgumentException("size is not positive.");
        }

        this.root = root;
        this.size = size;
        this.setupMouseDoubleClicked();
    }

    private void setupMouseDoubleClicked() {
        this.setOnMouseClicked(event -> {
            if (event != null && MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() >= 2) {
                this.root.selectIcon(this.getItem());
            }
        });
    }

    @Override
    protected void updateItem(QEFXIcon icon, boolean empty) {
        super.updateItem(icon, empty);
        if (empty || icon == null) {
            this.setGraphic(null);
            this.setContextMenu(null);
            return;
        }

        BorderPane borderPane = new BorderPane();
        this.updateFigure(borderPane, icon);
        this.updateCaption(borderPane, icon);
        this.setGraphic(borderPane);

        ContextMenu contextMenu = QEFXContextMenu.getContextMenu(icon, this.root);
        this.setContextMenu(contextMenu);
    }

    private void updateFigure(BorderPane borderPane, QEFXIcon icon) {
        Node figure = icon.getFigure(this.size);
        if (figure == null) {
            figure = new Label("No Figure");
        }

        figure.getStyleClass().add("list-figure");
        BorderPane.setAlignment(figure, Pos.CENTER);
        borderPane.setLeft(figure);
    }

    private void updateCaption(BorderPane borderPane, QEFXIcon icon) {
        VBox vbox = new VBox();
        StringProperty captionProp = icon.captionProperty();
        StringProperty subCaptionProp = icon.subCaptionProperty();
        this.updateCaptionKernel(vbox, captionProp.get(), subCaptionProp.get());

        captionProp.addListener(o -> {
            this.updateCaptionKernel(vbox, captionProp.get(), subCaptionProp.get());
        });

        subCaptionProp.addListener(o -> {
            this.updateCaptionKernel(vbox, captionProp.get(), subCaptionProp.get());
        });

        HBox hbox = new HBox();
        hbox.getChildren().add(new Separator(Orientation.VERTICAL));
        hbox.getChildren().add(vbox);
        vbox.getStyleClass().add("list-caption");
        BorderPane.setAlignment(hbox, Pos.CENTER_LEFT);
        borderPane.setCenter(hbox);
    }

    private void updateCaptionKernel(VBox vbox, String captionStr, String subCaptionStr) {
        vbox.getChildren().clear();

        String captionStr2 = captionStr;
        if (captionStr2 == null || captionStr2.isEmpty()) {
            captionStr2 = "No Caption";
        }
        Label caption = new Label(captionStr2);
        caption.getStyleClass().add("list-caption-head");
        vbox.getChildren().add(caption);

        if (subCaptionStr != null && (!subCaptionStr.isEmpty())) {
            Label subCaption = new Label(subCaptionStr);
            vbox.getChildren().add(subCaption);
        }
    }
}
