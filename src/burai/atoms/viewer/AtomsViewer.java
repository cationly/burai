/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.viewer.logger.AtomsLogger;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.visible.VisibleAtom;
import burai.atoms.visible.VisibleCell;

public class AtomsViewer extends Group {

    private static final Color BACKGROUND_COLOR = Color.DIMGRAY;

    private double width;
    private double height;

    private boolean compassMode;

    private Camera camera;
    private Group sceneRoot;
    private SubScene subScene;

    private ViewerCell viewerCell;
    private ViewerSample viewerSample;
    private ViewerXYZAxis viewerXYZAxis;
    private ViewerCompass viewerCompass;

    private AtomsLogger logger;

    private List<NodeWrapper> exclusiveNodes;
    private Map<NodeWrapper, Boolean> exclusiveDisables;

    public AtomsViewer(Cell cell, double size) {
        this(cell, size, size);
    }

    public AtomsViewer(Cell cell, double width, double height) {
        super();

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        if (width <= 0.0) {
            throw new IllegalArgumentException("width is not positive.");
        }

        if (height <= 0.0) {
            throw new IllegalArgumentException("height is not positive.");
        }

        this.width = width;
        this.height = height;

        this.compassMode = false;

        this.camera = null;
        this.sceneRoot = null;
        this.subScene = null;

        this.viewerCell = new ViewerCell(this, cell);
        this.viewerSample = new ViewerSample(this, cell);
        this.viewerXYZAxis = new ViewerXYZAxis(this);
        this.viewerCompass = new ViewerCompass(this.viewerCell);

        this.logger = new AtomsLogger(cell);

        this.exclusiveNodes = new ArrayList<NodeWrapper>();
        this.exclusiveDisables = new HashMap<NodeWrapper, Boolean>();

        this.createCamera();
        this.createSceneRoot();
        this.createSubScene();
        this.getChildren().add(this.subScene);
    }

    private void createCamera() {
        if (this.camera == null) {
            this.camera = new ParallelCamera();
        }

        double range = Math.min(this.width, this.height);
        this.camera.setFarClip(100.0 * range);
        this.camera.setNearClip(1.0e-4);
    }

    private void createSceneRoot() {
        this.sceneRoot = new Group();
        this.sceneRoot.setDepthTest(DepthTest.ENABLE);
        this.sceneRoot.getChildren().add(this.viewerCell.getNode());
        this.sceneRoot.getChildren().add(this.viewerSample.getNode());
        this.sceneRoot.getChildren().add(this.viewerXYZAxis.getNode());
        this.sceneRoot.getChildren().add(this.viewerCompass.getNode());
    }

    private void createSubScene() {
        ViewerEventManager viewerEventManager = new ViewerEventManager(this);

        this.subScene = new SubScene(this.sceneRoot, this.width, this.height, true, SceneAntialiasing.BALANCED);
        this.subScene.getStyleClass().add("atoms-viewer");
        this.subScene.setFill(BACKGROUND_COLOR);
        this.subScene.setCamera(this.camera);
        this.subScene.setManaged(false);
        this.subScene.setOnMouseClicked(event -> this.subScene.requestFocus());
        this.subScene.setOnMousePressed(viewerEventManager.getMousePressedHandler());
        this.subScene.setOnMouseDragged(viewerEventManager.getMouseDraggedHandler());
        this.subScene.setOnMouseReleased(viewerEventManager.getMouseReleasedHandler());
        this.subScene.setOnKeyPressed(viewerEventManager.getKeyPressedHandler());
        this.subScene.setOnScroll(viewerEventManager.getScrollHandler());
        this.subScene.widthProperty().addListener(o -> this.actionOnResizing());
        this.subScene.heightProperty().addListener(o -> this.actionOnResizing());
    }

    private void actionOnResizing() {
        double widthTmp = -1.0;
        double heightTmp = -1.0;

        if (this.subScene != null) {
            widthTmp = this.subScene.getWidth();
            heightTmp = this.subScene.getHeight();
        }

        if (widthTmp <= 0.0 || heightTmp <= 0.0) {
            return;
        }

        this.width = widthTmp;
        this.height = heightTmp;

        this.createCamera();

        if (this.viewerCell != null) {
            this.viewerCell.initialize(true);
        }

        if (this.viewerSample != null) {
            this.viewerSample.initialize();
        }

        if (this.viewerXYZAxis != null) {
            this.viewerXYZAxis.initialize(true);
        }

        if (this.viewerCompass != null) {
            this.viewerCompass.initialize(true);
        }
    }

