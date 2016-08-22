/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.vlight;

import burai.atoms.viewer.ViewerComponentBase;
import javafx.scene.Node;

public abstract class VLightComponent<N extends Node> extends ViewerComponentBase<AtomsVLight, N> {

    public VLightComponent(AtomsVLight atomsVLight) {
        super(atomsVLight);
    }

}
