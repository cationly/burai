/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.mouse;

import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.operation.ViewerEventCompass;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.visible.VisibleAtom;
import javafx.scene.input.MouseEvent;

public class MouseEventCompass extends ViewerEventCompass<MouseEvent> implements MouseEventKernel {

    private MouseEventProxy proxy;

    public MouseEventCompass(MouseEventHandler handler) {
        super();
        this.proxy = new MouseEventProxy(handler, this);
    }

    @Override
    public void perform(ViewerEventManager manager, MouseEvent event) {
        this.proxy.perform(manager, event);
    }

    @Override
    public void performOnMousePressed(MouseEvent event) {
        if (event == null) {
            return;
        }

        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        VisibleAtom pickedAtom = handler.getPickedAtom();

        if (pickedAtom != null && pickedAtom == manager.getPrincipleAtom()) {
            AtomsViewer atomsViewer = manager.getAtomsViewer();
            if (atomsViewer != null) {
                atomsViewer.storeCell();
            }

            manager.setCompassPicking(true);

        } else if (event.getClickCount() >= 2) {
            manager.exitCompassMode();
        }
    }

    @Override
    public void performOnMouseDragged(MouseEvent event) {
        if (event == null) {
            return;
        }

        this.rotateCompass();
    }

    @Override
    public void performOnMouseReleased(MouseEvent event) {
        // NOP
    }

    private void rotateCompass() {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        double x1 = handler.getMouseX1();
        double x2 = handler.getMouseX2();
        double y1 = handler.getMouseY1();
        double y2 = handler.getMouseY2();
        double dx = x2 - x1;
        double dy = y2 - y1;

        double rr = dx * dx + dy * dy;
        if (rr > 0.0) {
            double rho = MOUSE_ROTATE_SPEED * Math.sqrt(rr);
            manager.getAtomsViewer().appendCompassRotation(rho, dy, -dx, 0.0);
        }
    }
}
