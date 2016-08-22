/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.menu.roll;

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

public class QEFXRollMenuItem extends QEFXMenuItem<Node> {

    private static final double MEDIUM_RATE = 0.525;
    private static final double GRAPHIC_RATE = 0.65;

    private static final double OFFSET_ANGLE = 90.0;
    private static final double ARROW_ANGLE = 8.0;

    private static final int BLUR_ITER = 2;
    private static final double BLUR_RADIUS = 1.7;

    private DoubleProperty radius;
    private DoubleProperty width;
    private DoubleProperty startAngle;
    private DoubleProperty endAngle;

    private Path rollShape;

    public QEFXRollMenuItem(Node graphic) {
        super(graphic);
        if (this.key == null) {
            this.key = new Group();
        }

        this.radius = null;
        this.width = null;
        this.startAngle = null;
        this.endAngle = null;

        this.createRollShape();

        this.setOnMenuItemSelected(key -> {
            this.updateRollColor();
        });

        this.getChildren().add(this.rollShape);
        this.getChildren().add(this.key);
    }

    public QEFXRollMenuItem(Node graphic, double radius, double width, double startAngle, double endAngle) {
        this(graphic);

        this.setRadius(radius);
        this.setWidth(width);
        this.setStartAngle(startAngle);
        this.setEndAngle(endAngle);
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

    public DoubleProperty radiusProperty() {
        if (this.radius == null) {
            this.radius = new SimpleDoubleProperty(0.0);
            this.radius.addListener(o -> {
                this.updateGraphic();
                this.updateRollShape();
            });
        }

        return this.radius;
    }

    public void setRadius(double value) {
        this.radiusProperty().set(value);
    }

    public double getRadius() {
        return this.radiusProperty().get();
    }

    public DoubleProperty widthProperty() {
        if (this.width == null) {
            this.width = new SimpleDoubleProperty(0.0);
            this.width.addListener(o -> {
                this.updateGraphic();
                this.updateRollShape();
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

    public DoubleProperty startAngleProperty() {
        if (this.startAngle == null) {
            this.startAngle = new SimpleDoubleProperty(0.0);
            this.startAngle.addListener(o -> {
                this.updateGraphic();
                this.updateRollShape();
            });
        }

        return this.startAngle;
    }

    public void setStartAngle(double value) {
        this.startAngleProperty().set(value);
    }

    public double getStartAngle() {
        return this.startAngleProperty().get();
    }

    public DoubleProperty endAngleProperty() {
        if (this.endAngle == null) {
            this.endAngle = new SimpleDoubleProperty(0.0);
            this.endAngle.addListener(o -> {
                this.updateGraphic();
                this.updateRollShape();
            });
        }

        return this.endAngle;
    }

    public void setEndAngle(double value) {
        this.endAngleProperty().set(value);
    }

    public double getEndAngle() {
        return this.endAngleProperty().get();
    }

    private void createRollShape() {
        this.rollShape = new Path();
        this.rollShape.getStyleClass().add("roll-menu-item");
        this.rollShape.setEffect(new BoxBlur(BLUR_RADIUS, BLUR_RADIUS, BLUR_ITER));

        this.updateRollColor();
        this.updateRollShape();
    }

    private void updateRollColor() {
        if (this.selected) {
            this.rollShape.setStyle("-fx-fill: -fx-fill-focused");
        } else {
            this.rollShape.setStyle("");
        }
    }

    private void updateGraphic() {
        double angle1 = this.getStartAngle();
        double angle2 = this.getEndAngle();

        double radiusMedium = Math.max(this.getRadius() + GRAPHIC_RATE * this.getWidth(), 0.0);
        double angleMedium = (OFFSET_ANGLE + 0.5 * (angle1 + angle2)) * Math.PI / 180.0;
        double cosMedium = Math.cos(angleMedium);
        double sinMedium = Math.sin(angleMedium);
        double xMedium = radiusMedium * cosMedium;
        double yMesium = -radiusMedium * sinMedium;

        Affine affine = new Affine();
        affine.prependTranslation(xMedium, yMesium);
        this.key.getTransforms().clear();
        this.key.getTransforms().add(affine);
    }

    private void updateRollShape() {
        double angle1 = 0.0;
        double angle2 = this.getEndAngle() - this.getStartAngle();

        double radiusSmall = Math.max(this.getRadius(), 0.0);
        double radiusMedium = Math.max(this.getRadius() + MEDIUM_RATE * this.getWidth(), radiusSmall);
        double radiusLarge = Math.max(this.getRadius() + this.getWidth(), radiusSmall);
        double startAngleTail = (OFFSET_ANGLE + angle1) * Math.PI / 180.0;
        double startAngleHead = (OFFSET_ANGLE + angle1 + ARROW_ANGLE) * Math.PI / 180.0;
        double endAngleTail = (OFFSET_ANGLE + angle2) * Math.PI / 180.0;
        double endAngleHead = (OFFSET_ANGLE + angle2 + ARROW_ANGLE) * Math.PI / 180.0;

        double startCosTail = Math.cos(startAngleTail);
        double startSinTail = Math.sin(startAngleTail);
        double startCosHead = Math.cos(startAngleHead);
        double startSinHead = Math.sin(startAngleHead);
        double endCosTail = Math.cos(endAngleTail);
        double endSinTail = Math.sin(endAngleTail);
        double endCosHead = Math.cos(endAngleHead);
        double endSinHead = Math.sin(endAngleHead);

        double xStart1 = radiusSmall * startCosTail;
        double yStart1 = -radiusSmall * startSinTail;
        double xStart2 = radiusMedium * startCosHead;
        double yStart2 = -radiusMedium * startSinHead;
        double xStart3 = radiusLarge * startCosTail;
        double yStart3 = -radiusLarge * startSinTail;
        double xEnd1 = radiusSmall * endCosTail;
        double yEnd1 = -radiusSmall * endSinTail;
        double xEnd2 = radiusMedium * endCosHead;
        double yEnd2 = -radiusMedium * endSinHead;
        double xEnd3 = radiusLarge * endCosTail;
        double yEnd3 = -radiusLarge * endSinTail;

        MoveTo moveTo = new MoveTo();
        moveTo.setX(xStart1);
        moveTo.setY(yStart1);

        LineTo lineToStart1 = new LineTo();
        lineToStart1.setX(xStart2);
        lineToStart1.setY(yStart2);

        LineTo lineToStart2 = new LineTo();
        lineToStart2.setX(xStart3);
        lineToStart2.setY(yStart3);

        ArcTo arcToLarge = new ArcTo();
        arcToLarge.setX(xEnd3);
        arcToLarge.setY(yEnd3);
        arcToLarge.setRadiusX(radiusLarge);
        arcToLarge.setRadiusY(radiusLarge);
        arcToLarge.setSweepFlag(false);

        LineTo lineToEnd1 = new LineTo();
        lineToEnd1.setX(xEnd2);
        lineToEnd1.setY(yEnd2);

        LineTo lineToEnd2 = new LineTo();
        lineToEnd2.setX(xEnd1);
        lineToEnd2.setY(yEnd1);

        ArcTo arcToSmall = new ArcTo();
        arcToSmall.setX(xStart1);
        arcToSmall.setY(yStart1);
        arcToSmall.setRadiusX(radiusSmall);
        arcToSmall.setRadiusY(radiusSmall);
        arcToSmall.setSweepFlag(true);

        List<PathElement> elements = this.rollShape.getElements();
        elements.clear();
        elements.add(moveTo);
        elements.add(lineToStart1);
        elements.add(lineToStart2);
        elements.add(arcToLarge);
        elements.add(lineToEnd1);
        elements.add(lineToEnd2);
        elements.add(arcToSmall);

        Affine affine = new Affine();
        affine.prependRotation(-this.getStartAngle(), 0.0, 0.0);
        this.rollShape.getTransforms().clear();
        this.rollShape.getTransforms().add(affine);
    }
}
