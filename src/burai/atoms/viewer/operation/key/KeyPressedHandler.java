/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.key;

import javafx.scene.input.KeyEvent;
import burai.atoms.viewer.operation.ViewerEventHandler;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.com.keys.PriorKeyEvent;

public class KeyPressedHandler extends ViewerEventHandler<KeyEvent> {

    public KeyPressedHandler(ViewerEventManager manager) {
        super(manager);
        this.addKernel(new KeyPressedCompassPicking());
        this.addKernel(new KeyPressedCompass());
        this.addKernel(new KeyPressedEditorMenu());
        this.addKernel(new KeyPressedScope());
        this.addKernel(new KeyPressedRegular());
    }

    @Override
    public void handle(KeyEvent event) {
        if (event == null) {
            return;
        }

        if (PriorKeyEvent.isPriorKeyEvent(event)) {
            return;
        }

        event.consume();

        super.handle(event);
    }
}
