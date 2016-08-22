/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.menu.fan;

import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import burai.app.project.menu.QEFXMenuItem;

public class QEFXFanMenuItem extends QEFXMenuItem<String> {

    private static final double DEFAULT_FONT_SIZE = 12.0;

    private static final double BIG_TEXT_RATE = 1.4;

    private static final double BETWEEN_ITEMS = 2.0;

    private DoubleProperty radius;
    private DoubleProperty width;
    private DoubleProperty startAngle;
    private DoubleProperty endAngle;

    private double fanFontSize;
    private Label fanLabel;
    private Path fanShape;

    public QEFXFanMenuItem(String title) {
        super(title);
        if (this.key == null) {
            this.key = "";
        }

        this.radius = null;
        this.width = null;
        this.startAngle = null;
        this.endAngle = null;

        this.createFanLabel();
        this.createFanShape();

        this.setOnMenuItemSelected(key -> {
            this.updateFanColor();
            this.updateFanLabel();
        });

        this.getChildren().add(this.fanShape);
        this.getChildren().add(this.fanLabel);
    }

    public QEFXFanMenuItem(String title, double radius, double width, double startAngle, double endAngle) {
        this(title);

        this.setRadius(radius);
        this.setWidth(width);
        this.setStartAngle(startAngle);
        this.setEndAngle(endAngle);
    }

    public DoubleProperty radiusProperty() {
        if (this.radius == null) {
            this.radius = new SimpleDoubleProperty(0.0);
            this.radius.addListener(o -> {
                this.updateFanLabel();
                this.updateFanShape();
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
                this.updateFanLabel();
                this.updateFanShape();
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
                this.updateFanLabel();
                this.updateFanShape();
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
                this.updateFanLabel();
                this.updateFanShape();
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

    private void createFanLabel() {
        this.fanLabel = new Label(this.key);
        this.fanLabel.getStyleClass().add("fan-menu-text");

        this.fanFontSize = DEFAULT_FONT_SIZE;
        Font font = this.fanLabel.getFont();
        if (font != null) {
            this.fanFontSize = font.getSize();
        }

        this.updateFanLabel();
    }

    private void createFanShape() {
        this.fanShape = new Path();
        this.fanShape.getStyleClass().add("fan-menu-item");

        this.updateFanColor();
        this.updateFanShape();
    }

    private void updateFanColor() {
        if (this.selected) {
            this.fanShape.setStyle("-fx-fill: -fx-fill-focused");
        } else {
            this.fanShape.setStyle("");
        }
    }

    private void updateFanLabel() {
        double fanFontSize_ = this.fanFontSize;

        if (this.selected) {
            fanFontSize_ *= BIG_TEXT_RATE;
            String sizeStyle = "-fx-font-size: " + (fanFontSize_ / 12.0) + "em;";
            String weightStyle = "-fx-font-weight: bold;";
            this.fanLabel.setStyle(sizeStyle + weightStyle);
        } else {
            this.fanLabel.setStyle("");
        }

        double x = -(this.getRadius() + this.getWidth() - 1.2 * this.fanFontSize);
        double y = -0.75 * fanFontSize_;
        double angle = 180.0 - 0.5 * (this.getStartAngle() + this.getEndAngle());

        Affine affine = new Affine();
        affine.prependTranslation(x, y);
        affine.prependRotation(angle, 0.0, 0.0);
        this.fanLabel.getTransforms().clear();
        this.fanLabel.getTransforms().add(affine);
    }

    private void updateFanShape() {
        double radiusSmall = Math.max(this.getRadius() + BETWEEN_ITEMS, BETWEEN_ITEMS);
        double radiusLarge = Math.max(this.getRadius() + this.getWidth() - BETWEEN_ITEMS, radiusSmall);
        double startAngleSmall = this.getStartAngle() * Math.PI / 180.0 + BETWEEN_ITEMS / radiusSmall;
        double startAngleLarge = this.getStartAngle() * Math.PI / 180.0 + BETWEEN_ITEMS / radiusLarge;
        double endAngleSmall =
                Math.max(this.getEndAngle() * Math.PI / 180.0 - BETWEEN_ITEMS / radiusSmall, startAngleSmall);
        double endAngleLarge =
                Math.max(this.getEndAngle() * Math.PI / 180.0 - BETWEEN_ITEMS / radiusLarge, startAngleLarge);

        double startCosSmall = Math.cos(startAngleSmall);
        double startSinSmall = Math.sin(startAngleSmall);
        double startCosLarge = Math.cos(startAngleLarge);
        double startSinLarge = Math.sin(startAngleLarge);
        double endCosSmall = Math.cos(endAngleSmall);
        double endSinSmall = Math.sin(endAngleSmall);
        double endCosLarge = Math.cos(endAngleLarge);
        double endSinLarge = Math.sin(endAngleLarge);

        double x1 = radiusSmall * startCosSmall;
        double y1 = -radiusSmall * startSinSmall;
        double x2 = radiusLarge * startCosLarge;
        double y2 = -radiusLarge * startSinLarge;
        double x3 = radiusLarge * endCosLarge;
        double y3 = -radiusLarge * endSinLarge;
        double x4 = radiusSmall * endCosSmall;
        double y4 = -radiusSmall * endSinSmall;

        MoveTo moveTo = new MoveTo();
        moveTo.setX(x1);
        moveTo.setY(y1);

        LineTo lineTo1 = new LineTo();
        lineTo1.setX(x2);
        lineTo1.setY(y2);

        ArcTo arcToLarge = new ArcTo();
        arcToLarge.setX(x3);
        arcToLarge.setY(y3);
        arcToLarge.setRadiusX(radiusLarge);
        arcToLarge.setRadiusY(radiusLarge);
        arcToLarge.setSweepFlag(false);

        LineTo lineTo2 = new LineTo();
        lineTo2.setX(x4);
        lineTo2.setY(y4);

        ArcTo arcToSmall = new ArcTo();
        arcToSmall.setX(x1);
        arcToSmall.setY(y1);
        arcToSmall.setRadiusX(radiusSmall);
        arcToSmall.setRadiusY(radiusSmall);
        arcToSmall.setSweepFlag(true);

        List<PathElement> elements = this.fanShape.getElements();
        elements.clear();
        elements.add(moveTo);
        elements.add(lineTo1);
        elements.add(arcToLarge);
        elements.add(lineTo2);
        elements.add(arcToSmall);
    }
}
