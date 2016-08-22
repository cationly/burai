/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.movie;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultViewerController;
import burai.project.property.ProjectGeometryList;

public class QEFXMovieViewerController extends QEFXResultViewerController {

    private ProjectGeometryList projectGeometryList;

    @FXML
    private BorderPane basePane;

    public QEFXMovieViewerController(QEFXProjectController projectController, ProjectGeometryList projectGeometryList) {
        super(projectController);

        if (projectGeometryList == null) {
            throw new IllegalArgumentException("projectGeometryList is null.");
        }

        this.projectGeometryList = projectGeometryList;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO
    }

    @Override
    public void reload() {
        // TODO
    }

}
