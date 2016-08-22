/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.save;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import burai.app.project.QEFXProjectController;

public class SaveText extends Text {

    private static final double MAX_ANGLE = 15.0;

    private static final double TIME_TO_DECAY = 1200.0;

    private Animation animation;

    public SaveText(QEFXProjectController controller) {
        super("This project was saved.");

        this.getStyleClass().add("save-text");

        if (controller != null) {
            controller.stackOnViewerPane(this);
        }

        this.setupRotation();
        this.setupAnimation();
    }

    private void setupRotation() {
        this.setRotate(MAX_ANGLE * (1.0 - 2.0 * Math.random()));
    }

    private void setupAnimation() {
        KeyValue keyValue = new KeyValue(this.opacityProperty(), 0.0);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(TIME_TO_DECAY), keyValue);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.getKeyFrames().add(keyFrame);
        timeline.setOnFinished(event -> this.deleteOneself());

        this.animation = timeline;
    }

    private void deleteOneself() {
        Parent parent = this.getParent();
        if (parent != null && parent instanceof Pane) {
            ((Pane) parent).getChildren().remove(this);
        }
    }

    public void playDecayingAnimation() {
        this.setOpacity(1.0);
        if (this.animation != null) {
            animation.playFromStart();
        }
    }
}
