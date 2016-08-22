/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.graphic;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.shape.Polygon;

public final class ToggleGraphics {

    private ToggleGraphics() {
        // NOP
    }

    public static Node getGraphic(double width, double height, boolean status, String text, String styleClass) {
        double width1 = 0.5 * width;
        double width2 = 0.5 * width;
        if (status) {
            width1 += 0.1 * width;
        } else {
            width2 -= 0.1 * width;
        }

        Polygon polygon1 = new Polygon();
        polygon1.getPoints().addAll(0.0, 0.0);
        polygon1.getPoints().addAll(width1, 0.0);
        polygon1.getPoints().addAll(width2, height);
        polygon1.getPoints().addAll(0.0, height);

        Polygon polygon2 = new Polygon();
        polygon2.getPoints().addAll(width1, 0.0);
        polygon2.getPoints().addAll(width, 0.0);
        polygon2.getPoints().addAll(width, height);
        polygon2.getPoints().addAll(width2, height);

        Label label = new Label(text);
        label.setLayoutX((status ? 0.2 : 0.7) * width);
        label.setLayoutY(0.1 * height);

        if (styleClass != null) {
            polygon1.getStyleClass().add(styleClass);
            polygon2.getStyleClass().add(styleClass);
            label.getStyleClass().add(styleClass);
        }

        if (status) {
            polygon2.setStyle("-fx-fill: transparent");
        } else {
            polygon1.setStyle("-fx-fill: transparent");
        }

        Group group = new Group();
        group.getChildren().addAll(polygon1, polygon2, label);
        return group;
    }
}
