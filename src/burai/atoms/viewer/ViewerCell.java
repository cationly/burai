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
import burai.atoms.visible.VisibleCell;
import burai.com.math.Matrix3D;
import javafx.geometry.Point3D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class ViewerCell extends ViewerComponent<VisibleCell> {

    private Cell cell;

    private boolean keepOperation;

    public ViewerCell(AtomsViewer atomsViewer, Cell cell) {
        super(atomsViewer);

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;
        this.keepOperation = false;
    }

    public void initialize(boolean keepOperation) {
        this.keepOperation = keepOperation;
        this.initialize();
        this.keepOperation = false;
    }

    @Override
    public void initialize() {
        double[][] lattice = this.cell.copyLattice();
        double[] center = { 0.5, 0.5, 0.5 };
        double[] latticeCenter = Matrix3D.mult(center, lattice);
        double rangeLattice = 0.0;
        rangeLattice += Matrix3D.norm2(lattice[0]);
        rangeLattice += Matrix3D.norm2(lattice[1]);
        rangeLattice += Matrix3D.norm2(lattice[2]);
        rangeLattice = Math.sqrt(rangeLattice);
        double width = this.atomsViewer.getSceneWidth();
        double height = this.atomsViewer.getSceneHeight();
        double rangeScene = Math.min(width, height);

        double scaleOld = this.scale;
        double centerXOld = this.centerX;
        double centerYOld = this.centerY;
        double centerZOld = this.centerZ;

        this.scale = 0.7 * rangeScene / rangeLattice;
        this.centerX = 0.5 * width;
        this.centerY = 0.5 * height;
        this.centerZ = 0.0;

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
            this.affine.prependTranslation(-latticeCenter[0], latticeCenter[1], latticeCenter[2]);
        }

        this.affine.prependScale(this.scale, this.scale, this.scale);
        this.affine.prependTranslation(this.centerX, this.centerY, this.centerZ);
    }

    @Override
    protected VisibleCell createNode() {
        return new VisibleCell(this.cell);
    }

    public boolean isInCell(double sceneX, double sceneY, double sceneZ) {
        VisibleCell visibleCell = this.getNode();
        if (visibleCell == null) {
            return false;
        }

        Point3D point3d = visibleCell.sceneToLocal(sceneX, sceneY, sceneZ);
        double x = point3d.getX();
        double y = point3d.getY();
        double z = point3d.getZ();

        return visibleCell.getModel().isInCell(x, y, z);
    }
}
