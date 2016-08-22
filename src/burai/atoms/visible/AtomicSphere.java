/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.visible;

import javafx.scene.shape.Sphere;

public class AtomicSphere extends Sphere {

    private static final int SPHERE_DIV_HIGH = 24;
    private static final int SPHERE_DIV_LOW = 16;

    private VisibleAtom visibleAtom;

    public AtomicSphere(VisibleAtom visibleAtom, boolean divHigh) {
        super(1.0, divHigh ? SPHERE_DIV_HIGH : SPHERE_DIV_LOW);
        this.visibleAtom = visibleAtom;
    }

    public VisibleAtom getVisibleAtom() {
        return this.visibleAtom;
    }

}
