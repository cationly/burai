/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.band;

import java.io.File;
import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.band.QEFXBandEditor;
import burai.app.project.viewer.result.QEFXResultButton;
import burai.app.project.viewer.result.QEFXResultButtonWrapper;
import burai.project.Project;
import burai.project.property.BandData;
import burai.project.property.ProjectBand;
import burai.project.property.ProjectBandPaths;
import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;

public class QEFXBandButton extends QEFXResultButton<QEFXBandViewer, QEFXBandEditor> {

    private static final String FILE_NAME = ".burai.graph.band";

    private static final String BUTTON_TITLE = "BAND";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: ivory";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: derive(steelblue, 5.0%)";

    public static QEFXResultButtonWrapper<QEFXBandButton> getWrapper(QEFXProjectController projectController, Project project) {

        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        ProjectStatus projectStatus = projectProperty.getStatus();
        if (projectStatus == null || (!projectStatus.isBandDone())) {
            return null;
        }

        ProjectEnergies projectEnergies = projectProperty.getFermiEnergies();
        if (projectEnergies == null || projectEnergies.numEnergies() < 1) {
            return null;
        }

        ProjectBand projectBand = projectProperty.getBand();
        if (projectBand == null) {
            return null;
        }

        ProjectBandPaths projectBandPaths = projectProperty.getBandPaths();
        if (projectBandPaths == null || projectBandPaths.numPoints() < 1) {
            return null;
        }

        BandData bandData = projectBand.getBandData();
        if (bandData == null) {
            return null;
        }

        String dirPath = project == null ? null : project.getDirectoryPath();
        String fileName = project == null ? null : (project.getPrefixName() + ".band1.gnu");

        File file = null;
        if (dirPath != null && fileName != null) {
            file = new File(dirPath, fileName);
        }

        try {
            if (file == null || (!file.isFile()) || (file.length() <= 0L)) {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return () -> {
            QEFXBandButton button =
                    new QEFXBandButton(projectController, projectEnergies, projectBand, projectBandPaths);

            String propPath = project == null ? null : project.getDirectoryPath();
            File propFile = propPath == null ? null : new File(propPath, FILE_NAME);
            if (propFile != null) {
                button.propertyFile = propFile;
            }

            return button;
        };
    }

    private File propertyFile;

    private ProjectEnergies projectEnergies;

    private ProjectBand projectBand;

    private ProjectBandPaths projectBandPaths;

    private QEFXBandButton(QEFXProjectController projectController,
            ProjectEnergies projectEnergies, ProjectBand projectBand, ProjectBandPaths projectBandPaths) {

        super(projectController, BUTTON_TITLE, null);

        if (projectEnergies == null) {
            throw new IllegalArgumentException("projectEnergies is null.");
        }

        if (projectBand == null) {
            throw new IllegalArgumentException("projectBand is null.");
        }

        this.propertyFile = null;
        this.projectEnergies = projectEnergies;
        this.projectBand = projectBand;
        this.projectBandPaths = projectBandPaths;

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }

    @Override
    protected final QEFXBandViewer createResultViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        QEFXBandViewer viewer = new QEFXBandViewer(
                this.projectController, this.projectEnergies, this.projectBand, this.projectBandPaths);

        if (viewer != null) {
            QEFXBandViewerController controller = viewer.getController();
            if (controller != null) {
                controller.setPropertyFile(this.propertyFile);
            }
        }

        return viewer;
    }

    @Override
    protected final QEFXBandEditor createResultEditor(QEFXBandViewer resultViewer) throws IOException {
        if (resultViewer == null) {
            return null;
        }

        if (this.projectController == null) {
            return null;
        }

        return new QEFXBandEditor(this.projectController, resultViewer);
    }
}
