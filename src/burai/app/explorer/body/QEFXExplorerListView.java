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

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.KeyCode;
import burai.app.explorer.body.contextmenu.QEFXContextMenu;
import burai.app.icon.QEFXIcon;
import burai.app.icon.QEFXProjectIcon;
import burai.app.icon.QEFXRunningIcon;
import burai.matapi.MaterialsAPILoader;
import burai.project.Project;
import burai.run.RunningNode;

public class QEFXExplorerListView extends QEFXExplorerBody {

    private static final double FIGURE_SIZE = 85.0;

    private ListView<QEFXIcon> listView;

    public QEFXExplorerListView(String directoryName,
            List<Project> shownProjects, MaterialsAPILoader matApiLoader) throws IOException {

        super(directoryName, shownProjects, matApiLoader);
        this.createListView();
        this.showIcons();
    }

    @Override
    public void detachFromParent() {
        super.detachFromParent();

        if (this.listView != null) {
            this.listView.setOnKeyPressed(null);
        }

        List<QEFXIcon> icons = null;
        if (this.listView != null) {
            icons = this.listView.getItems();
        }

        if (icons != null) {
            for (QEFXIcon icon : icons) {
                if (icon != null) {
                    icon.detach();
                }
            }

            icons.clear();
        }
    }

    @Override
    public Node getNode() {
        return this.listView;
    }

    @Override
    protected int indexOfIcon(QEFXIcon icon) {
        if (icon == null) {
            return -1;
        }

        return this.listView.getItems().indexOf(icon);
    }

    @Override
    protected int indexOfIcon(Project project) {
        if (project == null) {
            return -1;
        }

        List<QEFXIcon> icons = this.listView.getItems();
        for (int i = 0; i < icons.size(); i++) {
            QEFXIcon icon = icons.get(i);
            if (icon != null && (icon instanceof QEFXProjectIcon)) {
                Project project2 = ((QEFXProjectIcon) icon).getContent();
                if (project.isSameAs(project2)) {
                    return i;
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

        List<QEFXIcon> icons = this.listView.getItems();
        for (int i = 0; i < icons.size(); i++) {
            QEFXIcon icon = icons.get(i);
            if (icon != null && (icon instanceof QEFXProjectIcon)) {
                Project project2 = ((QEFXProjectIcon) icon).getContent();
                if (project.isSameAs(project2)) {
                    indexList.add(i);
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

        List<QEFXIcon> icons = this.listView.getItems();
        for (int i = 0; i < icons.size(); i++) {
            QEFXIcon icon = icons.get(i);
            if (icon != null && (icon instanceof QEFXRunningIcon)) {
                RunningNode runningNode2 = ((QEFXRunningIcon) icon).getRunningNode();
                if (runningNode.equals(runningNode2)) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    protected QEFXIcon getIconAt(int position) {
        List<QEFXIcon> icons = this.listView.getItems();
        if (0 <= position && position < icons.size()) {
            return icons.get(position);
        } else {
            return null;
        }
    }

    @Override
    protected void onIconShowing(QEFXIcon icon, int position, boolean swapping) {
        if (icon != null) {
            List<QEFXIcon> icons = this.listView.getItems();

            if (0 <= position && position < icons.size()) {
                icons.add(position, icon);
                if (swapping) {
                    icons.remove(position + 1);
                }

            } else {
                icons.add(icon);
            }
        }
    }

    @Override
    protected void onIconDeleted(QEFXIcon icon) {
        if (icon != null) {
            this.listView.getItems().remove(icon);
        }
    }

    @Override
    protected void onIconSearched(QEFXIcon icon) {
        if (icon != null) {
            this.actionOnIconSearched(icon);
        }
    }

    private void createListView() {
        this.listView = new ListView<QEFXIcon>();

        this.listView.setContextMenu(QEFXContextMenu.getContextMenu(null, this));

        this.listView.setCellFactory(listView_ -> {
            return new QEFXListCell(this, FIGURE_SIZE);
        });

        this.listView.setOnKeyPressed(event -> {
            KeyCode code = null;
            if (event != null) {
                code = event.getCode();
            }
            if (code == null) {
                return;
            }

            if (code.equals(KeyCode.ENTER)) {
                this.actionOnEnterPressed();
            } else {
                this.searchIcon(code);
            }
        });
    }

    private void actionOnEnterPressed() {
        MultipleSelectionModel<QEFXIcon> selectionModel = this.listView.getSelectionModel();
        if (selectionModel != null) {
            QEFXIcon icon = selectionModel.getSelectedItem();
            this.selectIcon(icon);
        }
    }

    private void actionOnIconSearched(QEFXIcon icon) {
        this.listView.scrollTo(icon);
        MultipleSelectionModel<QEFXIcon> selectionModel = this.listView.getSelectionModel();
        if (selectionModel != null) {
            selectionModel.select(icon);
        }
    }
}
