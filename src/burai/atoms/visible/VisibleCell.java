/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.visible;

import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import burai.atoms.model.Atom;
import burai.atoms.model.Bond;
import burai.atoms.model.Cell;
import burai.atoms.model.event.CellEvent;
import burai.atoms.model.event.CellEventListener;
import burai.com.math.Matrix3D;

public class VisibleCell extends Visible<Cell> implements CellEventListener {

    private static final double CYLINDER_RADIUS = 0.0015;
    private static final double CYLINDER_BOLD_SCALE = 4.0;
    private static final int CYLINDER_DIV = 6;

    private boolean boldMode;

    private Cylinder[] latticeCylinders;

    public VisibleCell(Cell cell) {
        this(cell, false);
    }

    public VisibleCell(Cell cell, boolean boldMode) {
        super(cell);

        this.model.addListener(this);

        this.boldMode = boldMode;
        this.latticeCylinders = new Cylinder[12];
        for (int i = 0; i < latticeCylinders.length; i++) {
            this.latticeCylinders[i] = new Cylinder(1.0, 1.0, CYLINDER_DIV);
        }

        this.updateRadiusOfCylinders();
        this.updateXYZOfCylinders();
        this.updateColorOfCylinders();

        for (int i = 0; i < latticeCylinders.length; i++) {
            this.getChildren().add(this.latticeCylinders[i]);
        }

        this.initChildren();
    }

    private VisibleAtom createVisibleAtom(Atom atom) {
        boolean disableToSelect = false;
        if (!this.boldMode) {
            disableToSelect = false;
        } else {
            disableToSelect = true;
        }

        VisibleAtom visibleAtom = new VisibleAtom(atom, disableToSelect, this.boldMode);
        this.toBeFlushedProperty().addListener(o -> visibleAtom.setToBeFlushed(this.isToBeFlushed()));
        return visibleAtom;
    }

    private VisibleBond createVisibleBond(Bond bond) {
        VisibleBond visibleBond = new VisibleBond(bond, this.boldMode);
        this.toBeFlushedProperty().addListener(o -> visibleBond.setToBeFlushed(this.isToBeFlushed()));
        return visibleBond;
    }

    private void initChildren() {
        List<Node> children = this.getChildren();

        Atom[] atoms = this.model.listAtoms();
        if (atoms != null) {
            for (Atom atom : atoms) {
                children.add(this.createVisibleAtom(atom));
            }
        }

        Bond[] bonds = this.model.listBonds();
        if (bonds != null) {
            for (Bond bond : bonds) {
                children.add(this.createVisibleBond(bond));
            }
        }
    }

    private void updateRadiusOfCylinders() {
        double boldScale = 1.0;
        if (this.boldMode) {
            boldScale = CYLINDER_BOLD_SCALE;
        }

        double[][] lattice = this.model.copyLattice();
        double aNorm = Matrix3D.norm(lattice[0]);
        double bNorm = Matrix3D.norm(lattice[1]);
        double cNorm = Matrix3D.norm(lattice[2]);
        double maxNorm = Math.max(Math.max(aNorm, bNorm), cNorm);
        double radius = boldScale * maxNorm * CYLINDER_RADIUS;

        for (int i = 0; i < latticeCylinders.length; i++) {
            this.latticeCylinders[i].setRadius(radius);
        }
    }

    private void updateXYZOfCylinder(Cylinder cylinder, int i1, int j1, int k1, int i2, int j2, int k2) {
        double[] r1 = this.model.convertToCartesianPosition((double) i1, (double) j1, (double) k1);
        double[] r2 = this.model.convertToCartesianPosition((double) i2, (double) j2, (double) k2);
        double[] dr = Matrix3D.minus(r2, r1);
        double r = Matrix3D.norm(dr);
        Point3D ax = new Point3D(dr[2], 0.0, -dr[0]);
        double theta = Math.acos(Math.min(Math.max(-1.0, dr[1] / r), 1.0));

        cylinder.setTranslateX(r1[0] + 0.5 * dr[0]);
        cylinder.setTranslateY(r1[1] + 0.5 * dr[1]);
        cylinder.setTranslateZ(r1[2] + 0.5 * dr[2]);
        cylinder.setHeight(r);
        cylinder.setRotationAxis(ax);
        cylinder.setRotate((180.0 / Math.PI) * theta);
    }

    private void updateXYZOfCylinders() {
        this.updateXYZOfCylinder(this.latticeCylinders[0], 0, 0, 0, 1, 0, 0);
        this.updateXYZOfCylinder(this.latticeCylinders[1], 1, 0, 0, 1, 1, 0);
        this.updateXYZOfCylinder(this.latticeCylinders[2], 1, 1, 0, 0, 1, 0);
        this.updateXYZOfCylinder(this.latticeCylinders[3], 0, 1, 0, 0, 0, 0);
        this.updateXYZOfCylinder(this.latticeCylinders[4], 0, 0, 0, 0, 0, 1);
        this.updateXYZOfCylinder(this.latticeCylinders[5], 1, 0, 0, 1, 0, 1);
        this.updateXYZOfCylinder(this.latticeCylinders[6], 0, 1, 0, 0, 1, 1);
        this.updateXYZOfCylinder(this.latticeCylinders[7], 1, 1, 0, 1, 1, 1);
        this.updateXYZOfCylinder(this.latticeCylinders[8], 0, 0, 1, 1, 0, 1);
        this.updateXYZOfCylinder(this.latticeCylinders[9], 1, 0, 1, 1, 1, 1);
        this.updateXYZOfCylinder(this.latticeCylinders[10], 1, 1, 1, 0, 1, 1);
        this.updateXYZOfCylinder(this.latticeCylinders[11], 0, 1, 1, 0, 0, 1);
    }

    private void updateColorOfCylinders() {
        for (int i = 0; i < latticeCylinders.length; i++) {
            Cylinder cylinder = latticeCylinders[i];
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(Color.BLACK);
            material.setSpecularColor(Color.BLACK);
            cylinder.setMaterial(material);
        }
    }

    @Override
    public void onLatticeMoved(CellEvent event) {
        this.updateRadiusOfCylinders();
        this.updateXYZOfCylinders();
    }

    @Override
    public void onAtomAdded(CellEvent event) {
        Atom atom = event.getAtom();
        this.getChildren().add(this.createVisibleAtom(atom));
    }

    @Override
    public void onAtomRemoved(CellEvent event) {
        Atom notifiedAtom = event.getAtom();
        List<Node> children = this.getChildren();

        int index = -1;
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (child instanceof VisibleAtom) {
                Atom atom = ((VisibleAtom) child).getModel();
                if (notifiedAtom == atom) {
                    index = i;
                    break;
                }
            }
        }

        if (index > -1) {
            children.remove(index);
        }
    }

    @Override
    public void onBondAdded(CellEvent event) {
        Bond bond = event.getBond();
        this.getChildren().add(this.createVisibleBond(bond));
    }

    @Override
    public void onBondRemoved(CellEvent event) {
        Bond notifiedBond = event.getBond();
        List<Node> children = this.getChildren();

        int index = -1;
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (child instanceof VisibleBond) {
                Bond bond = ((VisibleBond) child).getModel();
                if (notifiedBond == bond) {
                    index = i;
                    break;
                }
            }
        }

        if (index > -1) {
            children.remove(index);
        }
    }
}
