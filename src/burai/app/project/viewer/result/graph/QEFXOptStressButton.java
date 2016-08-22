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

public class QEFXOptStressButton extends QEFXGraphButton<QEFXStressViewer> {

    private static final String BUTTON_TITLE = "OPT";
    private static final String BUTTON_SUBTITLE = ".stress";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: derive(mediumorchid, -15.0%)";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: snow";

    public static QEFXResultButtonWrapper<QEFXOptStressButton> getWrapper(QEFXProjectController projectController, Project project) {

        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        ProjectGeometryList projectGeometryList = projectProperty.getOptList();
        if (projectGeometryList == null || projectGeometryList.numGeometries() < 1) {
            return null;
        }

        if (!projectGeometryList.hasAnyConvergedGeometries()) {
            return null;
        }

        if (new GeometryChecker(projectGeometryList).isAvailableStress()) {
            return () -> new QEFXOptStressButton(projectController, projectGeometryList);
        }

        return null;
    }

    private ProjectGeometryList projectGeometryList;

    private QEFXOptStressButton(QEFXProjectController projectController, ProjectGeometryList projectGeometryList) {
        super(projectController, BUTTON_TITLE, BUTTON_SUBTITLE);

        if (projectGeometryList == null) {
            throw new IllegalArgumentException("projectGeometryList is null.");
        }

        this.projectGeometryList = projectGeometryList;

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }

    @Override
    protected QEFXStressViewer createResultViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        return new QEFXStressViewer(this.projectController, this.projectGeometryList, false);
    }
}
