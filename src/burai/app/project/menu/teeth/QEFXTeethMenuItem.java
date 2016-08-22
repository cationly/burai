/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.menu.teeth;

import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.transform.Affine;
import burai.app.project.menu.QEFXMenuItem;

public class QEFXTeethMenuItem extends QEFXMenuItem<Node> {

    private static final double WIDTH_RANDOM = 0.02;

    private static final double GRAPHIC_MARGIN = 26.0;

    private static final double SELECTED_X_SHIFT = 5.0;

    private static final int BLUR_ITER = 2;
    private static final double BLUR_RADIUS = 1.7;

    private DoubleProperty width;
    private DoubleProperty height;
    private DoubleProperty x;
    private DoubleProperty y;

    private double random;

    private Path toothShape;

    public QEFXTeethMenuItem(Node graphic) {
        super(graphic);
        if (this.key == null) {
            this.key = new Group();
        }

        this.width = null;
        this.height = null;
        this.x = null;
        this.y = null;

        this.random = 2.0 * (Math.random() - 0.5);

        this.createToothShape();

        this.setOnMenuItemSelected(key -> {
            this.updateToothColor();
            this.updateGraphic();
        });

        this.getChildren().add(this.toothShape);
        this.getChildren().add(this.key);
    }

    public QEFXTeethMenuItem(Node graphic, double width, double height, double x, double y) {
        this(graphic);

        this.setWidth(width);
        this.setHeight(height);
        this.setX(x);
        this.setY(y);
    }

    public void showGraphic() {
        List<Node> children = this.getChildren();
        if (!children.contains(this.key)) {
            children.add(this.key);
        }
    }

    public void hideGraphic() {
        List<Node> children = this.getChildren();
        int index = children.indexOf(this.key);
        if (index > -1) {
            children.remove(index);
        }
    }

    public DoubleProperty widthProperty() {
        if (this.width == null) {
            this.width = new SimpleDoubleProperty(0.0);
            this.width.addListener(o -> {
                this.updateGraphic();
                this.updateToothShape();
            });
        }

        return this.width;
    }

    public void setWidth(double value) {
        this.widthProperty().set(value);
    }

    public double getWidth() {
        return this.widthProperty().get();
    }

    public double getMaxWidth() {
        return this.getWidth() * (1.0 + WIDTH_RANDOM);
    }

    public DoubleProperty heightProperty() {
        if (this.height == null) {
            this.height = new SimpleDoubleProperty(0.0);
            this.height.addListener(o -> {
                this.updateGraphic();
                this.updateToothShape();
            });
        }

        return this.height;
    }

    public void setHeight(double value) {
        this.heightProperty().set(value);
    }

    public double getHeight() {
        return this.heightProperty().get();
    }

    public DoubleProperty xProperty() {
        if (this.x == null) {
            this.x = new SimpleDoubleProperty(0.0);
            this.x.addListener(o -> {
                this.updateGraphic();
                this.updateToothShape();
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
                this.updateGraphic();
                this.updateToothShape();
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

    private void createToothShape() {
        this.toothShape = new Path();
        this.toothShape.getStyleClass().add("teeth-menu-item");
        this.toothShape.setEffect(new BoxBlur(BLUR_RADIUS, BLUR_RADIUS, BLUR_ITER));

        this.updateToothColor();
        this.updateToothShape();
    }

    private void updateToothColor() {
        if (this.selected) {
            this.toothShape.setStyle("-fx-fill: -fx-fill-focused");
        } else {
            this.toothShape.setStyle("");
        }
    }

    private void updateGraphic() {
        double x = this.getX();
        double y = this.getY();
        double h = this.getHeight();

        if (this.selected) {
            x += SELECTED_X_SHIFT;
        }

        double xMedium = x + GRAPHIC_MARGIN;
        double yMedium = y - 0.5 * h;

        Affine affine = new Affine();
        affine.prependTranslation(xMedium, yMedium);
        this.key.getTransforms().clear();
        this.key.getTransforms().add(affine);
    }

    private void updateToothShape() {
        double w = this.getWidth() * (1.0 + WIDTH_RANDOM * this.random);
        double h = this.getHeight();
        double x1 = w;
        double y1 = -h;
        double x2 = 0.0;
        double y2 = -h;
        double x3 = 0.0;
        double y3 = 0.0;
        double x4 = w;
        double y4 = 0.0;
        double radius = 0.5 * w;

        MoveTo moveTo = new MoveTo();
        moveTo.setX(x1);
        moveTo.setY(y1);

        LineTo lineTop = new LineTo();
        lineTop.setX(x2);
        lineTop.setY(y2);

        LineTo lineLeft = new LineTo();
        lineLeft.setX(x3);
        lineLeft.setY(y3);

        LineTo lineBottom = new LineTo();
        lineBottom.setX(x4);
        lineBottom.setY(y4);

        ArcTo arcRight = new ArcTo();
        arcRight.setX(x1);
        arcRight.setY(y1);
        arcRight.setRadiusX(radius);
        arcRight.setRadiusY(radius);
        arcRight.setSweepFlag(false);

        List<PathElement> elements = this.toothShape.getElements();
        elements.clear();
        elements.add(moveTo);
        elements.add(lineTop);
        elements.add(lineLeft);
        elements.add(lineBottom);
        elements.add(arcRight);

        Affine affine = new Affine();
        affine.prependTranslation(this.getX(), this.getY());
        this.toothShape.getTransforms().clear();
        this.toothShape.getTransforms().add(affine);
    }
}
