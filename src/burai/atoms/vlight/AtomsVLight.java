/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.vlight;

import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import burai.atoms.model.Cell;

public class AtomsVLight extends Group {

    private static final Color BACKGROUND_COLOR = Color.TRANSPARENT;

    private double size;

    private Camera camera;
    private Group sceneRoot;
    private SubScene subScene;

    private VLightCell vlightCell;

    public AtomsVLight(Cell cell, double size) {
        this(cell, size, false);
    }

    public AtomsVLight(Cell cell, double size, boolean parallel) {
        super();

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        if (size <= 0.0) {
            throw new IllegalArgumentException("width is not positive.");
        }

        this.size = size;

        this.camera = null;
        this.sceneRoot = null;
        this.subScene = null;

        this.vlightCell = new VLightCell(this, cell);

        this.createCamera(parallel);
        this.createSceneRoot();
        this.createSubScene();
        this.getChildren().add(this.subScene);
    }

    private void createCamera(boolean parallel) {
        if (parallel) {
            this.camera = new ParallelCamera();
        } else {
            this.camera = new PerspectiveCamera(false);
        }
    }

    private void createSceneRoot() {
        this.sceneRoot = new Group();
        this.sceneRoot.setDepthTest(DepthTest.ENABLE);
        this.sceneRoot.getChildren().add(this.vlightCell.getNode());
        this.sceneRoot.getChildren().add(new AmbientLight(Color.WHITE));
    }

    private void createSubScene() {
        this.subScene = new SubScene(this.sceneRoot, this.size, this.size, true, SceneAntialiasing.BALANCED);
        this.subScene.setFill(BACKGROUND_COLOR);
        this.subScene.setCamera(this.camera);

        MouseLightHandler handler = new MouseLightHandler(this);
        this.subScene.setOnMousePressed(handler);
        this.subScene.setOnMouseDragged(handler);
        this.subScene.setOnMouseReleased(handler);
    }

    public double getSize() {
        return this.size;
    }

    public void appendRotation(double angle, double axisX, double axisY, double axisZ) {
        if (this.vlightCell != null) {
            this.vlightCell.appendRotation(angle, axisX, axisY, axisZ);
        }
    }

    public void detachFromCell() {
        if (this.vlightCell != null) {
            this.vlightCell.detachFromCell();
        }
    }
}
