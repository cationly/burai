/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.editor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.visible.VisibleAtom;

public class EditorMenu {

    private ViewerEventManager manager;

    private boolean itemInAction;

    private Set<EditorMenuItem> itemSet;

    private ContextMenu contextMenu;

    public EditorMenu(ViewerEventManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("manager is null.");
        }

        this.manager = manager;

        this.itemInAction = false;

        this.createItemSet();
        this.createContextMenu();
    }

    private void createItemSet() {
        this.itemSet = new LinkedHashSet<EditorMenuItem>();
        this.itemSet.add(new PutMenuItem(this));
        //this.itemSet.add(new DetailMenuItem(this));
        this.itemSet.add(new MoveMenuItem(this));
        this.itemSet.add(new DeleteMenuItem(this));
        this.itemSet.add(new RenameMenuItem(this));
        this.itemSet.add(new SelectAllMenuItem(this));
        this.itemSet.add(new NotSelectAnyMenuItem(this));
        this.itemSet.add(new CenterMenuItem(this));
        this.itemSet.add(new UndoMenuItem(this));
        this.itemSet.add(new RedoMenuItem(this));
    }

    private void createContextMenu() {
        this.contextMenu = new ContextMenu();
        double opacity = this.contextMenu.getOpacity();
        this.contextMenu.setOpacity(Math.min(0.85, opacity));

        this.contextMenu.setOnHidden(event -> {
            if (!this.isItemInAction()) {
                this.manager.setPrincipleAtom(null);
                this.manager.removeEditorMenu();
            }
        });

        List<MenuItem> items = this.contextMenu.getItems();
        for (EditorMenuItem item : this.itemSet) {
            items.add(item);
        }
    }

    public synchronized void setItemInAction(boolean itemInAction) {
        this.itemInAction = itemInAction;
    }

    public synchronized boolean isItemInAction() {
        return this.itemInAction;
    }

    protected ViewerEventManager getManager() {
        return this.manager;
    }

    private boolean areAnyAtomsSelected() {
        AtomsViewer atomsViewer = this.manager.getAtomsViewer();
        if (atomsViewer == null) {
            return false;
        }

        List<VisibleAtom> visibleAtoms = atomsViewer.getVisibleAtoms();
        if (visibleAtoms == null) {
            return false;
        }

        for (VisibleAtom visibleAtom : visibleAtoms) {
            if (visibleAtom.isSelected()) {
                return true;
            }
        }

        return false;
    }

    private boolean areAllAtomsSelected() {
        AtomsViewer atomsViewer = this.manager.getAtomsViewer();
        if (atomsViewer == null) {
            return false;
        }

        List<VisibleAtom> visibleAtoms = atomsViewer.getVisibleAtoms();
        if (visibleAtoms == null) {
            return false;
        }

        for (VisibleAtom visibleAtom : visibleAtoms) {
            if (!visibleAtom.isSelected()) {
                return false;
            }
        }

        return true;
    }

    public void show(MouseEvent event) {
        if (event == null) {
            return;
        }

        this.show(event.getScreenX(), event.getScreenY());
    }

    public void show(double screenX, double screenY) {
        AtomsViewer atomsViewer = this.manager.getAtomsViewer();
        if (atomsViewer == null) {
            return;
        }

        boolean atomIsPicked = (this.manager.getPrincipleAtom() != null);
        boolean anyAreSelected = this.areAnyAtomsSelected();
        boolean allAreSelected = this.areAllAtomsSelected();

        for (EditorMenuItem item : this.itemSet) {
            if (item instanceof PutMenuItem) {
                item.setDisable(atomIsPicked);

            } else if (item instanceof DetailMenuItem) {
                item.setDisable(!atomIsPicked);

            } else if (item instanceof MoveMenuItem) {
                item.setDisable(!(anyAreSelected && atomIsPicked));

            } else if (item instanceof DeleteMenuItem) {
                item.setDisable(!anyAreSelected);

            } else if (item instanceof RenameMenuItem) {
                item.setDisable(!anyAreSelected);

            } else if (item instanceof SelectAllMenuItem) {
                item.setDisable(allAreSelected);

            } else if (item instanceof NotSelectAnyMenuItem) {
                item.setDisable(!anyAreSelected);

            } else if (item instanceof UndoMenuItem) {
                item.setDisable(!atomsViewer.canRestoreCell());

            } else if (item instanceof RedoMenuItem) {
                item.setDisable(!atomsViewer.canSubRestoreCell());
            }
        }

        if (this.contextMenu != null) {
            this.contextMenu.show(atomsViewer, screenX, screenY);
        }
    }
}
