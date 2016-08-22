/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer;

import javafx.scene.control.Button;
import burai.app.explorer.body.QEFXExplorerBody;
import burai.com.env.Environments;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class ButtonsSetupper extends ExplorerSetupper {

    private static final double GRAPHIC_SIZE = 20.0;

    protected ButtonsSetupper(QEFXExplorerController controller) {
        super(controller);
    }

    protected void setupRecentButton(Button recentButton) {
        if (recentButton == null) {
            return;
        }

        recentButton.setOnAction(event -> {
            this.controller.getLocationField().setText(QEFXExplorerBody.CODE_RECENTLY_USED);
            this.controller.updateExplorerBody();
        });
    }

    protected void setupComputerButton(Button computerButton) {
        if (computerButton == null) {
            return;
        }

        computerButton.setOnAction(event -> {
            String rootPath = Environments.getRootPath();
            this.controller.getLocationField().setText(rootPath == null ? "" : rootPath);
            this.controller.updateExplorerBody();
        });
    }

    protected void setupProjectsButton(Button projectsButton) {
        if (projectsButton == null) {
            return;
        }

        projectsButton.setOnAction(event -> {
            String projPath = Environments.getProjectsPath();
            this.controller.getLocationField().setText(projPath == null ? "" : projPath);
            this.controller.updateExplorerBody();
        });
    }

    protected void setupCalculatingButton(Button calculatingButton) {
        if (calculatingButton == null) {
            return;
        }

        calculatingButton.setOnAction(event -> {
            this.controller.getLocationField().setText(QEFXExplorerBody.CODE_CALCULATING);
            this.controller.updateExplorerBody();
        });
    }

    protected void setupSearchedButton(Button searchedButton) {
        if (searchedButton == null) {
            return;
        }

        searchedButton.setOnAction(event -> {
            this.controller.getLocationField().setText(QEFXExplorerBody.CODE_SEARCHED);
            this.controller.updateExplorerBody();
        });
    }

    protected void setupWebButton(Button webButton) {
        if (webButton == null) {
            return;
        }

        webButton.setText(" " + webButton.getText());
        webButton.setGraphic(SVGLibrary.getGraphic(SVGData.EARTH, GRAPHIC_SIZE));

        webButton.setOnAction(event -> {
            this.controller.getLocationField().setText(QEFXExplorerBody.CODE_WEB);
            this.controller.updateExplorerBody();
        });
    }

    protected void setupDownloadsButton(Button downloadsButton) {
        if (downloadsButton == null) {
            return;
        }

        downloadsButton.setOnAction(event -> {
            String downloadsPath = Environments.getDownloadsPath();
            this.controller.getLocationField().setText(downloadsPath == null ? "" : downloadsPath);
            this.controller.updateExplorerBody();
        });
    }
}
