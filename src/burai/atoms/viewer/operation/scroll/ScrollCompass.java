/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.scroll;

import burai.atoms.viewer.operation.ViewerEventCompass;
import burai.atoms.viewer.operation.ViewerEventManager;
import javafx.scene.input.ScrollEvent;

public class ScrollCompass extends ViewerEventCompass<ScrollEvent> {

    public ScrollCompass() {
        super();
    }

    @Override
    public void perform(ViewerEventManager manager, ScrollEvent event) {
        // NOP
    }
}
