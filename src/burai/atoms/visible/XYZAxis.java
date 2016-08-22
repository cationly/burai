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
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class XYZAxis extends Group {

    private static final double CYLINDER_RADIUS = 0.03;
    private static final double CYLINDER_HEIGHT = 1.00;
    private static final double TEXT_SIZE = 0.50;
    private static final String TEXT_FONT = "Times New Roman";

    public XYZAxis() {
        super();

        this.creatAx(0, "X", Color.RED);
        this.creatAx(1, "Y", Color.BLUE);
        this.creatAx(2, "Z", Color.GREEN);
    }

    private void creatAx(int index, String label, Color color) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(Color.SILVER);

        Cylinder cylinder = new Cylinder(CYLINDER_RADIUS, CYLINDER_HEIGHT);
        cylinder.setMaterial(material);

        Text text = new Text(label);
        text.setFont(Font.font(TEXT_FONT, TEXT_SIZE));
        //text.setTranslateX(-0.25 * TEXT_SIZE);
        text.setTranslateX(-0.33 * TEXT_SIZE);
        text.setTranslateY(0.10 * TEXT_SIZE + CYLINDER_HEIGHT);
        text.setRotationAxis(Rotate.Z_AXIS);
        text.setRotate(180.0);

        Group group = new Group();
        group.getChildren().add(cylinder);
        group.getChildren().add(text);

        Affine affine = new Affine();
        affine.prependRotation(180.0, Point3D.ZERO, Rotate.Y_AXIS);
        affine.prependTranslation(0.0, 0.5 * CYLINDER_HEIGHT, 0.0);
        if (index == 0) {
            affine.prependRotation(-90.0, Point3D.ZERO, Rotate.Z_AXIS);
            affine.prependRotation(-90.0, Point3D.ZERO, Rotate.X_AXIS);
            affine.prependRotation(45.0, Point3D.ZERO, Rotate.X_AXIS);
        } else if (index == 1) {
            affine.prependRotation(45.0, Point3D.ZERO, Rotate.Y_AXIS);
        } else if (index == 2) {
            affine.prependRotation(90.0, Point3D.ZERO, Rotate.X_AXIS);
            affine.prependRotation(90.0, Point3D.ZERO, Rotate.Z_AXIS);
            affine.prependRotation(45.0, Point3D.ZERO, Rotate.Z_AXIS);
        }

        group.getTransforms().add(affine);
        this.getChildren().add(group);
    }
}
