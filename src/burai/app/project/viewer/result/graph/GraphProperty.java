/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph;

import java.util.ArrayList;
import java.util.List;

public class GraphProperty {

    private String title;

    private String xLabel;

    private boolean xAuto;

    private double xLower;

    private double xUpper;

    private double xTick;

    private String yLabel;

    private boolean yAuto;

    private double yLower;

    private double yUpper;

    private double yTick;

    private List<SeriesProperty> seriesList;

    public GraphProperty() {
        this.title = "TITLE";
        this.xLabel = "X-AXIS";
        this.xAuto = true;
        this.xLower = 0.0;
        this.xUpper = 0.0;
        this.xTick = -1.0;
        this.yLabel = "Y-AXIS";
        this.yAuto = true;
        this.yLower = 0.0;
        this.yUpper = 0.0;
        this.yTick = -1.0;
        this.seriesList = null;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getXLabel() {
        return this.xLabel;
    }

    public void setXLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public boolean isXAuto() {
        return this.xAuto;
    }

    public void setXAuto(boolean xAuto) {
        this.xAuto = xAuto;
    }

    public double getXLower() {
        return this.xLower;
    }

    public void setXLower(double xLower) {
        this.xLower = xLower;
    }

    public double getXUpper() {
        return this.xUpper;
    }

    public void setXUpper(double xUpper) {
        this.xUpper = xUpper;
    }

    public double getXTick() {
        return this.xTick;
    }

    public void setXTick(double xTick) {
        this.xTick = xTick;
    }

    public String getYLabel() {
        return this.yLabel;
    }

    public void setYLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    public boolean isYAuto() {
        return this.yAuto;
    }

    public void setYAuto(boolean yAuto) {
        this.yAuto = yAuto;
    }

    public double getYLower() {
        return this.yLower;
    }

    public void setYLower(double yLower) {
        this.yLower = yLower;
    }

    public double getYUpper() {
        return this.yUpper;
    }

    public void setYUpper(double yUpper) {
        this.yUpper = yUpper;
    }

    public double getYTick() {
        return this.yTick;
    }

    public void setYTick(double yTick) {
        this.yTick = yTick;
    }

    public int numSeries() {
        return this.seriesList == null ? 0 : this.seriesList.size();
    }

    public SeriesProperty getSeries(int i) {
        if (this.seriesList == null || i < 0 || i >= this.seriesList.size()) {
            throw new IndexOutOfBoundsException("incorrect index of seriesList: " + i + ".");
        }

        return this.seriesList.get(i);
    }

    public void removeSeries(int i) {
        if (this.seriesList == null || i < 0 || i >= this.seriesList.size()) {
            throw new IndexOutOfBoundsException("incorrect index of seriesList: " + i + ".");
        }

        this.seriesList.remove(i);
    }

    public void addSeries(SeriesProperty series) {
        if (series == null) {
            throw new IllegalArgumentException("series is null.");
        }

        if (this.seriesList == null) {
            this.seriesList = new ArrayList<SeriesProperty>();
        }

        this.seriesList.add(series);
    }

    @Override
    public String toString() {
        return this.title;
    }
}
