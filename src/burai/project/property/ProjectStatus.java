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

    private int scfCount;
    private int optCount;
    private int mdCount;
    private int dosCount;
    private int bandCount;

    public ProjectStatus() {
        this.updateDate();
        this.scfCount = 0;
        this.optCount = 0;
        this.mdCount = 0;
        this.dosCount = 0;
        this.bandCount = 0;
    }

    private void updateDate() {
        Date objDate = new Date();
        this.date = objDate.toString();
    }

    public synchronized String getDate() {
        return this.date;
    }

    public synchronized boolean isScfDone() {
        return this.scfCount > 0;
    }

    public synchronized int getScfCount() {
        return this.scfCount;
    }

    public synchronized void updateScfCount() {
        this.updateDate();
        this.scfCount++;
    }

    public synchronized boolean isOptDone() {
        return this.optCount > 0;
    }

    public synchronized int getOptCount() {
        return this.optCount;
    }

    public synchronized void updateOptDone() {
        this.updateDate();
        this.optCount++;
    }

    public synchronized boolean isMdDone() {
        return this.mdCount > 0;
    }

    public synchronized int getMdCount() {
        return this.mdCount;
    }

    public synchronized void updateMdCount() {
        this.updateDate();
        this.mdCount++;
    }

    public synchronized boolean isDosDone() {
        return this.dosCount > 0;
    }

    public synchronized int getDosCount() {
        return this.dosCount;
    }

    public synchronized void updateDosCount() {
        this.updateDate();
        this.dosCount++;
    }

    public synchronized boolean isBandDone() {
        return this.bandCount > 0;
    }

    public synchronized int getBandCount() {
        return this.bandCount;
    }

    public synchronized void updateBandDone() {
        this.updateDate();
        this.bandCount++;
    }
}
