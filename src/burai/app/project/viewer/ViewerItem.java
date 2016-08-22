/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class ViewerItem extends BorderPane {

    private static final double GRAPHIC_SIZE = 32.0;
    private static final String GRAPHIC_CLASS = "teeth-menu-icon";

    private static final double CAPTION_MARGIN = 16.0;

    private static final double SHADOW_RADIUS = 2.0;
    private static final double SHADOW_LAYOUT_X = 0.0;
    private static final double SHADOW_LAYOUT_Y = 3.0;

    public ViewerItem(SVGData svgData, String message) {
        super();

        if (svgData == null) {
            throw new IllegalArgumentException("svgData is null");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("message is empty.");
        }

        Node icon = SVGLibrary.getGraphic(svgData, GRAPHIC_SIZE, null, GRAPHIC_CLASS);
        BorderPane.setAlignment(icon, Pos.CENTER);

        Label caption = new Label(message);
        caption.getStyleClass().add(GRAPHIC_CLASS);
        BorderPane.setAlignment(caption, Pos.CENTER_LEFT);
        BorderPane.setMargin(caption, new Insets(0.0, 0.0, 0.0, CAPTION_MARGIN));

        this.setCenter(icon);
        this.setRight(caption);
        this.setLayoutX(0.0);
        this.setLayoutY(-0.5 * GRAPHIC_SIZE);
        this.setEffect(new DropShadow(SHADOW_RADIUS, SHADOW_LAYOUT_X, SHADOW_LAYOUT_Y, Color.BLACK));
    }
}
