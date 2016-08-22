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

public class QEFXInputButton extends QEFXLogButton {

    private static final String BUTTON_TITLE = "IN";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: ivory";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: deepskyblue";

    public static QEFXResultButtonWrapper<QEFXInputButton> getWrapper(QEFXProjectController projectController, Project project) {

        String dirPath = project == null ? null : project.getDirectoryPath();
        String fileName = project == null ? null : project.getInpFileName();

        File file = null;
        if (dirPath != null && fileName != null) {
            file = new File(dirPath, fileName);
        }

        try {
            if (file != null && file.isFile() && (file.length() > 0L)) {
                final File file_ = file;
                return () -> new QEFXInputButton(projectController, file_);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private QEFXInputButton(QEFXProjectController projectController, File file) {
        super(projectController, BUTTON_TITLE, null, file);

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }
}
