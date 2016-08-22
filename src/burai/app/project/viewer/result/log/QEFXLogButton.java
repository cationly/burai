/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.log;

import java.io.File;
import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.log.QEFXLogEditor;
import burai.app.project.viewer.result.QEFXResultButton;

public abstract class QEFXLogButton extends QEFXResultButton<QEFXLogViewer, QEFXLogEditor> {

    private File file;

    public QEFXLogButton(QEFXProjectController projectController, String title, String subTitle, File file) {
        super(projectController, title, subTitle);

        if (file == null) {
            throw new IllegalArgumentException("file is null.");
        }

        this.file = file;
    }

    @Override
    protected QEFXLogViewer createResultViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        return new QEFXLogViewer(this.projectController, this.file);
    }

    @Override
    protected QEFXLogEditor createResultEditor(QEFXLogViewer resultViewer) throws IOException {
        if (resultViewer == null) {
            return null;
        }

        if (this.projectController == null) {
            return null;
        }

        return new QEFXLogEditor(this.projectController, resultViewer);
    }
}
