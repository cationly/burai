/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph;

import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultButtonWrapper;
import burai.project.Project;
import burai.project.property.ProjectGeometryList;
import burai.project.property.ProjectProperty;

public class QEFXMdLatticeButton extends QEFXGraphButton<QEFXLatticeViewer> {

    private static final String BUTTON_TITLE = "MD";
    private static final String BUTTON_SUBTITLE = ".latt";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: derive(limegreen, -20.0%)";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: snow";

    public static QEFXResultButtonWrapper<QEFXMdLatticeButton> getWrapper(
            QEFXProjectController projectController, Project project, LattViewerType lattViewerType) {

        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        if (lattViewerType == null) {
            return null;
        }

        ProjectGeometryList projectGeometryList = projectProperty.getMdList();
        if (projectGeometryList == null || projectGeometryList.numGeometries() < 2) {
            return null;
        }

        if (new GeometryChecker(projectGeometryList).isAvailableLattice(lattViewerType)) {
            return () -> new QEFXMdLatticeButton(projectController, projectGeometryList, lattViewerType);
        }

        return null;
    }

    private LattViewerType lattViewerType;

    private ProjectGeometryList projectGeometryList;

    private QEFXMdLatticeButton(QEFXProjectController projectController,
            ProjectGeometryList projectGeometryList, LattViewerType lattViewerType) {

        super(projectController,
                BUTTON_TITLE, BUTTON_SUBTITLE + "." + (lattViewerType == null ? "" : lattViewerType.name()));

        if (projectGeometryList == null) {
            throw new IllegalArgumentException("projectGeometryList is null.");
        }

        if (lattViewerType == null) {
            throw new IllegalArgumentException("lattViewerType is null.");
        }

        this.projectGeometryList = projectGeometryList;
        this.lattViewerType = lattViewerType;

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }

    @Override
    protected QEFXLatticeViewer createResultViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        return new QEFXLatticeViewer(this.projectController, this.projectGeometryList, this.lattViewerType, true);
    }
}
