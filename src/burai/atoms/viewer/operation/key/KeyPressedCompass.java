/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.key;

import java.util.HashMap;
import java.util.Map;

import burai.atoms.viewer.operation.ViewerEventCompass;
import burai.atoms.viewer.operation.ViewerEventManager;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyPressedCompass extends ViewerEventCompass<KeyEvent> {

    private ViewerEventManager manager;

    private Map<KeyPressedAnsatz, KeyPressedKernel> keyKernels;

    public KeyPressedCompass() {
        super();
        this.manager = null;
        this.createKeyKernels();
    }

    @Override
    public void perform(ViewerEventManager manager, KeyEvent event) {
        if (manager == null) {
            return;
        }

        if (event == null) {
            return;
        }

        this.manager = manager;

        KeyCode keyCode = event.getCode();
        boolean ctrlStat = event.isControlDown();
        boolean shiftStat = event.isShiftDown();
        boolean altStat = event.isAltDown();

        KeyPressedKernel keyKernel = this.keyKernels.get(new KeyPressedAnsatz(keyCode, ctrlStat, shiftStat, altStat));
        if (keyKernel != null) {
            keyKernel.performOnKeyPressed();
        }
    }

    private void createKeyKernels() {
        this.keyKernels = new HashMap<KeyPressedAnsatz, KeyPressedKernel>();

        // Ctrl key is pressed
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.C, true, false, false),
                () -> this.manager.getAtomsViewer().setCompassToCenter());

        // any key is not pressed
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.ESCAPE),
                () -> this.manager.exitCompassMode());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.RIGHT),
                () -> this.rotateRight());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.LEFT),
                () -> this.rotateLeft());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.UP),
                () -> this.rotateUp());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.PAGE_UP),
                () -> this.rotateUp());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.DOWN),
                () -> this.rotateDown());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.PAGE_DOWN),
                () -> this.rotateDown());
    }

    private void rotateRight() {
        this.manager.getAtomsViewer().appendCompassRotation(KEY_ROTATE_SPEED, 0.0, -1.0, 0.0);
    }

    private void rotateLeft() {
        this.manager.getAtomsViewer().appendCompassRotation(KEY_ROTATE_SPEED, 0.0, 1.0, 0.0);
    }

    private void rotateUp() {
        this.manager.getAtomsViewer().appendCompassRotation(KEY_ROTATE_SPEED, -1.0, 0.0, 0.0);
    }

    private void rotateDown() {
        this.manager.getAtomsViewer().appendCompassRotation(KEY_ROTATE_SPEED, 1.0, 0.0, 0.0);
    }
}