    public boolean isCompassMode() {
        return this.compassMode;
    }

    public void setCompassMode(VisibleAtom targetAtom) {
        this.compassMode = (targetAtom != null);
        this.viewerCompass.setTargetAtom(targetAtom);

        if (this.compassMode) {
            this.viewerCompass.initialize();
            this.viewerCompass.getNode().setVisible(true);
            for (NodeWrapper nodeWrapper : this.exclusiveNodes) {
                if (nodeWrapper == null) {
                    continue;
                }
                Node node = nodeWrapper.getNode();
                if (node == null) {
                    continue;
                }
                this.exclusiveDisables.put(nodeWrapper, node.isDisable());
                node.setDisable(true);
            }

        } else {
            this.viewerCompass.getNode().setVisible(false);
            for (NodeWrapper nodeWrapper : this.exclusiveNodes) {
                if (nodeWrapper == null) {
                    continue;
                }
                Node node = nodeWrapper.getNode();
                if (node == null) {
                    continue;
                }
                Boolean disable = this.exclusiveDisables.get(nodeWrapper);
                if (disable != null) {
                    node.setDisable(disable);
                }
            }
        }
    }

    public void addExclusiveNode(Node node) {
        if (node == null) {
            return;
        }

        this.exclusiveNodes.add(() -> {
            return node;
        });
    }

    public void addExclusiveNode(NodeWrapper nodeWrapper) {
        if (nodeWrapper == null) {
            return;
        }

        this.exclusiveNodes.add(nodeWrapper);
    }

    public void bindSceneTo(Pane pane) {
        if (pane == null) {
            return;
        }

        if (this.subScene == null) {
            return;
        }

        if (pane instanceof AnchorPane) {
            AnchorPane.setBottomAnchor(this.subScene, 0.0);
            AnchorPane.setTopAnchor(this.subScene, 0.0);
            AnchorPane.setLeftAnchor(this.subScene, 0.0);
            AnchorPane.setRightAnchor(this.subScene, 0.0);
        }

        this.subScene.widthProperty().bind(pane.widthProperty());
        this.subScene.heightProperty().bind(pane.heightProperty());

        Parent parent = this.subScene.getParent();
        if (parent == this) {
            List<Node> children = this.getChildren();
            if (children.contains(this.subScene)) {
                children.remove(this.subScene);
            }
        }

        pane.getChildren().add(this.subScene);
    }

    public void unbindScene() {
        if (this.subScene == null) {
            return;
        }

        this.subScene.widthProperty().unbind();
        this.subScene.heightProperty().unbind();

        Parent parent = this.subScene.getParent();
        if (parent != null && parent instanceof Pane) {
            Pane pane = (Pane) parent;
            List<Node> children = pane.getChildren();
            if (children.contains(this.subScene)) {
                children.remove(this.subScene);
            }
        }

        this.getChildren().add(this.subScene);
    }

    public double getSceneWidth() {
        return this.width;
    }

    public double getSceneHeight() {
        return this.height;
    }

    public Cell getCell() {
        if (this.viewerCell == null) {
            return null;
        }

        VisibleCell visibleCell = this.viewerCell.getNode();
        if (visibleCell == null) {
            return null;
        }

        return visibleCell.getModel();
    }

    public void appendCellScale(double scale) {
        if (this.compassMode) {
            return;
        }

        if (this.viewerCell != null) {
            this.viewerCell.appendScale(scale);
        }
    }

    public void appendCellRotation(double angle, double axisX, double axisY, double axisZ) {
        if (this.compassMode) {
            return;
        }

        if (this.viewerCell != null) {
            this.viewerCell.appendRotation(angle, axisX, axisY, axisZ);
        }

        if (this.viewerXYZAxis != null) {
            this.viewerXYZAxis.appendRotation(angle, axisX, axisY, axisZ);
        }
    }

    public void appendCellTranslation(double x, double y, double z) {
        if (this.compassMode) {
            return;
        }

        if (this.viewerCell != null) {
            this.viewerCell.appendTranslation(x, y, z);
        }
    }

