/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer;

import burai.com.graphic.svg.SVGLibrary.SVGData;

public class ViewerItemSet {

    private ViewerItem atomsViewerItem;
    private ViewerItem inputFileItem;
    private ViewerItem saveFileItem;
    private ViewerItem saveAsFileItem;
    private ViewerItem screenShotItem;
    private ViewerItem runItem;
    private ViewerItem resultItem;

    public ViewerItemSet() {
        this.atomsViewerItem = new ViewerItem(SVGData.ATOMS, "Show atoms");
        this.inputFileItem = new ViewerItem(SVGData.INPUTFILE, "Input-file");
        this.saveFileItem = new ViewerItem(SVGData.SAVE, "Save");
        this.saveAsFileItem = new ViewerItem(SVGData.SAVE, "Save as ...");
        this.screenShotItem = new ViewerItem(SVGData.CAMERA, "Screen-shot");
        this.runItem = new ViewerItem(SVGData.RUN, "Run");
        this.resultItem = new ViewerItem(SVGData.RESULT, "Result");
    }

    public ViewerItem[] getItems() {
        return new ViewerItem[] {
                this.atomsViewerItem,
                this.inputFileItem,
                this.saveFileItem,
                this.saveAsFileItem,
                this.screenShotItem,
                this.runItem,
                this.resultItem
        };
    }

    public ViewerItem getAtomsViewerItem() {
        return this.atomsViewerItem;
    }

    public ViewerItem getInputFileItem() {
        return this.inputFileItem;
    }

    public ViewerItem getSaveFileItem() {
        return this.saveFileItem;
    }

    public ViewerItem getSaveAsFileItem() {
        return this.saveAsFileItem;
    }

    public ViewerItem getScreenShotItem() {
        return this.screenShotItem;
    }

    public ViewerItem getRunItem() {
        return this.runItem;
    }

    public ViewerItem getResultItem() {
        return this.resultItem;
    }
}
