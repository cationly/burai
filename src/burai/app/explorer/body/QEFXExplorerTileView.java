/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.PickResult;
import javafx.scene.layout.TilePane;
import burai.app.explorer.body.contextmenu.QEFXContextMenu;
import burai.app.icon.QEFXIcon;
import burai.app.icon.QEFXProjectIcon;
import burai.app.icon.QEFXRunningIcon;
import burai.matapi.MaterialsAPILoader;
import burai.project.Project;
import burai.run.RunningNode;

public class QEFXExplorerTileView extends QEFXExplorerBody {

    private static final double MIN_PANE_HEIGHT = 100.0;
    private static final double MIN_PANE_WIDTH = 100.0;

    private ScrollPane scrollPane;

    private TilePane tilePane;

    private double iconSize;

    public QEFXExplorerTileView(String directoryName,
            List<Project> shownProjects, MaterialsAPILoader matApiLoader, double iconSize) throws IOException {

        super(directoryName, shownProjects, matApiLoader);

        if (iconSize <= 0.0) {
            throw new IllegalArgumentException("iconSize is not positive.");
        }

        this.iconSize = iconSize;

        this.createScrollPane();
        this.createTilePane();
        this.showIcons();
    }

    @Override
    public void detachFromParent() {
        super.detachFromParent();

        if (this.scrollPane != null) {
            this.scrollPane.setOnKeyPressed(null);
        }

        List<Node> nodes = null;
        if (this.tilePane != null) {
            nodes = this.tilePane.getChildren();
        }

        if (nodes != null) {
            for (Node node : nodes) {
                if (node != null && (node instanceof QEFXTileCell)) {
                    QEFXIcon icon = ((QEFXTileCell) node).getIcon();
                    if (icon != null) {
                        icon.detach();
                    }
                }
            }

            nodes.clear();
        }
    }

    @Override
    public Node getNode() {
        return this.scrollPane;
    }

