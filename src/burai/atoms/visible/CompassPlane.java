/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.visible;

import burai.atoms.model.Cell;
import burai.com.math.Matrix3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class CompassPlane extends Group {

    private static final double CYLINDER_RADIUS = 0.02;
    private static final double CYLINDER_HEIGHT_RATE = 1.25;
    private static final double SPHERE_RADIUS = 0.10;

    private Cell cell;

    public CompassPlane(Cell cell) {
        super();

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;

        this.creatCompassPlane();
    }

    private double getCylinderHeight() {
        double[][] lattice = this.cell.copyLattice();
        double xLattice = Matrix3D.norm(lattice[0]);
        double yLattice = Matrix3D.norm(lattice[1]);
        double zLattice = Matrix3D.norm(lattice[2]);
        return CYLINDER_HEIGHT_RATE * Math.max(Math.max(xLattice, yLattice), zLattice);
    }

    private void creatCompassPlane() {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.BLACK);
        material.setSpecularColor(Color.GHOSTWHITE);

        double height = this.getCylinderHeight();
        Cylinder cylinder = new Cylinder(CYLINDER_RADIUS, height);
        cylinder.setMaterial(material);
        cylinder.setRotationAxis(Rotate.X_AXIS);
        cylinder.setRotate(90.0);

        Sphere sphere1 = new Sphere(SPHERE_RADIUS);
        sphere1.setMaterial(material);

        Sphere sphere2 = new Sphere(SPHERE_RADIUS);
        sphere2.setMaterial(material);
        sphere2.setTranslateZ(+0.5 * height);

        Sphere sphere3 = new Sphere(SPHERE_RADIUS);
        sphere3.setMaterial(material);
        sphere3.setTranslateZ(-0.5 * height);

        Rectangle rectangle = new Rectangle(-0.5 * height, -0.5 * height, height, height);
        rectangle.setFill(Color.rgb(0, 255, 0, 0.2));

        Group group = new Group();
        group.getChildren().add(cylinder);
        group.getChildren().add(sphere1);
        group.getChildren().add(sphere2);
        group.getChildren().add(sphere3);
        group.getChildren().add(rectangle);

        this.getChildren().add(group);
    }
}
