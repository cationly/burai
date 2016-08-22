/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.mouse;

import burai.atoms.viewer.operation.ViewerEventEditorMenu;
import burai.atoms.viewer.operation.ViewerEventManager;
import javafx.scene.input.MouseEvent;

public class MouseEventEditorMenu extends ViewerEventEditorMenu<MouseEvent> implements MouseEventKernel {

    private MouseEventProxy proxy;

    public MouseEventEditorMenu(MouseEventHandler handler) {
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
        // NOP
    }

    @Override
    public void performOnMouseReleased(MouseEvent event) {
        // NOP
    }
}
