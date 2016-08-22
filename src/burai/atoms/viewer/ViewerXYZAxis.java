/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer;

import burai.atoms.visible.XYZAxis;
import javafx.geometry.Point3D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class ViewerXYZAxis extends ViewerComponent<XYZAxis> {

    private boolean keepOperation;

    public ViewerXYZAxis(AtomsViewer atomsViewer) {
        super(atomsViewer);

        this.keepOperation = false;
    }

    public void initialize(boolean keepOperation) {
        this.keepOperation = keepOperation;
        this.initialize();
        this.keepOperation = false;
    }

    @Override
    public void initialize() {
        double width = this.atomsViewer.getSceneWidth();
        double height = this.atomsViewer.getSceneHeight();
        double rangeScene = Math.min(width, height);

        double scaleOld = this.scale;
        double centerXOld = this.centerX;
        double centerYOld = this.centerY;
        double centerZOld = this.centerZ;

        this.scale = 0.06 * rangeScene;
        this.centerX = 0.85 * width;
        this.centerY = 0.85 * height;
        this.centerZ = -0.40 * rangeScene;

        if (this.affine == null) {
            this.affine = new Affine();
        }

        if (this.keepOperation) {
            this.affine.prependTranslation(-centerXOld, -centerYOld, -centerZOld);
            this.affine.prependScale(1.0 / scaleOld, 1.0 / scaleOld, 1.0 / scaleOld);
        } else {
            this.affine.setToIdentity();
            this.affine.prependRotation(180.0, Point3D.ZERO, Rotate.Y_AXIS);
            this.affine.prependRotation(180.0, Point3D.ZERO, Rotate.Z_AXIS);
        }

        this.affine.prependScale(this.scale, this.scale, this.scale);
        this.affine.prependTranslation(this.centerX, this.centerY, this.centerZ);
    }

    @Override
    protected XYZAxis createNode() {
        return new XYZAxis();
    }
}
