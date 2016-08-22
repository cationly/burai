/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.menu.teeth;

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

public class QEFXTeethMenu extends QEFXMenu<Node> {

    private static final double INITIAL_X = -1000.0;
    private static final double INITIAL_BOTTOM = -3.0;
    private static final double BETWEEN_TEETH = 2.5;

    private static final double CLOSE_ITEM_X = 7.0;
    private static final double CLOSE_ITEM_Y = INITIAL_BOTTOM + 0.5;

    private static final double OVER_RUNNING = 0.02;

    private static final double TIME_TO_SHOW_ITEM = 100.0;
    private static final double TIME_TO_DELAY_ITEM = TIME_TO_SHOW_ITEM / 4.5;

    private double width;
    private double height;
    private double bottom;

    private QEFXCloseMenuItem<Node> closeItem;

    private List<Animation> animations;

    public QEFXTeethMenu(double width, double height) {
        super();

        if (width <= 0.0) {
            throw new IllegalArgumentException("width is not positive.");
        }

        if (height <= 0.0) {
            throw new IllegalArgumentException("height is not positive.");
        }

        this.width = width;
        this.height = height;
        this.bottom = INITIAL_BOTTOM;
        this.createCloseItem();
        this.animations = null;
    }

    private void createCloseItem() {
        this.closeItem = new QEFXCloseMenuItem<Node>(CLOSE_ITEM_X, CLOSE_ITEM_Y);
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

    private void addToAnimations(QEFXTeethMenuItem item) {
        if (item == null) {
            return;
        }

        int numItems = this.getChildren().size() - 1;

        KeyValue keyValue1 = new KeyValue(item.xProperty(), OVER_RUNNING * item.getWidth());
        KeyValue keyValue2 = new KeyValue(item.xProperty(), 0.0);
        KeyFrame keyFrame1 = new KeyFrame(Duration.millis(TIME_TO_SHOW_ITEM), keyValue1);
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(OVER_RUNNING * TIME_TO_SHOW_ITEM), keyValue2);

        Timeline timeline1 = new Timeline();
        timeline1.setCycleCount(1);
        timeline1.setDelay(Duration.millis(((double) numItems) * TIME_TO_DELAY_ITEM));
        timeline1.getKeyFrames().add(keyFrame1);

        timeline1.setOnFinished(event -> {
            Timeline timeline2 = new Timeline();
            timeline2.setCycleCount(1);
            timeline2.getKeyFrames().add(keyFrame2);
            timeline2.playFromStart();
        });

        if (this.animations == null) {
            this.animations = new ArrayList<Animation>();
        }
        this.animations.add(timeline1);
    }

    @Override
    public void clearItem() {
        this.bottom = INITIAL_BOTTOM;
        this.getChildren().clear();
        this.getChildren().add(this.closeItem);
        if (this.animations != null) {
            this.animations.clear();
        }
    }

    @Override
    public void addItem(Node graphic) {
        QEFXTeethMenuItem item = new QEFXTeethMenuItem(graphic);
        item.setX(INITIAL_X);
        item.setY(this.bottom - BETWEEN_TEETH);
        item.setWidth(this.width);
        item.setHeight(this.height);
        item.setParent(this);
        this.addToAnimations(item);
        this.getChildren().add(item);
        this.bottom -= this.height + BETWEEN_TEETH;
    }

    @Override
    protected void playShowingAnimation() {
        this.closeItem.setOpacity(0.0);

        List<Node> children = this.getChildren();
        for (Node child : children) {
            if (child != null && (child instanceof QEFXTeethMenuItem)) {
                QEFXTeethMenuItem item = (QEFXTeethMenuItem) child;
                item.setX(-item.getMaxWidth());
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
