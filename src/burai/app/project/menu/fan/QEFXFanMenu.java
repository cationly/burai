/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.menu.fan;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;
import burai.app.project.menu.QEFXCloseMenuItem;
import burai.app.project.menu.QEFXMenu;

public class QEFXFanMenu extends QEFXMenu<String> {

    private static final double ITEM_ARC = 120.0 * Math.PI / 6.0;

    private static final double INITIAL_ANGLE = 90.0;
    private static final double FINAL_ANGLE = 180.0;
    private static final double ANGLE_WIDTH = FINAL_ANGLE - INITIAL_ANGLE;

    private static final double CLOSE_ITEM_X = -35.0;
    private static final double CLOSE_ITEM_Y = -34.0;

    private static final double OVER_RUNNING = 0.20;

    private static final double TIME_TO_SHOW_ITEM = 100.0;
    private static final double TIME_TO_DELAY_ITEM = TIME_TO_SHOW_ITEM / 3.0;

    private double initialRadius;
    private double initialWidth;

    private double radius;
    private double width;
    private double angle;
    private double angleWidth;

    private QEFXCloseMenuItem<String> closeItem;

    private List<Animation> animations;

    public QEFXFanMenu(double radius, double width) {
        super();

        if (radius < 0.0) {
            throw new IllegalArgumentException("radius is negative.");
        }

        if (width <= 0.0) {
            throw new IllegalArgumentException("width is not positive.");
        }

        this.initialRadius = radius;
        this.initialWidth = width;
        this.initializeParameters();
        this.createCloseItem();
        this.animations = null;
    }

    private void initializeParameters() {
        this.radius = this.initialRadius;
        this.width = this.initialWidth;
        this.angle = INITIAL_ANGLE;
        this.updateAngleWidth();
    }

    private void updateAngleWidth() {
        double radiusEff = this.radius + 0.5 * this.width;
        double radianWidth = ITEM_ARC / radiusEff;
        this.angleWidth = radianWidth * 180.0 / Math.PI;
        this.angleWidth = ANGLE_WIDTH / Math.rint(ANGLE_WIDTH / this.angleWidth);
    }

    private void updateParameters() {
        this.angle += this.angleWidth;
        if (this.angle < (FINAL_ANGLE - 1.0e-10)) {
            return;
        }

        this.radius += this.width;
        this.angle = INITIAL_ANGLE;
        this.updateAngleWidth();
    }

    private void createCloseItem() {
        this.closeItem = new QEFXCloseMenuItem<String>(CLOSE_ITEM_X, CLOSE_ITEM_Y);
        this.closeItem.setOpacity(0.0);
        this.closeItem.setParent(this);
        this.getChildren().add(this.closeItem);
    }

    private Animation getCloseAnimation() {
        KeyValue keyValue = new KeyValue(this.closeItem.opacityProperty(), 1.0);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(TIME_TO_SHOW_ITEM), keyValue);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setDelay(Duration.millis(((double) this.getChildren().size()) * TIME_TO_DELAY_ITEM));
        timeline.getKeyFrames().add(keyFrame);

        return timeline;
    }

    private void addToAnimations(QEFXFanMenuItem item) {
        if (item == null) {
            return;
        }

        int numItems = this.getChildren().size() - 1;

        KeyValue radiusKeyValue1 = new KeyValue(item.radiusProperty(), (1.0 + OVER_RUNNING) * this.radius);
        KeyValue radiusKeyValue2 = new KeyValue(item.radiusProperty(), this.radius);
        KeyValue widthKeyValue = new KeyValue(item.widthProperty(), this.width);
        KeyFrame radiusKeyFrame1 = new KeyFrame(Duration.millis(TIME_TO_SHOW_ITEM), radiusKeyValue1);
        KeyFrame radiusKeyFrame2 = new KeyFrame(Duration.millis(OVER_RUNNING * TIME_TO_SHOW_ITEM), radiusKeyValue2);
        KeyFrame widthKeyFrame = new KeyFrame(Duration.millis(TIME_TO_SHOW_ITEM), widthKeyValue);

        Timeline timeline1 = new Timeline();
        timeline1.setCycleCount(1);
        timeline1.setDelay(Duration.millis(((double) numItems) * TIME_TO_DELAY_ITEM));
        timeline1.getKeyFrames().addAll(radiusKeyFrame1, widthKeyFrame);

        timeline1.setOnFinished(event -> {
            Timeline timeline2 = new Timeline();
            timeline2.setCycleCount(1);
            timeline2.getKeyFrames().addAll(radiusKeyFrame2);
            timeline2.playFromStart();
        });

        if (this.animations == null) {
            this.animations = new ArrayList<Animation>();
        }
        this.animations.add(timeline1);
    }

    @Override
    public void clearItem() {
        this.getChildren().clear();
        this.getChildren().add(this.closeItem);
        this.initializeParameters();
        if (this.animations != null) {
            this.animations.clear();
        }
    }

    @Override
    public void addItem(String text) {
        QEFXFanMenuItem item = new QEFXFanMenuItem(text);
        item.setStartAngle(this.angle);
        item.setEndAngle(this.angle + this.angleWidth);
        item.setParent(this);
        this.addToAnimations(item);
        this.getChildren().add(item);
        this.updateParameters();
    }

    @Override
    protected void playShowingAnimation() {
        this.closeItem.setOpacity(0.0);

        List<Node> children = this.getChildren();
        for (Node child : children) {
            if (child != null && (child instanceof QEFXFanMenuItem)) {
                QEFXFanMenuItem item = (QEFXFanMenuItem) child;
                item.setRadius(0.0);
                item.setWidth(0.0);
            }
        }

        Animation closeAnimation = this.getCloseAnimation();
        if (closeAnimation != null) {
            closeAnimation.playFromStart();
        }

        if (this.animations != null) {
            for (Animation animation : this.animations) {
                animation.playFromStart();
            }
        }
    }
}
