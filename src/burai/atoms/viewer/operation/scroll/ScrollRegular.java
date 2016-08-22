/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.scroll;

import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.viewer.operation.ViewerEventRegular;
import javafx.scene.input.ScrollEvent;

public class ScrollRegular extends ViewerEventRegular<ScrollEvent> {

    public ScrollRegular() {
        super();
    }

    @Override
    public void perform(ViewerEventManager manager, ScrollEvent event) {
        double dy = event.getDeltaY();

        if (dy != 0.0) {
            double eta = 1.0 - Math.tanh(SCROLL_SCALE_SPEED * dy);
            manager.getAtomsViewer().appendCellScale(eta);
        }
    }
}
