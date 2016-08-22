/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.menu;

import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.transform.Affine;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class QEFXCloseMenuItem<K> extends QEFXMenuItem<K> {

    private static final double GRAPHIC_SIZE = 19.0;
    private static final String GRAPHIC_CLASS = "picblack-button";

    private static final double SQUARE_SIZE = 34.0;
    private static final double EDGE_SIZE = 12.0;

    private DoubleProperty x;
    private DoubleProperty y;

    private Node closeGraphic;
    private Path closeShape;

    public QEFXCloseMenuItem() {
        super(null);

        this.x = null;
        this.y = null;

        this.createCloseGraphic();
        this.createCloseShape();

        this.setOnMenuItemSelected(key -> {
            this.updateCloseColor();
        });

        this.getChildren().add(this.closeShape);
        this.getChildren().add(this.closeGraphic);
    }

    public QEFXCloseMenuItem(double x, double y) {
        this();

        this.setX(x);
        this.setY(y);
    }

    public DoubleProperty xProperty() {
        if (this.x == null) {
            this.x = new SimpleDoubleProperty(0.0);
            this.x.addListener(o -> {
                this.updateCloseGraphic();
                this.updateCloseShape();
            });
        }

        return this.x;
    }

    public void setX(double value) {
        this.xProperty().set(value);
    }

    public double getX() {
        return this.xProperty().get();
    }

    public DoubleProperty yProperty() {
        if (this.y == null) {
            this.y = new SimpleDoubleProperty(0.0);
            this.y.addListener(o -> {
                this.updateCloseGraphic();
                this.updateCloseShape();
            });
        }

        return this.y;
    }

    public void setY(double value) {
        this.yProperty().set(value);
    }

    public double getY() {
        return this.yProperty().get();
    }

    private void createCloseGraphic() {
        this.closeGraphic = SVGLibrary.getGraphic(SVGData.CLOSE, GRAPHIC_SIZE, null, GRAPHIC_CLASS);

        this.updateCloseGraphic();
    }

    private void createCloseShape() {
        this.closeShape = new Path();
        this.closeShape.getStyleClass().add("close-menu-item");

        this.updateCloseColor();
        this.updateCloseShape();
    }

    private void updateCloseColor() {
        if (this.selected) {
            this.closeShape.setStyle("-fx-fill: -fx-fill-focused");
        } else {
            this.closeShape.setStyle("");
        }
    }

    private void updateCloseGraphic() {
        double x0 = this.getX() + 0.5 * SQUARE_SIZE - 0.5 * GRAPHIC_SIZE;
        double y0 = this.getY() + 0.5 * SQUARE_SIZE - 0.5 * GRAPHIC_SIZE;

        Affine affine = new Affine();
        affine.prependTranslation(x0, y0);
        this.closeGraphic.getTransforms().clear();
        this.closeGraphic.getTransforms().add(affine);
    }

    private void updateCloseShape() {
        double x0 = this.getX();
        double y0 = this.getY();
        double x1 = x0 + EDGE_SIZE;
        double y1 = y0;
        double x2 = x0 + SQUARE_SIZE - EDGE_SIZE;
        double y2 = y0;
        double x3 = x0 + SQUARE_SIZE;
        double y3 = y0 + EDGE_SIZE;
        double x4 = x0 + SQUARE_SIZE;
        double y4 = y0 + SQUARE_SIZE - EDGE_SIZE;
        double x5 = x0 + SQUARE_SIZE - EDGE_SIZE;
        double y5 = y0 + SQUARE_SIZE;
        double x6 = x0 + EDGE_SIZE;
        double y6 = y0 + SQUARE_SIZE;
        double x7 = x0;
        double y7 = y0 + SQUARE_SIZE - EDGE_SIZE;
        double x8 = x0;
        double y8 = y0 + EDGE_SIZE;

        MoveTo moveTo = new MoveTo();
        moveTo.setX(x1);
        moveTo.setY(y1);

        LineTo lineTo1 = new LineTo();
        lineTo1.setX(x2);
        lineTo1.setY(y2);

        ArcTo arcTo1 = new ArcTo();
        arcTo1.setX(x3);
        arcTo1.setY(y3);
        arcTo1.setRadiusX(EDGE_SIZE);
        arcTo1.setRadiusY(EDGE_SIZE);
        arcTo1.setSweepFlag(true);

        LineTo lineTo2 = new LineTo();
        lineTo2.setX(x4);
        lineTo2.setY(y4);

        ArcTo arcTo2 = new ArcTo();
        arcTo2.setX(x5);
        arcTo2.setY(y5);
        arcTo2.setRadiusX(EDGE_SIZE);
        arcTo2.setRadiusY(EDGE_SIZE);
        arcTo2.setSweepFlag(true);

        LineTo lineTo3 = new LineTo();
        lineTo3.setX(x6);
        lineTo3.setY(y6);

        ArcTo arcTo3 = new ArcTo();
        arcTo3.setX(x7);
        arcTo3.setY(y7);
        arcTo3.setRadiusX(EDGE_SIZE);
        arcTo3.setRadiusY(EDGE_SIZE);
        arcTo3.setSweepFlag(true);

        LineTo lineTo4 = new LineTo();
        lineTo4.setX(x8);
        lineTo4.setY(y8);

        ArcTo arcTo4 = new ArcTo();
        arcTo4.setX(x1);
        arcTo4.setY(y1);
        arcTo4.setRadiusX(EDGE_SIZE);
        arcTo4.setRadiusY(EDGE_SIZE);
        arcTo4.setSweepFlag(true);

        List<PathElement> elements = this.closeShape.getElements();
        elements.clear();
        elements.add(moveTo);
        elements.add(lineTo1);
        elements.add(arcTo1);
        elements.add(lineTo2);
        elements.add(arcTo2);
        elements.add(lineTo3);
        elements.add(arcTo3);
        elements.add(lineTo4);
        elements.add(arcTo4);
    }
}
