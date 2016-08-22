/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

public abstract class ViewerComponentBase<A, N extends Node> {

    protected A atomsViewer;

    private N node;
    protected Affine affine;
    protected double scale;
    protected double centerX;
    protected double centerY;
    protected double centerZ;

    public ViewerComponentBase(A atomsViewer) {
        if (atomsViewer == null) {
            throw new IllegalArgumentException("atomsViewer is null.");
        }

        this.atomsViewer = atomsViewer;

        this.node = null;
        this.scale = 1.0;
        this.centerX = 0.0;
        this.centerY = 0.0;
        this.centerZ = 0.0;
    }

    public abstract void initialize();

    protected abstract N createNode();

    public N getNode() {
        if (this.node == null) {
            this.initialize();
            this.node = this.createNode();
            this.node.getTransforms().add(this.affine);
        }

        return this.node;
    }

    public void appendScale(double scale) {
        if (this.affine != null) {
            this.affine.prependScale(
                    scale, scale, scale, this.centerX, this.centerY, this.centerZ);
        }
    }

    public void appendRotation(double angle, double axisX, double axisY, double axisZ) {
        if (this.affine != null) {
            this.affine.prependRotation(
                    angle, this.centerX, this.centerY, this.centerZ, axisX, axisY, axisZ);
        }
    }

    public void appendTranslation(double x, double y, double z) {
        if (this.affine != null) {
            this.affine.prependTranslation(x, y, z);
        }
    }
}
