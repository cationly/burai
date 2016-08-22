/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.key;

import burai.atoms.viewer.operation.ViewerEventCompassPicking;
import burai.atoms.viewer.operation.ViewerEventManager;
import javafx.scene.input.KeyEvent;

public class KeyPressedCompassPicking extends ViewerEventCompassPicking<KeyEvent> {

    public KeyPressedCompassPicking() {
        super();
    }

    @Override
    public void perform(ViewerEventManager manager, KeyEvent event) {
        // NOP
    }
}