    @Override
    protected int indexOfIcon(QEFXIcon icon) {
        if (icon == null) {
            return -1;
        }

        List<Node> children = this.tilePane.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (child != null && (child instanceof QEFXTileCell)) {
                QEFXTileCell tileCell = (QEFXTileCell) child;
                if (icon.equals(tileCell.getIcon())) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    protected int indexOfIcon(Project project) {
        if (project == null) {
            return -1;
        }

        List<Node> children = this.tilePane.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (child != null && (child instanceof QEFXTileCell)) {
                QEFXTileCell tileCell = (QEFXTileCell) child;
                QEFXIcon icon = tileCell.getIcon();
                if (icon != null && (icon instanceof QEFXProjectIcon)) {
                    Project project2 = ((QEFXProjectIcon) icon).getContent();
                    if (project.isSameAs(project2)) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    @Override
    protected int[] indexOfAllIcons(Project project) {
        if (project == null) {
            return null;
        }

        List<Integer> indexList = new ArrayList<Integer>();

        List<Node> children = this.tilePane.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (child != null && (child instanceof QEFXTileCell)) {
                QEFXTileCell tileCell = (QEFXTileCell) child;
                QEFXIcon icon = tileCell.getIcon();
                if (icon != null && (icon instanceof QEFXProjectIcon)) {
                    Project project2 = ((QEFXProjectIcon) icon).getContent();
                    if (project.isSameAs(project2)) {
                        indexList.add(i);
                    }
                }
            }
        }

        if (indexList.isEmpty()) {
            return null;
        }

        int[] indexes = new int[indexList.size()];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = indexList.get(i);
        }

        return indexes;
    }

    @Override
    protected int indexOfIcon(RunningNode runningNode) {
        if (runningNode == null) {
            return -1;
        }

        List<Node> children = this.tilePane.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (child != null && (child instanceof QEFXTileCell)) {
                QEFXTileCell tileCell = (QEFXTileCell) child;
                QEFXIcon icon = tileCell.getIcon();
                if (icon != null && (icon instanceof QEFXRunningIcon)) {
                    RunningNode runningNode2 = ((QEFXRunningIcon) icon).getRunningNode();
                    if (runningNode.equals(runningNode2)) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    @Override
    protected QEFXIcon getIconAt(int position) {
        List<Node> children = this.tilePane.getChildren();
        if (0 <= position && position < children.size()) {
            Node child = children.get(position);
            if (child != null && (child instanceof QEFXTileCell)) {
                QEFXTileCell tileCell = (QEFXTileCell) child;
                return tileCell.getIcon();
            }
        }

        return null;
    }

    @Override
    protected void onIconShowing(QEFXIcon icon, int position, boolean swapping) {
        QEFXTileCell tileCell = null;
        if (icon != null) {
            tileCell = new QEFXTileCell(this, icon, this.iconSize);
        }

        if (tileCell != null) {
            List<Node> children = this.tilePane.getChildren();

            if (0 <= position && position < children.size()) {
                children.add(position, tileCell);
                if (swapping) {
                    children.remove(position + 1);
                }

            } else {
                children.add(tileCell);
            }
        }
    }

    @Override
    protected void onIconDeleted(QEFXIcon icon) {
        if (icon != null) {
            int index = this.indexOfIcon(icon);
            if (index > -1) {
                this.tilePane.getChildren().remove(index);
            }
        }
    }

    @Override
    protected void onIconSearched(QEFXIcon icon) {
        if (icon != null) {
            this.actionOnIconSearched(icon);
        }
    }

    public void createScrollPane() {
        this.scrollPane = new ScrollPane();
        this.scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setFitToHeight(true);
        this.scrollPane.setFitToWidth(true);
        this.scrollPane.setPrefHeight(MIN_PANE_HEIGHT);
        this.scrollPane.setPrefWidth(MIN_PANE_WIDTH);
        this.scrollPane.setPannable(false);
        this.scrollPane.setFocusTraversable(true);
        this.scrollPane.setOnKeyPressed(event -> {
            KeyCode code = null;
            if (event != null) {
                code = event.getCode();
            }
            if (code != null) {
                this.searchIcon(code);
            }
        });
    }

    private void createTilePane() {
        this.tilePane = new TilePane();
        this.tilePane.getStyleClass().add("tile-pane");

        this.tilePane.setOnMouseClicked(event -> this.scrollPane.requestFocus());

        ContextMenu contextMenu = QEFXContextMenu.getContextMenu(null, this);
        this.tilePane.setOnMousePressed(event -> {
            PickResult pickResult = null;
            if (event != null) {
                pickResult = event.getPickResult();
            }

            Node pickNode = null;
            if (pickResult != null) {
                pickNode = pickResult.getIntersectedNode();
            }

            if (pickNode == this.tilePane && event != null && event.isSecondaryButtonDown()) {
                contextMenu.show(this.tilePane, event.getScreenX(), event.getScreenY());
            } else {
                contextMenu.hide();
            }
        });

        this.scrollPane.setContent(this.tilePane);
    }

    private void actionOnIconSearched(QEFXIcon icon) {
        if (icon == null) {
            return;
        }

        QEFXTileCell tileCell = null;
        List<Node> children = this.tilePane.getChildren();
        for (Node child : children) {
            if (child != null && (child instanceof QEFXTileCell)) {
                QEFXTileCell tileCell2 = (QEFXTileCell) child;
                if (icon.equals(tileCell2.getIcon())) {
                    tileCell = tileCell2;
                    break;
                }
            }
        }

        if (tileCell != null) {
            double height = this.tilePane.getHeight() - this.scrollPane.getHeight();
            if (height > 0.0) {
                double vmin = this.scrollPane.getVmin();
                double vmax = this.scrollPane.getVmax();
                Point2D point = tileCell.localToParent(0.0, 0.0);
                if (point != null) {
                    double y = point.getY();
                    double vy = y * (vmax - vmin) / height + vmin;
                    this.scrollPane.setVvalue(Math.min(Math.max(vmin, vy), vmax));
                }
            }
        }
    }
}
