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
import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectProperty;

public class QEFXScfButton extends QEFXGraphButton<QEFXScfViewer> {

    private static final String FILE_NAME = ".burai.graph.scf.ene";

    private static final String BUTTON_TITLE = "SCF";
    private static final String BUTTON_SUBTITLE = ".ene";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: derive(red, 20.0%)";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: snow";

    public static QEFXResultButtonWrapper<QEFXScfButton> getWrapper(QEFXProjectController projectController, Project project) {

        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        ProjectEnergies projectEnergies = projectProperty.getScfEnergies();
        if (projectEnergies == null || projectEnergies.numEnergies() < 1) {
            return null;
        }

        return () -> {
            QEFXScfButton button = new QEFXScfButton(projectController, projectEnergies);

            String propPath = project == null ? null : project.getDirectoryPath();
            File propFile = propPath == null ? null : new File(propPath, FILE_NAME);
            if (propFile != null) {
                button.setPropertyFile(propFile);
            }

            return button;
        };
    }

    private ProjectEnergies projectEnergies;

    private QEFXScfButton(QEFXProjectController projectController, ProjectEnergies projectEnergies) {
        super(projectController, BUTTON_TITLE, BUTTON_SUBTITLE);

        if (projectEnergies == null) {
            throw new IllegalArgumentException("projectEnergies is null.");
        }

        this.projectEnergies = projectEnergies;

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }

    @Override
    protected QEFXScfViewer createGraphViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        return new QEFXScfViewer(this.projectController, this.projectEnergies);
    }
}