    public void appendCompassRotation(double angle, double axisX, double axisY, double axisZ) {
        if (!this.compassMode) {
            return;
        }

        if (this.viewerCompass != null) {
            this.viewerCompass.appendRotation(angle, axisX, axisY, axisZ);
        }
    }

    public boolean addChild(Node node) {
        if (this.compassMode) {
            return false;
        }

        if (this.sceneRoot != null) {
            return this.sceneRoot.getChildren().add(node);
        }

        return false;
    }

    public boolean removeChild(Node node) {
        if (this.compassMode) {
            return false;
        }

        if (this.sceneRoot != null) {
            return this.sceneRoot.getChildren().remove(node);
        }

        return false;
    }

    public boolean hasChild(Node node) {
        if (this.sceneRoot != null) {
            return this.sceneRoot.getChildren().contains(node);
        }

        return false;
    }

    public List<VisibleAtom> getVisibleAtoms() {
        List<VisibleAtom> visibleAtoms = new ArrayList<VisibleAtom>();

        VisibleCell visibleCell = null;
        if (this.viewerCell != null) {
            visibleCell = this.viewerCell.getNode();
        }

        if (visibleCell != null) {
            List<Node> children = visibleCell.getChildren();
            for (Node child : children) {
                if (child instanceof VisibleAtom) {
                    visibleAtoms.add((VisibleAtom) child);
                }
            }
        }

        return visibleAtoms;
    }

    public boolean isInCell(double sceneX, double sceneY, double sceneZ) {
        if (this.viewerCell == null) {
            return false;
        }

        return this.viewerCell.isInCell(sceneX, sceneY, sceneZ);
    }

    public Point3D sceneToCell(double sceneX, double sceneY, double sceneZ) {
        if (this.viewerCell == null) {
            return null;
        }

        VisibleCell visibleCell = this.viewerCell.getNode();
        if (visibleCell == null) {
            return null;
        }

        return visibleCell.sceneToLocal(sceneX, sceneY, sceneZ);
    }

    public double getSceneZOnCompass(double sceneX, double sceneY) {
        if (!this.compassMode) {
            return 0.0;
        }

        if (this.viewerCompass == null) {
            return 0.0;
        }

        return this.viewerCompass.getSceneZ(sceneX, sceneY);
    }

    public void putAtom(String name, double sceneX, double sceneY, double sceneZ) {
        if (this.compassMode) {
            return;
        }

        if (this.viewerCell == null) {
            return;
        }

        VisibleCell visibleCell = this.viewerCell.getNode();
        if (visibleCell == null) {
            return;
        }

        Point3D point3d = visibleCell.sceneToLocal(sceneX, sceneY, sceneZ);
        double x = point3d.getX();
        double y = point3d.getY();
        double z = point3d.getZ();

        if (name != null && !name.isEmpty()) {
            visibleCell.getModel().addAtom(new Atom(name, x, y, z));
        }
    }

    public void setCellToCenter() {
        if (this.compassMode) {
            return;
        }

        if (this.viewerCell != null) {
            this.viewerCell.initialize();
        }

        if (this.viewerXYZAxis != null) {
            this.viewerXYZAxis.initialize();
        }
    }

    public void setCompassToCenter() {
        if (!this.compassMode) {
            return;
        }

        if (this.viewerCompass != null) {
            this.viewerCompass.initialize();
        }
    }

    public void storeCell() {
        if (this.logger != null) {
            this.logger.storeConfiguration();
        }
    }

    public boolean canRestoreCell() {
        if (this.compassMode) {
            return false;
        }

        if (this.logger != null) {
            return this.logger.canRestoreConfiguration();
        }

        return false;
    }

    public boolean canSubRestoreCell() {
        if (this.compassMode) {
            return false;
        }

        if (this.logger != null) {
            return this.logger.canSubRestoreConfiguration();
        }

        return false;
    }

    public void restoreCell() {
        if (this.compassMode) {
            return;
        }

        if (this.logger != null) {
            this.logger.restoreConfiguration();
        }
    }

    public void subRestoreCell() {
        if (this.compassMode) {
            return;
        }

        if (this.logger != null) {
            this.logger.subRestoreConfiguration();
        }
    }
}
