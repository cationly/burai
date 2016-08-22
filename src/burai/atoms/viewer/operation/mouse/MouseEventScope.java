/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.mouse;

import java.util.List;

import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.viewer.operation.ViewerEventScope;
import burai.atoms.visible.VisibleAtom;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class MouseEventScope extends ViewerEventScope<MouseEvent> implements MouseEventKernel {

    private MouseEventProxy proxy;

    public MouseEventScope(MouseEventHandler handler) {
        super();
        this.proxy = new MouseEventProxy(handler, this);
    }

    @Override
    public void perform(ViewerEventManager manager, MouseEvent event) {
        this.proxy.perform(manager, event);
    }

    @Override
    public void performOnMousePressed(MouseEvent event) {
        // NOP
    }

    @Override
    public void performOnMouseDragged(MouseEvent event) {
        this.scopeArea();
    }

    @Override
    public void performOnMouseReleased(MouseEvent event) {
        this.endScoping();
    }

    private void scopeArea() {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        double x1 = handler.getMouseX0();
        double y1 = handler.getMouseY0();
        double x2 = handler.getMouseX2();
        double y2 = handler.getMouseY2();
        double X1 = Math.min(x1, x2);
        double Y1 = Math.min(y1, y2);
        double X2 = Math.max(x1, x2);
        double Y2 = Math.max(y1, y2);

        Rectangle rectangle = manager.getScopeRectangle();
        rectangle.setX(X1);
        rectangle.setY(Y1);
        rectangle.setWidth(X2 - X1);
        rectangle.setHeight(Y2 - Y1);
    }

    private void endScoping() {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        this.selectScopedAtoms();

        manager.removeScopeRectangle();
    }

    private void selectScopedAtoms() {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        double x1 = handler.getMouseX0();
        double y1 = handler.getMouseY0();
        double x2 = handler.getMouseX2();
        double y2 = handler.getMouseY2();
        double X1 = Math.min(x1, x2);
        double Y1 = Math.min(y1, y2);
        double X2 = Math.max(x1, x2);
        double Y2 = Math.max(y1, y2);

        List<VisibleAtom> visibleAtoms = manager.getAtomsViewer().getVisibleAtoms();
        for (VisibleAtom visibleAtom : visibleAtoms) {
            double atomX = visibleAtom.getX();
            double atomY = visibleAtom.getY();
            double atomZ = visibleAtom.getZ();
            Point3D point3d = visibleAtom.localToScene(atomX, atomY, atomZ);
            double X3 = point3d.getX();
            double Y3 = point3d.getY();
            if (X1 <= X3 && X3 <= X2 && Y1 <= Y3 && Y3 <= Y2) {
                visibleAtom.setSelected(manager.isRegularScope());
            }
        }
    }
}
