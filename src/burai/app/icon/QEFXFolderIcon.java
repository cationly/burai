/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.icon;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class QEFXFolderIcon extends QEFXIconBase<String> {

    public QEFXFolderIcon(String dirPath) {
        super(dirPath);
    }

    @Override
    public void detach() {
        this.content = null;
    }

    @Override
    public Node getFigure(double size) {
        if (size <= 0.0) {
            return null;
        }

        double scaledSize = size * ICON_SCALE;
        double insetsSize = 0.5 * (size - scaledSize);
        Node figure = SVGLibrary.getGraphic(SVGData.FOLDER, scaledSize, null, "icon-folder");
        BorderPane.setMargin(figure, new Insets(insetsSize));
        return figure;
    }

    @Override
    protected String initCaption() {
        File dirFile = new File(this.content);
        String dirName = dirFile.getName();
        if (dirName == null || dirName.trim().isEmpty()) {
            dirName = this.content;
        }

        return dirName;
    }

    @Override
    protected String initSubCaption() {
        return this.getFileDetail(this.content);
    }
}
