/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project.property;

import java.util.Date;

public class ProjectStatus {

    private String date;

    private boolean scfDone;
    private boolean optDone;
    private boolean mdDone;
    private boolean dosDone;
    private boolean bandDone;

    public ProjectStatus() {
        this.updateDate();
        this.scfDone = false;
        this.optDone = false;
        this.mdDone = false;
        this.dosDone = false;
        this.bandDone = false;
    }

    private void updateDate() {
        Date objDate = new Date();
        this.date = objDate.toString();
    }

    public synchronized String getDate() {
        return this.date;
    }

    public synchronized boolean isScfDone() {
        return this.scfDone;
    }

    public synchronized void setScfDone(boolean scfDone) {
        this.updateDate();
        this.scfDone = scfDone;
    }

    public synchronized boolean isOptDone() {
        return this.optDone;
    }

    public synchronized void setOptDone(boolean optDone) {
        this.updateDate();
        this.optDone = optDone;
    }

    public synchronized boolean isMdDone() {
        return this.mdDone;
    }

    public synchronized void setMdDone(boolean mdDone) {
        this.updateDate();
        this.mdDone = mdDone;
    }

    public synchronized boolean isDosDone() {
        return this.dosDone;
    }

    public synchronized void setDosDone(boolean dosDone) {
        this.updateDate();
        this.dosDone = dosDone;
    }

    public synchronized boolean isBandDone() {
        return this.bandDone;
    }

    public synchronized void setBandDone(boolean bandDone) {
        this.updateDate();
        this.bandDone = bandDone;
    }
}
