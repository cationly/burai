/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.mouse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import burai.atoms.model.Atom;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.operation.ViewerEventCompassPicking;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.visible.VisibleAtom;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class MouseEventCompassPicking extends ViewerEventCompassPicking<MouseEvent> implements MouseEventKernel {

    private MouseEventProxy proxy;

    private Set<Atom> mobileAtoms;

    public MouseEventCompassPicking(MouseEventHandler handler) {
        super();
        this.proxy = new MouseEventProxy(handler, this);
        this.mobileAtoms = null;
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
        if (this.mobileAtoms == null) {
            this.createMobileAtoms();
        }

        this.moveSelectedAtoms();
    }

    @Override
    public void performOnMouseReleased(MouseEvent event) {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        this.mobileAtoms = null;
        manager.setCompassPicking(false);
        manager.exitCompassMode();
    }

    private void createMobileAtoms() {
        this.mobileAtoms = new HashSet<Atom>();

        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        AtomsViewer atomsViewer = manager.getAtomsViewer();
        if (atomsViewer == null) {
            return;
        }

        List<VisibleAtom> visibleAtoms = atomsViewer.getVisibleAtoms();
        for (VisibleAtom visibleAtom : visibleAtoms) {
            if (visibleAtom != null && visibleAtom.isSelected()) {
                Atom atom = visibleAtom.getModel();
                if (atom != null) {
                    atom = atom.getMasterAtom();
                }
                if (atom != null) {
                    this.mobileAtoms.add(atom);
                }
            }
        }
    }

    private void moveSelectedAtoms() {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        AtomsViewer atomsViewer = manager.getAtomsViewer();
        if (atomsViewer == null) {
            return;
        }

        double sceneX1 = handler.getMouseX1();
        double sceneY1 = handler.getMouseY1();
        double sceneZ1 = atomsViewer.getSceneZOnCompass(sceneX1, sceneY1);
        Point3D point1 = atomsViewer.sceneToCell(sceneX1, sceneY1, sceneZ1);
        if (point1 == null) {
            return;
        }
        double x1 = point1.getX();
        double y1 = point1.getY();
        double z1 = point1.getZ();

        double sceneX2 = handler.getMouseX2();
        double sceneY2 = handler.getMouseY2();
        double sceneZ2 = atomsViewer.getSceneZOnCompass(sceneX2, sceneY2);
        Point3D point2 = atomsViewer.sceneToCell(sceneX2, sceneY2, sceneZ2);
        if (point2 == null) {
            return;
        }
        double x2 = point2.getX();
        double y2 = point2.getY();
        double z2 = point2.getZ();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        for (Atom atom : this.mobileAtoms) {
            atom.moveBy(dx, dy, dz);
        }
    }
}
