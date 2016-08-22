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

import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultButtonWrapper;
import burai.project.Project;

public class QEFXErrorButton extends QEFXLogButton {

    private static final String BUTTON_TITLE = "ERR";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: ivory";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: derive(deepskyblue, -24.0%)";

    public static QEFXResultButtonWrapper<QEFXErrorButton> getWrapper(QEFXProjectController projectController, Project project, int index) {

        String dirPath = project == null ? null : project.getDirectoryPath();
        String fileName = project == null ? null : project.getErrFileName(index);

        File file = null;
        if (dirPath != null && fileName != null) {
            file = new File(dirPath, fileName);
        }

        try {
            if (file != null && file.isFile() && (file.length() > 0L)) {
                final File file_ = file;
                return () -> new QEFXErrorButton(projectController, file_, index);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private QEFXErrorButton(QEFXProjectController projectController, File file, int index) {
        super(projectController, BUTTON_TITLE, "#" + index, file);

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }
}
