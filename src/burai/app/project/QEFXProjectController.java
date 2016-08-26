/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import burai.app.QEFXAppController;
import burai.app.QEFXMainController;
import burai.app.project.editor.EditorActions;
import burai.app.project.menu.MenuItemSelected;
import burai.app.project.menu.fan.QEFXFanMenu;
import burai.app.project.menu.teeth.QEFXTeethMenu;
import burai.app.project.viewer.ViewerActions;
import burai.atoms.viewer.AtomsViewer;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class QEFXProjectController extends QEFXAppController {

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "pictured-button";

    private static final double TEETH_WIDTH = 285.0;
    private static final double TEETH_HEIGHT = 56.0;

    private static final double FAN_RADIUS = 180.0;
    private static final double FAN_WIDTH = 140.0;
    private static final double FAN_OFFSET = 10.0;

    private static final int RESULT_MODE_NULL = 0;
    private static final int RESULT_MODE_EXPLORER = 1;
    private static final int RESULT_MODE_VIEWER = 2;

    @FXML
    private Pane backPane;

    @FXML
    private BorderPane projectPane;

    @FXML
    private StackPane viewerPane;

    @FXML
    private Button viewerButton;

    private QEFXTeethMenu viewerMenu;

    private MenuItemSelected<Node> onViewerSelected;

    private List<Node> stackedsOnViewer;

    private Queue<Node> stackedsToViewer;

    private ViewerActions viewerActions;

    @FXML
    private Button editorButton;

    @FXML
    private Label editorLabel;

    private QEFXFanMenu editorMenu;

    private MenuItemSelected<String> onEditorSelected;

    private EditorActions editorActions;

    private AtomsViewer atomsViewer;

    private int resultMode;

    private Map<Integer, ProjectAnsatz> projectAnsatzMap;

    private Map<Integer, ProjectAction> restoredActionMap;

    public QEFXProjectController(QEFXMainController mainController) {
        super(mainController);

        this.viewerMenu = null;
        this.onViewerSelected = null;
        this.stackedsOnViewer = null;
        this.stackedsToViewer = null;
        this.viewerActions = null;

        this.editorMenu = null;
        this.onEditorSelected = null;
        this.editorActions = null;

        this.atomsViewer = null;
        this.resultMode = RESULT_MODE_NULL;
        this.projectAnsatzMap = null;
        this.restoredActionMap = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.createViewerMenu();
        this.createEditorMenu();
        this.setupResizing();
        this.setupViewerButton();
        this.setupEditorButton();
        this.setupEditorLabel();
    }

    public BorderPane getProjectPane() {
        return this.projectPane;
    }

    /**
     * stack node on viewerPane.
     * @param node
     */
    public void stackOnViewerPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.viewerPane != null && (!this.viewerPane.getChildren().isEmpty())) {
            if (this.stackedsOnViewer == null) {
                this.stackedsOnViewer = new ArrayList<Node>();
            }

            this.stackedsOnViewer.add(node);

            this.viewerPane.getChildren().add(node);
        }
    }

    /**
     * node will be stacked onto viewerPane, when new viewerPane will be set.
     * @param node
     */
    public void stackToViewerPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.stackedsToViewer == null) {
            this.stackedsToViewer = new LinkedList<Node>();
        }

        this.stackedsToViewer.offer(node);
    }

    public void clearStackedsOnViewerPane() {
        if (this.stackedsOnViewer == null || this.stackedsOnViewer.isEmpty()) {
            return;
        }

        if (this.viewerPane != null) {
            List<Node> children = this.viewerPane.getChildren();
            for (Node stacked : this.stackedsOnViewer) {
                int index = children.indexOf(stacked);
                if (index > -1) {
                    children.remove(index);
                }
            }

            this.stackedsOnViewer.clear();
        }
    }

    public void setViewerPane(Node node) {
        if (this.viewerPane != null) {
            if (this.atomsViewer != null) {
                this.atomsViewer.unbindScene();
                this.atomsViewer = null;
            }

            if (this.stackedsOnViewer != null) {
                this.stackedsOnViewer.clear();
            }

            List<Node> children = this.viewerPane.getChildren();
            children.clear();

            if (node != null) {
                if (node instanceof AtomsViewer) {
                    this.atomsViewer = (AtomsViewer) node;
                    this.atomsViewer.bindSceneTo(this.viewerPane);
                }
                children.add(node);

                if (this.stackedsToViewer != null) {
                    while (!this.stackedsToViewer.isEmpty()) {
                        Node stacked = this.stackedsToViewer.poll();
                        this.stackOnViewerPane(stacked);
                    }
                }
            }
        }
    }

    public Node getViewerPane() {
        if (this.viewerPane != null) {
            List<Node> children = this.viewerPane.getChildren();
            if (!children.isEmpty()) {
                return children.get(0);
            }
        }

        return null;
    }

    public void setEditorPane(Node node) {
        if (this.projectPane != null) {
            this.projectPane.setRight(node);
        }
    }

    public Node getEditorPane() {
        return this.projectPane == null ? null : this.projectPane.getRight();
    }

    public void setOnViewerSelected(MenuItemSelected<Node> onViewerSelected) {
        this.onViewerSelected = onViewerSelected;
    }

    public void setOnEditorSelected(MenuItemSelected<String> onEditorSelected) {
        this.onEditorSelected = onEditorSelected;
    }

    public void addViewerMenuItems(Node... graphics) {
        if (graphics == null || graphics.length < 1) {
            return;
        }

        if (this.viewerMenu != null) {
            this.viewerMenu.clearItem();
            for (Node graphic : graphics) {
                if (graphic != null) {
                    this.viewerMenu.addItem(graphic);
                }
            }
        }
    }

    public void addEditorMenuItems(String... texts) {
        if (texts == null || texts.length < 1) {
            return;
        }

        if (this.editorMenu != null) {
            this.editorMenu.clearItem();
            for (String text : texts) {
                if (text != null) {
                    this.editorMenu.addItem(text);
                }
            }
        }
    }

    public void setEditorText(String text) {
        if (this.editorLabel != null) {
            this.editorLabel.setText((text == null ? "" : text) + " ");
        }
    }

    private void createViewerMenu() {
        this.viewerMenu = new QEFXTeethMenu(TEETH_WIDTH, TEETH_HEIGHT);

        this.viewerMenu.setOnMenuItemSelected(key -> {
            if (this.onViewerSelected != null) {
                this.onViewerSelected.onMenuItemSelected(key);
            }
            this.enableProjectPane();
        });

        this.viewerMenu.setOnMenuShowing(() -> {
            if (this.backPane != null) {
                this.backPane.getChildren().add(this.viewerMenu);
                this.disableProjectPane();
            }
        });
    }

    private void createEditorMenu() {
        this.editorMenu = new QEFXFanMenu(FAN_RADIUS, FAN_WIDTH);

        this.editorMenu.setOnMenuItemSelected(key -> {
            if (this.onEditorSelected != null) {
                this.onEditorSelected.onMenuItemSelected(key);
            }
            this.enableProjectPane();
        });

        this.editorMenu.setOnMenuShowing(() -> {
            if (this.backPane != null) {
                this.backPane.getChildren().add(this.editorMenu);
                this.disableProjectPane();
            }
        });
    }

    private void setupResizing() {
        if (this.backPane != null) {
            this.backPane.widthProperty().addListener(o -> this.resizeBackPane());
            this.backPane.heightProperty().addListener(o -> this.resizeBackPane());
        }

        if (this.viewerPane != null) {
            this.viewerPane.widthProperty().addListener(o -> this.resizeViewerPane());
            this.viewerPane.heightProperty().addListener(o -> this.resizeViewerPane());
        }
    }

    private void resizeBackPane() {
        if (this.backPane == null) {
            return;
        }

        double width = this.backPane.getWidth();
        double height = this.backPane.getHeight();
        if (width <= 0.0 || height <= 0.0) {
            return;
        }

        double xFan = Math.max(0.0, width - FAN_OFFSET);
        double yFan = Math.max(0.0, height - FAN_OFFSET);
        this.editorMenu.setLayoutX(xFan);
        this.editorMenu.setLayoutY(yFan);
    }

    private void resizeViewerPane() {
        if (this.backPane == null || this.viewerPane == null) {
            return;
        }

        double xViewer = 0.0;
        double yViewer = this.viewerPane.getHeight();
        Point2D pointViewer = this.viewerPane.localToScreen(xViewer, yViewer);
        Point2D pointBack = this.backPane.screenToLocal(pointViewer);
        double xRoll = pointBack.getX();
        double yRoll = pointBack.getY();
        this.viewerMenu.setLayoutX(xRoll);
        this.viewerMenu.setLayoutY(yRoll);
    }

    private void setupViewerButton() {
        if (this.viewerButton == null) {
            return;
        }

        this.viewerButton.setText("");
        this.viewerButton.setGraphic(SVGLibrary.getGraphic(SVGData.MENU, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        this.viewerButton.setOnAction(event -> {
            switch (this.resultMode) {
            case RESULT_MODE_NULL:
                this.viewerMenu.showMenu();
                break;
            case RESULT_MODE_EXPLORER:
                this.resetResultMode();
                break;
            case RESULT_MODE_VIEWER:
                this.setResultExplorerMode();
                break;
            }
        });
    }

    private void setupEditorButton() {
        if (this.editorButton == null) {
            return;
        }

        this.editorButton.setText("");
        this.editorButton.setGraphic(SVGLibrary.getGraphic(SVGData.CONTROL, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        this.editorButton.setOnAction(event -> {
            this.editorMenu.showMenu();
        });
    }

    private void setupEditorLabel() {
        if (this.editorLabel == null) {
            return;
        }

        this.setEditorText("");
    }

    private void enableProjectPane() {
        if (this.projectPane != null) {
            this.projectPane.setDisable(false);
        }
    }

    private void disableProjectPane() {
        if (this.projectPane != null) {
            this.projectPane.setDisable(true);
        }
    }

    protected void setViewerActions(ViewerActions viewerActions) {
        this.viewerActions = viewerActions;
    }

    protected void setEditorActions(EditorActions editorActions) {
        this.editorActions = editorActions;
    }

    public boolean saveFile() {
        return this.viewerActions.saveFile();
    }

    public void sceenShot() {
        this.viewerActions.screenShot();
    }

    public void sceenShot(Node subject) {
        this.viewerActions.screenShot(subject);
    }

    public void resetResultMode() {
        this.resetResultMode(null);
    }

    public void resetResultMode(ProjectAction restoredAction) {
        this.storeProjectAnsatz();
        this.resultMode = RESULT_MODE_NULL;
        if (this.restoreProjectAnsatz(restoredAction)) {
            return;
        }

        if (this.viewerButton != null) {
            this.viewerButton.setGraphic(
                    SVGLibrary.getGraphic(SVGData.MENU, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        }

        if (this.editorButton != null) {
            this.editorButton.setDisable(false);
            this.editorButton.setGraphic(
                    SVGLibrary.getGraphic(SVGData.CONTROL, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        }
    }

    public boolean isResultExplorerMode() {
        return this.resultMode == RESULT_MODE_EXPLORER;
    }

    public void setResultExplorerMode() {
        this.setResultExplorerMode(null);
    }

    public void setResultExplorerMode(ProjectAction restoredAction) {
        this.storeProjectAnsatz();
        this.resultMode = RESULT_MODE_EXPLORER;
        if (this.restoreProjectAnsatz(restoredAction)) {
            return;
        }

        if (this.viewerButton != null) {
            this.viewerButton.setGraphic(
                    SVGLibrary.getGraphic(SVGData.ARROW_LEFT, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        }

        if (this.editorButton != null) {
            this.editorButton.setDisable(true);
            this.editorButton.setGraphic(null);
        }

        this.setEditorText("");
    }

    public boolean isResultViewerMode() {
        return this.resultMode == RESULT_MODE_VIEWER;
    }

    public void setResultViewerMode() {
        this.setResultViewerMode(null);
    }

    public void setResultViewerMode(ProjectAction restoredAction) {
        this.storeProjectAnsatz();
        this.resultMode = RESULT_MODE_VIEWER;
        if (this.restoreProjectAnsatz(restoredAction)) {
            return;
        }

        if (this.viewerButton != null) {
            this.viewerButton.setGraphic(
                    SVGLibrary.getGraphic(SVGData.ARROW_LEFT, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        }

        if (this.editorButton != null) {
            this.editorButton.setDisable(true);
            this.editorButton.setGraphic(null);
        }

        this.setEditorText("");
    }

    private ProjectAnsatz getProjectAnsatzAlways() {
        if (this.projectAnsatzMap == null) {
            this.projectAnsatzMap = new HashMap<Integer, ProjectAnsatz>();
        }

        if (!this.projectAnsatzMap.containsKey(this.resultMode)) {
            this.projectAnsatzMap.put(this.resultMode, new ProjectAnsatz());
        }

        return this.projectAnsatzMap.get(this.resultMode);
    }

    private ProjectAnsatz getProjectAnsatzIfPossible() {
        if (this.projectAnsatzMap == null) {
            return null;
        }

        if (!this.projectAnsatzMap.containsKey(this.resultMode)) {
            return null;
        }

        return this.projectAnsatzMap.get(this.resultMode);
    }

    private List<Node> listViewerPaneNodes() {
        if (this.viewerPane != null) {
            List<Node> children = this.viewerPane.getChildren();
            if (children == null || children.isEmpty()) {
                return null;
            }

            List<Node> viewerPaneNodes = new ArrayList<Node>();

            if (this.atomsViewer != null) {
                viewerPaneNodes.add(this.atomsViewer);
                // avoid SubScene and AtomsViewer
                for (int i = 2; i < children.size(); i++) {
                    viewerPaneNodes.add(children.get(i));
                }

            } else {
                for (Node child : children) {
                    viewerPaneNodes.add(child);
                }
            }

            return viewerPaneNodes;
        }

        return null;
    }

    private void restoreViewerPaneNodes(List<Node> viewerPaneNodes) {
        if (viewerPaneNodes == null || viewerPaneNodes.isEmpty()) {
            this.setViewerPane(null);

        } else {
            this.setViewerPane(viewerPaneNodes.get(0));
            for (int i = 1; i < viewerPaneNodes.size(); i++) {
                this.stackOnViewerPane(viewerPaneNodes.get(i));
            }
        }
    }

    private boolean storeProjectAnsatz() {
        ProjectAnsatz projectAnsatz = this.getProjectAnsatzAlways();

        if (projectAnsatz != null) {
            List<Node> viewerPaneNodes = this.listViewerPaneNodes();
            projectAnsatz.setViewerPaneNodes(viewerPaneNodes);

            Node viewerMenuGraphic = this.viewerButton == null ? null : this.viewerButton.getGraphic();
            projectAnsatz.setViewerMenuGraphic(viewerMenuGraphic);

            Node editorPaneNode = this.getEditorPane();
            projectAnsatz.setEditorPaneNode(editorPaneNode);

            Node editorMenuGraphic = this.editorButton == null ? null : this.editorButton.getGraphic();
            projectAnsatz.setEditorMenuGraphic(editorMenuGraphic);

            boolean editorMenuDisable = this.editorButton == null ? false : this.editorButton.isDisable();
            projectAnsatz.setEditorMenuDisable(editorMenuDisable);

            String editorMenuText = this.editorLabel == null ? "" : this.editorLabel.getText();
            projectAnsatz.setEditorMenuText(editorMenuText);

            return true;
        }

        return false;
    }

    private boolean restoreProjectAnsatz(ProjectAction restoredAction) {
        if (restoredAction != null) {
            if (this.restoredActionMap == null) {
                this.restoredActionMap = new HashMap<Integer, ProjectAction>();
            }
            this.restoredActionMap.put(this.resultMode, restoredAction);
        }

        ProjectAnsatz projectAnsatz = this.getProjectAnsatzIfPossible();

        if (projectAnsatz != null) {
            List<Node> viewerPaneNodes = projectAnsatz.getViewerPaneNodes();
            this.restoreViewerPaneNodes(viewerPaneNodes);

            Node viewerMenuGraphic = projectAnsatz.getViewerMenuGraphic();
            if (this.viewerButton != null) {
                this.viewerButton.setGraphic(viewerMenuGraphic);
            }

            Node editorPaneNode = projectAnsatz.getEditorPaneNode();
            this.setEditorPane(editorPaneNode);

            Node editorMenuGraphic = projectAnsatz.getEditorMenuGraphic();
            if (this.editorButton != null) {
                this.editorButton.setGraphic(editorMenuGraphic);
            }

            boolean editorMenuDisable = projectAnsatz.isEditorMenuDisable();
            if (this.editorButton != null) {
                this.editorButton.setDisable(editorMenuDisable);
            }

            String editorMenuText = projectAnsatz.getEditorMenuText();
            if (this.editorLabel != null) {
                this.editorLabel.setText(editorMenuText);
            }

            ProjectAction restoredAction2 = null;
            if (this.restoredActionMap != null) {
                restoredAction2 = this.restoredActionMap.get(this.resultMode);
            }
            if (restoredAction2 != null) {
                restoredAction2.actionOnProject(this);
            }

            return true;
        }

        return false;
    }
}
