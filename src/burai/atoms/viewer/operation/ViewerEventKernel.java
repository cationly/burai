/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation;

import javafx.event.Event;

public interface ViewerEventKernel<T extends Event> {

    public static final double KEY_SCALE_SPEED = 1.1;

    public static final double KEY_ROTATE_SPEED = 10.0;

    public static final double KEY_TRANS_SPEED = 10.0;

    public static final double MOUSE_SCALE_SPEED = 0.01;

    public static final double MOUSE_ROTATE_SPEED = 0.50;

    public static final double MOUSE_TRANS_SPEED = 1.50;

    public static final double SCROLL_SCALE_SPEED = 0.002;

    public abstract boolean isToPerform(ViewerEventManager manager);

    public abstract void perform(ViewerEventManager manager, T event);

}
