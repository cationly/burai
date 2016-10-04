/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.result;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import burai.app.QEFXAppController;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultViewerController;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public abstract class QEFXResultEditorController<V extends QEFXResultViewerController> extends QEFXAppController {

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "piclight-button";

    protected QEFXProjectController projectController;

    protected V viewerController;

    @FXML
    private Button reloadButton;

    @FXML
    private Button screenButton;

    public QEFXResultEditorController(QEFXProjectController projectController, V viewerController) {
        super(projectController == null ? null : projectController.getMainController());

        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (viewerController == null) {
            throw new IllegalArgumentException("viewerController is null.");
        }

        this.projectController = projectController;
        this.viewerController = viewerController;
    }

    public void reload() {
        if (this.viewerController != null) {
            this.viewerController.reloadSafely();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupReloadButton();
        this.setupScreenButton();
        this.setupFXComponents();
    }

    protected abstract void setupFXComponents();

    private void setupReloadButton() {
        if (this.reloadButton == null) {
            return;
        }

        this.reloadButton.setText("");
        this.reloadButton.setGraphic(SVGLibrary.getGraphic(SVGData.ARROW_ROUND, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.reloadButton.setOnAction(event -> {
            if (this.viewerController != null) {
                this.viewerController.reloadSafely();
            }
        });
    }

    private void setupScreenButton() {
        if (this.screenButton == null) {
            return;
        }

        this.screenButton.setText("");
        this.screenButton.setGraphic(SVGLibrary.getGraphic(SVGData.CAMERA, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.screenButton.setOnAction(event -> {
            if (this.projectController != null) {
                this.projectController.sceenShot();
            }
        });
    }
}
