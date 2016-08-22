/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer;

import burai.atoms.model.Cell;
import burai.atoms.visible.AtomsSample;
import javafx.scene.transform.Affine;

public class ViewerSample extends ViewerComponent<AtomsSample> {

    private Cell cell;

    public ViewerSample(AtomsViewer atomsViewer, Cell cell) {
        super(atomsViewer);

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;
    }

    @Override
    public void initialize() {
        double width = this.atomsViewer.getSceneWidth();
        double height = this.atomsViewer.getSceneHeight();
        double rangeScene = Math.min(width, height);

        this.scale = 0.05 * rangeScene;
        this.centerX = 0.065 * width;
        this.centerY = 0.065 * height;
        this.centerZ = -0.40 * rangeScene;

        if (this.affine == null) {
            this.affine = new Affine();
        }

        this.affine.setToIdentity();
        this.affine.prependScale(this.scale, this.scale, this.scale);
        this.affine.prependTranslation(this.centerX, this.centerY, this.centerZ);
    }

    @Override
    protected AtomsSample createNode() {
        return new AtomsSample(this.cell);
    }
}
