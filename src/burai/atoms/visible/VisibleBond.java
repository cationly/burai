/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.visible;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import burai.atoms.element.ElementUtil;
import burai.atoms.model.Atom;
import burai.atoms.model.Bond;
import burai.atoms.model.event.BondEvent;
import burai.atoms.model.event.BondEventListener;

public class VisibleBond extends Visible<Bond> implements BondEventListener {

    private static final double CYLINDER_RADIUS = 0.040;
    private static final double CYLINDER_BOLD_SCALE = 3.0;
    private static final int CYLINDER_DIV = 12;

    private boolean boldMode;

    private Cylinder bondCylinder1;
    private Cylinder bondCylinder2;

    public VisibleBond(Bond bond) {
        this(bond, false);
    }

    public VisibleBond(Bond bond, boolean boldMode) {
        super(bond);

        this.model.addListener(this);

        this.boldMode = boldMode;
        double boldScale = 1.0;
        if (this.boldMode) {
            boldScale = CYLINDER_BOLD_SCALE;
        }

        this.bondCylinder1 = new Cylinder(boldScale * CYLINDER_RADIUS, 1.0, CYLINDER_DIV);
        this.bondCylinder2 = new Cylinder(boldScale * CYLINDER_RADIUS, 1.0, CYLINDER_DIV);

        this.updateXYZOfCylinder();
        this.updateColorOfCylinder();
        this.getChildren().add(this.bondCylinder1);
        this.getChildren().add(this.bondCylinder2);
    }

    private void updateXYZOfCylinder() {
        Atom atom1 = this.model.getAtom1();
        double x1 = atom1.getX();
        double y1 = atom1.getY();
        double z1 = atom1.getZ();
        double rad1 = Math.sqrt(atom1.getRadius());

        Atom atom2 = this.model.getAtom2();
        double x2 = atom2.getX();
        double y2 = atom2.getY();
        double z2 = atom2.getZ();
        double rad2 = Math.sqrt(atom2.getRadius());

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double rr = dx * dx + dy * dy + dz * dz;
        double r = Math.sqrt(rr);

        double rate1 = rad1 / (rad1 + rad2);
        double rate2 = 1.0 - rate1;

        Point3D ax1 = new Point3D(dz, 0.0, -dx);
        Point3D ax2 = new Point3D(-dz, 0.0, dx);
        double theta1 = Math.acos(Math.min(Math.max(-1.0, dy / r), 1.0));
        double theta2 = Math.PI - theta1;

        this.bondCylinder1.setTranslateX(x1 + 0.5 * rate1 * dx);
        this.bondCylinder1.setTranslateY(y1 + 0.5 * rate1 * dy);
        this.bondCylinder1.setTranslateZ(z1 + 0.5 * rate1 * dz);
        this.bondCylinder1.setHeight(rate1 * r);
        this.bondCylinder1.setRotationAxis(ax1);
        this.bondCylinder1.setRotate((180.0 / Math.PI) * theta1);

        this.bondCylinder2.setTranslateX(x2 - 0.5 * rate2 * dx);
        this.bondCylinder2.setTranslateY(y2 - 0.5 * rate2 * dy);
        this.bondCylinder2.setTranslateZ(z2 - 0.5 * rate2 * dz);
        this.bondCylinder2.setHeight(rate2 * r);
        this.bondCylinder2.setRotationAxis(ax2);
        this.bondCylinder2.setRotate((180.0 / Math.PI) * theta2);
    }

    private void updateColorOfCylinder() {
        Atom atom1 = this.model.getAtom1();
        PhongMaterial material1 = new PhongMaterial();
        material1.setDiffuseColor(ElementUtil.getColor(atom1.getName()));
        material1.setSpecularColor(Color.SILVER);
        this.bondCylinder1.setMaterial(material1);

        Atom atom2 = this.model.getAtom2();
        PhongMaterial material2 = new PhongMaterial();
        material2.setDiffuseColor(ElementUtil.getColor(atom2.getName()));
        material2.setSpecularColor(Color.SILVER);
        this.bondCylinder2.setMaterial(material2);
    }

    @Override
    public void onLinkedAtomRenamed(BondEvent event) {
        this.updateXYZOfCylinder();
        this.updateColorOfCylinder();
    }

    @Override
    public void onLinkedAtomMoved(BondEvent event) {
        this.updateXYZOfCylinder();
    }

}
