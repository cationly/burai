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

public class QEFXOptEnergyButton extends QEFXGraphButton<QEFXEnergyViewer> {

    private static final String FILE_NAME = ".burai.graph.opt.ene";

    private static final String BUTTON_TITLE = "OPT";
    private static final String BUTTON_SUBTITLE = ".ene";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: mediumorchid";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: snow";

    public static QEFXResultButtonWrapper<QEFXOptEnergyButton> getWrapper(QEFXProjectController projectController, Project project) {

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

        return () -> {
            QEFXOptEnergyButton button = new QEFXOptEnergyButton(projectController, projectGeometryList);

            String propPath = project == null ? null : project.getDirectoryPath();
            File propFile = propPath == null ? null : new File(propPath, FILE_NAME);
            if (propFile != null) {
                button.setPropertyFile(propFile);
            }

            return button;
        };
    }

    private ProjectGeometryList projectGeometryList;

    private QEFXOptEnergyButton(QEFXProjectController projectController, ProjectGeometryList projectGeometryList) {
        super(projectController, BUTTON_TITLE, BUTTON_SUBTITLE);

        if (projectGeometryList == null) {
            throw new IllegalArgumentException("projectGeometryList is null.");
        }

        this.projectGeometryList = projectGeometryList;

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }

    @Override
    protected QEFXEnergyViewer createGraphViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        return new QEFXEnergyViewer(this.projectController, this.projectGeometryList, EnergyType.TOTAL, false);
    }
}
