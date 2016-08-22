/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.mouse;

import javafx.scene.input.MouseEvent;

public interface MouseEventKernel {

    public abstract void performOnMousePressed(MouseEvent event);

    public abstract void performOnMouseDragged(MouseEvent event);

    public abstract void performOnMouseReleased(MouseEvent event);

}
