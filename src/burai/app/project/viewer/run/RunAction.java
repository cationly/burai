/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.run;

import burai.app.QEFXMainController;
import burai.app.project.QEFXProjectController;
import burai.run.RunningNode;
import burai.run.RunningManager;

public class RunAction {

    private QEFXProjectController controller;

    public RunAction(QEFXProjectController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.controller = controller;
    }

    public void runCalculation(RunningNode runningNode) {
        if (runningNode == null) {
            return;
        }

        RunningManager.getInstance().addNode(runningNode);

        QEFXMainController mainController = this.controller.getMainController();
        if (mainController == null) {
            return;
        }

        mainController.offerOnHomeTabSelected(explorerFacade -> {
            if (explorerFacade != null && (!explorerFacade.isCalculatingMode())) {
                explorerFacade.setCalculatingMode();
            }
        });

        mainController.showHome();
    }
}
