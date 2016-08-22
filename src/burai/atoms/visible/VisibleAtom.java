/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.visible;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import burai.atoms.element.ElementUtil;
import burai.atoms.model.Atom;
import burai.atoms.model.AtomProperty;
import burai.atoms.model.event.AtomEvent;
import burai.atoms.model.event.AtomEventListener;

public class VisibleAtom extends Visible<Atom> implements AtomEventListener {

    private static final String KEY_SELECTED = AtomProperty.SELECTED;

    private static final double RADIUS_SCALE = 0.5;
    private static final double RADIUS_BOLD_SCALE = 1.4;

    private boolean boldMode;

    private AtomicSphere atomSphere;

    private boolean disableToSelect;

    public VisibleAtom(Atom atom) {
        this(atom, false);
    }

    public VisibleAtom(Atom atom, boolean disableToSelect) {
        this(atom, disableToSelect, false);
    }

    public VisibleAtom(Atom atom, boolean disableToSelect, boolean boldMode) {
        super(atom);

        this.model.addListener(this);
        this.model.addPropertyListener(KEY_SELECTED, o -> this.updateSelected());

        this.boldMode = boldMode;
        this.atomSphere = new AtomicSphere(this, !this.boldMode);
        this.disableToSelect = disableToSelect;

        this.updateRadiusOfSphere();
        this.updateXYZOfSphere();
        this.updateColorOfSphere();
        this.updateSelected();
        this.getChildren().add(this.atomSphere);
    }

    private void updateRadiusOfSphere() {
        double boldScale = 1.0;
        if (this.boldMode) {
            boldScale = RADIUS_BOLD_SCALE;
        }

        double radius = RADIUS_SCALE * this.model.getRadius();
        if (this.isSelected()) {
            radius = Math.max(1.05 * radius, RADIUS_SCALE * 0.80);
        }

        this.atomSphere.setRadius(boldScale * radius);
    }

    private void updateXYZOfSphere() {
        this.atomSphere.setTranslateX(this.model.getX());
        this.atomSphere.setTranslateY(this.model.getY());
        this.atomSphere.setTranslateZ(this.model.getZ());
    }

    private void updateColorOfSphere() {
        PhongMaterial material = new PhongMaterial();
        Color diffuseColor = ElementUtil.getColor(this.model.getName());
        material.setDiffuseColor(diffuseColor);
        material.setSpecularColor(Color.SILVER);
        this.atomSphere.setMaterial(material);
    }

    private void updateSelected() {
        if (this.disableToSelect) {
            return;
        }

        if (!this.isSelected()) {
            this.updateRadiusOfSphere();
            this.atomSphere.setDrawMode(DrawMode.FILL);

        } else {
            this.updateRadiusOfSphere();
            this.atomSphere.setDrawMode(DrawMode.LINE);
        }
    }

    @Override
    public void onAtomRenamed(AtomEvent event) {
        this.updateRadiusOfSphere();
        this.updateColorOfSphere();
    }

    @Override
    public void onAtomMoved(AtomEvent event) {
        this.updateXYZOfSphere();
    }

    public void setSelected(boolean selected) {
        Atom masterAtom = this.model.getMasterAtom();
        if (masterAtom == null) {
            return;
        }

        masterAtom.setProperty(KEY_SELECTED, selected);
    }

    public boolean isSelected() {
        if (!this.model.hasProperty(KEY_SELECTED)) {
            return this.initializeSelected();
        }

        return this.model.booleanProperty(KEY_SELECTED);
    }

    private boolean initializeSelected() {
        Atom masterAtom = this.model.getMasterAtom();
        if (masterAtom == null) {
            this.model.setProperty(KEY_SELECTED, false);
            return false;
        }

        boolean selected = masterAtom.booleanProperty(KEY_SELECTED);
        this.model.setProperty(KEY_SELECTED, selected);
        return selected;
    }

    public double getRadius() {
        return this.atomSphere.getRadius();
    }

    public double getX() {
        return this.atomSphere.getTranslateX();
    }

    public double getY() {
        return this.atomSphere.getTranslateY();
    }

    public double getZ() {
        return this.atomSphere.getTranslateZ();
    }
}
