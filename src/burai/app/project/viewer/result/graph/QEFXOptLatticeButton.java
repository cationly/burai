/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph;

import java.io.File;
import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultButtonWrapper;
import burai.project.Project;
import burai.project.property.ProjectGeometryList;
import burai.project.property.ProjectProperty;

public class QEFXOptLatticeButton extends QEFXGraphButton<QEFXLatticeViewer> {

    private static final String FILE_NAME = ".burai.graph.opt.latt";

    private static final String BUTTON_TITLE = "OPT";
    private static final String BUTTON_SUBTITLE = ".latt";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: derive(mediumorchid, -30.0%)";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: snow";

    public static QEFXResultButtonWrapper<QEFXOptLatticeButton> getWrapper(
            QEFXProjectController projectController, Project project, LatticeViewerType lattVType) {

        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        if (lattVType == null) {
            return null;
        }

        ProjectGeometryList projectGeometryList = projectProperty.getOptList();
        if (projectGeometryList == null || projectGeometryList.numGeometries() < 2) {
            return null;
        }

        if (!(new GeometryChecker(projectGeometryList).isAvailableLattice(lattVType))) {
            return null;
        }

        return () -> {
            QEFXOptLatticeButton button = new QEFXOptLatticeButton(projectController, projectGeometryList, lattVType);

            String propPath = project == null ? null : project.getDirectoryPath();
            File propFile = propPath == null ? null : new File(propPath, FILE_NAME);
            if (propFile != null) {
                button.setPropertyFile(propFile);
            }

            return button;
        };
    }

    private LatticeViewerType lattVType;

    private ProjectGeometryList projectGeometryList;

    private QEFXOptLatticeButton(QEFXProjectController projectController,
            ProjectGeometryList projectGeometryList, LatticeViewerType lattVType) {

        super(projectController,
                BUTTON_TITLE, BUTTON_SUBTITLE + "." + (lattVType == null ? "" : lattVType.name()));

        if (projectGeometryList == null) {
            throw new IllegalArgumentException("projectGeometryList is null.");
        }

        if (lattVType == null) {
            throw new IllegalArgumentException("lattVType is null.");
        }

        this.projectGeometryList = projectGeometryList;
        this.lattVType = lattVType;

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }

    @Override
    protected QEFXLatticeViewer createGraphViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        return new QEFXLatticeViewer(this.projectController, this.projectGeometryList, this.lattVType, false);
    }
}
