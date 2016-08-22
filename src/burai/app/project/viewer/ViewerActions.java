/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer;

import java.io.IOException;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import burai.app.project.ProjectAction;
import burai.app.project.ProjectActions;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.atoms.AtomsAction;
import burai.app.project.viewer.inputfile.QEFXInputFile;
import burai.app.project.viewer.result.ResultAction;
import burai.app.project.viewer.run.QEFXRunDialog;
import burai.app.project.viewer.run.RunAction;
import burai.app.project.viewer.save.SaveAction;
import burai.app.project.viewer.screenshot.QEFXScreenshotDialog;
import burai.project.Project;
import burai.run.RunningNode;

public class ViewerActions extends ProjectActions<Node> {

    private ViewerItemSet itemSet;

    private AtomsAction atomsAction;

    private ResultAction resultAction;

    public ViewerActions(Project project, QEFXProjectController controller) {
        super(project, controller);

        this.itemSet = new ViewerItemSet();

        this.atomsAction = null;
        this.resultAction = null;

        this.setupOnViewerSelected();
        this.setupActions();
    }

    @Override
    public void actionInitially() {
        if (this.controller == null) {
            return;
        }

        this.controller.addViewerMenuItems(this.itemSet.getItems());

        ProjectAction action = this.actions.get(this.itemSet.getAtomsViewerItem());
        if (action != null) {
            action.actionOnProject(this.controller);
        }
    }

    public boolean saveFile() {
        return this.actionSaveFile(this.controller);
    }

    public void screenShot() {
        this.actionScreenShot(this.controller);
    }

    private void setupOnViewerSelected() {
        if (this.controller == null) {
            return;
        }

        this.controller.setOnViewerSelected(graphic -> {
            if (graphic == null) {
                return;
            }

            ProjectAction action = null;
            if (this.actions != null) {
                action = this.actions.get(graphic);
            }

            if (action != null && this.controller != null) {
                action.actionOnProject(this.controller);
            }
        });
    }

    private void setupActions() {
        ViewerItem[] items = this.itemSet.getItems();
        for (ViewerItem item : items) {
            if (item == null) {
                continue;
            }

            if (item == this.itemSet.getAtomsViewerItem()) {
                this.actions.put(item, controller2 -> this.actionAtomsViewer(controller2));

            } else if (item == this.itemSet.getInputFileItem()) {
                this.actions.put(item, controller2 -> this.actionInputFile(controller2));

            } else if (item == this.itemSet.getSaveFileItem()) {
                this.actions.put(item, controller2 -> this.actionSaveFile(controller2));

            } else if (item == this.itemSet.getSaveAsFileItem()) {
                this.actions.put(item, controller2 -> this.actionSaveAsFile(controller2));

            } else if (item == this.itemSet.getScreenShotItem()) {
                this.actions.put(item, controller2 -> this.actionScreenShot(controller2));

            } else if (item == this.itemSet.getRunItem()) {
                this.actions.put(item, controller2 -> this.actionRun(controller2));

            } else if (item == this.itemSet.getResultItem()) {
                this.actions.put(item, controller2 -> this.actionResult(controller2));
            }
        }
    }

    private void actionAtomsViewer(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        if (this.atomsAction == null || controller != this.atomsAction.getController()) {
            this.atomsAction = new AtomsAction(this.project, controller);
        }

        if (this.atomsAction != null) {
            this.atomsAction.showAtoms();
        }
    }

    private void actionInputFile(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        this.project.resolveQEInputs();

        try {
            QEFXInputFile inputFile = new QEFXInputFile(controller, this.project);
            controller.clearStackedsOnViewerPane();
            controller.stackOnViewerPane(inputFile.getNode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean actionSaveFile(QEFXProjectController controller) {
        if (controller == null) {
            return false;
        }

        SaveAction saveAction = new SaveAction(this.project, controller);
        return saveAction.saveProject();
    }

    private void actionSaveAsFile(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        SaveAction saveAction = new SaveAction(this.project, controller);
        saveAction.saveProjectAsNew();
    }

    private void actionScreenShot(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        QEFXScreenshotDialog dialog = new QEFXScreenshotDialog(controller, this.project);
        Optional<ButtonType> optButtonType = dialog.showAndWait();

        if (optButtonType != null && optButtonType.isPresent() && optButtonType.get() == ButtonType.YES) {
            try {
                dialog.saveImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void actionRun(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        this.project.resolveQEInputs();

        QEFXRunDialog dialog = new QEFXRunDialog(this.project, this);
        Optional<RunningNode> optButtonType = dialog.showAndWait();

        if (optButtonType != null && optButtonType.isPresent()) {
            RunningNode runningNode = optButtonType.get();
            if (runningNode != null) {
                RunAction runAction = new RunAction(controller);
                runAction.runCalculation(runningNode);
            }
        }
    }

    private void actionResult(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        if (this.resultAction == null || controller != this.resultAction.getController()) {
            this.resultAction = new ResultAction(this.project, controller);
        }

        if (this.resultAction != null) {
            this.resultAction.showResult();
        }
    }
}
