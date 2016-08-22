/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.mouse;

import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import burai.atoms.viewer.operation.ViewerEventHandler;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.visible.AtomicSphere;
import burai.atoms.visible.VisibleAtom;

public class MouseEventHandler extends ViewerEventHandler<MouseEvent> {

    private VisibleAtom pickedAtom;

    private double[][] mousePosition;

    public MouseEventHandler(ViewerEventManager manager) {
        super(manager);

        this.pickedAtom = null;

        this.mousePosition = new double[3][2];
        for (int i = 0; i < this.mousePosition.length; i++) {
            for (int j = 0; j < this.mousePosition[i].length; j++) {
                this.mousePosition[i][j] = 0.0;
            }
        }

        this.addKernel(new MouseEventCompassPicking(this));
        this.addKernel(new MouseEventCompass(this));
        this.addKernel(new MouseEventEditorMenu(this));
        this.addKernel(new MouseEventScope(this));
        this.addKernel(new MouseEventRegular(this));
    }

    @Override
    public void handle(MouseEvent event) {
        if (event == null) {
            return;
        }

        EventType<? extends MouseEvent> eventType = event.getEventType();
        if (eventType == null) {
            return;
        }

        if (eventType == MouseEvent.MOUSE_PRESSED) {
            Point2D point = this.getMousePosition(event);
            double x = point.getX();
            double y = point.getY();
            this.setMousePosition0(x, y);
            this.setMousePosition1(x, y);
            this.setMousePosition2(x, y);
            this.pickedAtom = this.pickAtom(event);

        } else if (eventType == MouseEvent.MOUSE_DRAGGED) {
            Point2D point = this.getMousePosition(event);
            double x1 = this.getMouseX2();
            double y1 = this.getMouseY2();
            double x2 = point.getX();
            double y2 = point.getY();
            this.setMousePosition1(x1, y1);
            this.setMousePosition2(x2, y2);

        } else if (eventType == MouseEvent.MOUSE_RELEASED) {
            // NOP
        }

        super.handle(event);
    }

    private Point2D getMousePosition(MouseEvent event) {
        Point2D point = null;

        Object source = event.getSource();
        if (source != null && (source instanceof Node)) {
            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();
            point = ((Node) source).sceneToLocal(sceneX, sceneY);

        } else {
            double x = event.getSceneX();
            double y = event.getSceneY();
            point = new Point2D(x, y);
        }

        return point;
    }

    public VisibleAtom getPickedAtom() {
        return this.pickedAtom;
    }

    private VisibleAtom pickAtom(MouseEvent event) {
        if (event == null) {
            return null;
        }

        VisibleAtom pickedAtom = null;

        Node node = event.getPickResult().getIntersectedNode();
        if (node != null && node instanceof AtomicSphere) {
            AtomicSphere atomSphere = (AtomicSphere) node;
            VisibleAtom visibleAtom = atomSphere.getVisibleAtom();
            pickedAtom = visibleAtom;
        }

        return pickedAtom;
    }

    private void setMousePosition0(double x, double y) {
        this.mousePosition[0][0] = x;
        this.mousePosition[0][1] = y;
    }

    private void setMousePosition1(double x, double y) {
        this.mousePosition[1][0] = x;
        this.mousePosition[1][1] = y;
    }

    private void setMousePosition2(double x, double y) {
        this.mousePosition[2][0] = x;
        this.mousePosition[2][1] = y;
    }

    public double getMouseX0() {
        return this.mousePosition[0][0];
    }

    public double getMouseX1() {
        return this.mousePosition[1][0];
    }

    public double getMouseX2() {
        return this.mousePosition[2][0];
    }

    public double getMouseY0() {
        return this.mousePosition[0][1];
    }

    public double getMouseY1() {
        return this.mousePosition[1][1];
    }

    public double getMouseY2() {
        return this.mousePosition[2][1];
    }
}
