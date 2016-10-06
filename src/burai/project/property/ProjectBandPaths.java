/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project.property;

import java.util.ArrayList;
import java.util.List;

public class ProjectBandPaths {

    private static final String DEFAULT_LABEL = "?";

    private List<Point> points;

    public ProjectBandPaths() {
        this.points = null;
    }

    public synchronized void clearBandPaths() {
        if (this.points != null) {
            this.points.clear();
        }
    }

    public synchronized int numPoints() {
        return this.points == null ? 0 : this.points.size();
    }

    private Point getPoint(int i) {
        if (this.points == null || i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        return this.points.get(i);
    }

    public synchronized double getKx(int i) {
        return this.getPoint(i).kx;
    }

    public synchronized double getKy(int i) {
        return this.getPoint(i).ky;
    }

    public synchronized double getKz(int i) {
        return this.getPoint(i).kz;
    }

    public synchronized double getCoordinate(int i) {
        return this.getPoint(i).coord;
    }

    public synchronized String getLabel(int i) {
        String label = this.getPoint(i).label;
        return label == null ? DEFAULT_LABEL : label;
    }

    public synchronized void removePoint(int i) {
        if (this.points == null || i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        this.points.remove(i);
    }

    public synchronized void addPoint(double kx, double ky, double kz, double coord) {
        if (this.points == null) {
            this.points = new ArrayList<Point>();
        }

        this.points.add(new Point(kx, ky, kz, coord));
    }

    public synchronized void setLabel(int i, String label) {
        this.getPoint(i).label = label;
    }

    public synchronized ProjectBandPaths copyBandPaths() {
        ProjectBandPaths other = new ProjectBandPaths();

        if (this.points == null) {
            other.points = null;

        } else {
            other.points = new ArrayList<Point>(this.points);
        }

        return other;
    }

    private static class Point {
        public double kx;
        public double ky;
        public double kz;
        public double coord;
        public String label;

        public Point(double kx, double ky, double kz, double coord) {
            this.kx = kx;
            this.ky = ky;
            this.kz = kz;
            this.coord = coord;
            this.label = DEFAULT_LABEL;
        }
    }
}
