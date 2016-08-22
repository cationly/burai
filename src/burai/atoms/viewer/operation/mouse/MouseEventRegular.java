/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.mouse;

import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.viewer.operation.ViewerEventRegular;
import burai.atoms.viewer.operation.editor.EditorMenu;
import burai.atoms.visible.VisibleAtom;
import javafx.scene.input.MouseEvent;

public class MouseEventRegular extends ViewerEventRegular<MouseEvent> implements MouseEventKernel {

    private MouseEventProxy proxy;

    public MouseEventRegular(MouseEventHandler handler) {
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

        if (event.isControlDown()) {
            this.startScoping(event);

        } else if (event.isSecondaryButtonDown()) {
            this.showEditorMenu(event);

        } else if (event.getClickCount() >= 2) {
            MouseEventHandler handler = this.proxy.getHandler();
            VisibleAtom visibleAtom = null;
            if (handler != null) {
                visibleAtom = handler.getPickedAtom();
            }

            if (visibleAtom != null) {
                visibleAtom.setSelected(!visibleAtom.isSelected());
            } else {
                this.startScoping(event);
            }
        }
    }

    @Override
    public void performOnMouseDragged(MouseEvent event) {
        if (event == null) {
            return;
        }

        if (event.isAltDown()) {
            this.scaleCell();

        } else if (event.isShiftDown()) {
            this.translateCell();

        } else if (event.isMiddleButtonDown()) {
            this.translateCell();

        } else {
            this.rotateCell();
        }
    }

    @Override
    public void performOnMouseReleased(MouseEvent event) {
        // NOP
    }

    private void showEditorMenu(MouseEvent event) {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        manager.setPrincipleAtom(handler.getPickedAtom());

        manager.removeEditorMenu();
        EditorMenu editorMenu = manager.getEditorMenu();
        editorMenu.show(event);
    }

    private void startScoping(MouseEvent event) {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        manager.removeScopeRectangle();

        if (event.isPrimaryButtonDown()) {
            manager.getScopeRectangle(true);
        } else {
            manager.getScopeRectangle(false);
        }
    }

    private void scaleCell() {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        double y1 = handler.getMouseY1();
        double y2 = handler.getMouseY2();
        double dy = y2 - y1;

        if (dy != 0.0) {
            double eta = 1.0 + Math.tanh(MOUSE_SCALE_SPEED * dy);
            manager.getAtomsViewer().appendCellScale(eta);
        }
    }

    private void rotateCell() {
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
            manager.getAtomsViewer().appendCellRotation(rho, dy, -dx, 0.0);
        }
    }

    private void translateCell() {
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

        double xi = MOUSE_TRANS_SPEED * dx;
        double eta = MOUSE_TRANS_SPEED * dy;
        manager.getAtomsViewer().appendCellTranslation(xi, eta, 0.0);
    }
}
