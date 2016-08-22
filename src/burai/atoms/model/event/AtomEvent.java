/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model.event;

public class AtomEvent extends ModelEvent {

    private String name;
    private String oldName;
    private double x;
    private double y;
    private double z;
    private double deltaX;
    private double deltaY;
    private double deltaZ;

    public AtomEvent(Object source) {
        super(source);
        this.name = null;
        this.oldName = null;
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.deltaX = 0.0;
        this.deltaY = 0.0;
        this.deltaZ = 0.0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getOldName() {
        return this.oldName;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return this.x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return this.y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getZ() {
        return this.z;
    }

    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }

    public double getDeltaX() {
        return this.deltaX;
    }

    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
    }

    public double getDeltaY() {
        return this.deltaY;
    }

    public void setDeltaZ(double deltaZ) {
        this.deltaZ = deltaZ;
    }

    public double getDeltaZ() {
        return this.deltaZ;
    }
}
