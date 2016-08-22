/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.scroll;

import burai.atoms.viewer.operation.ViewerEventHandler;
import burai.atoms.viewer.operation.ViewerEventManager;
import javafx.scene.input.ScrollEvent;

public class ScrollHandler extends ViewerEventHandler<ScrollEvent> {

    public ScrollHandler(ViewerEventManager manager) {
        super(manager);
        this.addKernel(new ScrollCompassPicking());
        this.addKernel(new ScrollCompass());
        this.addKernel(new ScrollEditorMenu());
        this.addKernel(new ScrollScope());
        this.addKernel(new ScrollRegular());
    }

    @Override
    public void handle(ScrollEvent event) {
        if (event == null) {
            return;
        }

        event.consume();

        super.handle(event);
    }
}
