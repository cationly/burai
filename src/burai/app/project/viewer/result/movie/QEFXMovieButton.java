/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.movie;

import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.movie.QEFXMovieEditor;
import burai.app.project.viewer.result.QEFXResultButton;
import burai.project.property.ProjectGeometryList;

public abstract class QEFXMovieButton extends QEFXResultButton<QEFXMovieViewer, QEFXMovieEditor> {

    private ProjectGeometryList projectGeometryList;

    public QEFXMovieButton(QEFXProjectController projectController,
            ProjectGeometryList projectGeometryList, String title, String subTitle) {

        super(projectController, title, subTitle);

        if (projectGeometryList == null) {
            throw new IllegalArgumentException("projectGeometryList is null.");
        }

        this.projectGeometryList = projectGeometryList;
    }

    @Override
    protected QEFXMovieViewer createResultViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        return new QEFXMovieViewer(this.projectController, this.projectGeometryList);
    }

    @Override
    protected QEFXMovieEditor createResultEditor(QEFXMovieViewer resultViewer) throws IOException {
        if (resultViewer == null) {
            return null;
        }

        if (this.projectController == null) {
            return null;
        }

        return new QEFXMovieEditor(this.projectController, resultViewer);
    }
}
