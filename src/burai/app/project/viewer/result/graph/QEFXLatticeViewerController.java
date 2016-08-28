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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import burai.app.project.QEFXProjectController;
import burai.com.consts.Constants;
import burai.com.math.Lattice;
import burai.project.property.ProjectGeometry;
import burai.project.property.ProjectGeometryList;

public class QEFXLatticeViewerController extends QEFXGraphViewerController {

    private LatticeViewerType lattVType;

    private boolean mdMode;

    private ProjectGeometryList projectGeometryList;

    public QEFXLatticeViewerController(QEFXProjectController projectController,
            ProjectGeometryList projectGeometryList, LatticeViewerType lattVType, boolean mdMode) {

        super(projectController, LatticeViewerType.ANGLE.equals(lattVType) ? Pos.BOTTOM_RIGHT : null);

        if (projectGeometryList == null) {
            throw new IllegalArgumentException("projectGeometryList is null.");
        }

        if (lattVType == null) {
            throw new IllegalArgumentException("lattVType is null.");
        }

        this.projectGeometryList = projectGeometryList;

        this.mdMode = mdMode;
        this.lattVType = lattVType;
    }

    @Override
    protected GraphProperty createProperty() {
        GraphProperty property = new GraphProperty();

        if (this.mdMode) {
            property.setTitle("Molecular Dynamics");
            property.setXLabel("Time /ps");

        } else {
            property.setTitle("Geometric Optimization");
            property.setXLabel("# Iterations");
        }

        int numSeries = 0;
        String[] names = null;
        String[] colors = null;

        if (LatticeViewerType.A.equals(this.lattVType)) {
            property.setYLabel("Lattice constant (A) / Angstrom");
            numSeries = 1;
            names = new String[] { "A" };
            colors = new String[] { "red" };

        } else if (LatticeViewerType.B.equals(this.lattVType)) {
            property.setYLabel("Lattice constant (B) / Angstrom");
            numSeries = 1;
            names = new String[] { "B" };
            colors = new String[] { "blue" };

        } else if (LatticeViewerType.C.equals(this.lattVType)) {
            property.setYLabel("Lattice constant (C) / Angstrom");
            numSeries = 1;
            names = new String[] { "C" };
            colors = new String[] { "green" };

        } else if (LatticeViewerType.ANGLE.equals(this.lattVType)) {
            property.setYLabel("Lattice constant (angles) / Degree");
            numSeries = 3;
            names = new String[] { "alpha", "beta", "gamma" };
            colors = new String[] { "red", "blue", "green" };
        }

        for (int iSeries = 0; iSeries < numSeries; iSeries++) {
            String name = names == null ? null : names[iSeries];
            String color = colors == null ? null : colors[iSeries];
            if (name != null && color != null) {
                SeriesProperty seriesProperty = new SeriesProperty();
                seriesProperty.setName(name);
                seriesProperty.setColor(color);
                seriesProperty.setDash(SeriesProperty.DASH_NULL);
                seriesProperty.setWithSymbol(true);
                seriesProperty.setWidth(2.0);
                property.addSeries(seriesProperty);
            }
        }

        return property;
    }

    @Override
    protected void reloadData(LineChart<Number, Number> lineChart) {
        if (lineChart == null) {
            return;
        }

        ProjectGeometryList projectGeometryList = this.projectGeometryList.copyGeometryList();
        if (projectGeometryList == null) {
            lineChart.getData().clear();
            return;
        }

        int numSeries = 0;
        String[] names = null;
        String[] units = null;
        LatticeConstGetter[] lattConsts = null;

        if (LatticeViewerType.A.equals(this.lattVType)) {
            numSeries = 1;
            names = new String[] { "A" };
            units = new String[] { "Angstrom" };

            lattConsts = new LatticeConstGetter[] {
                    cell -> {
                        return Lattice.getA(cell) * Constants.BOHR_RADIUS_ANGS;
                    }
            };

        } else if (LatticeViewerType.B.equals(this.lattVType)) {
            numSeries = 1;
            names = new String[] { "B" };
            units = new String[] { "Angstrom" };
            lattConsts = new LatticeConstGetter[] {
                    cell -> {
                        return Lattice.getB(cell) * Constants.BOHR_RADIUS_ANGS;
                    }
            };

        } else if (LatticeViewerType.C.equals(this.lattVType)) {
            numSeries = 1;
            names = new String[] { "C" };
            units = new String[] { "Angstrom" };
            lattConsts = new LatticeConstGetter[] {
                    cell -> {
                        return Lattice.getC(cell) * Constants.BOHR_RADIUS_ANGS;
                    }
            };

        } else if (LatticeViewerType.ANGLE.equals(this.lattVType)) {
            numSeries = 3;
            names = new String[] { "alpha", "beta", "gamma" };
            units = new String[] { "Degree", "Degree", "Degree" };
            lattConsts = new LatticeConstGetter[] {
                    cell -> {
                        return Lattice.getAlpha(cell);
                    },
                    cell -> {
                        return Lattice.getBeta(cell);
                    },
                    cell -> {
                        return Lattice.getGamma(cell);
                    }
            };
        }

        lineChart.getData().clear();

        for (int iSeries = 0; iSeries < numSeries; iSeries++) {
            LatticeConstGetter lattConst = lattConsts == null ? null : lattConsts[iSeries];
            if (lattConst == null) {
                continue;
            }

            Series<Number, Number> series = new Series<Number, Number>();

            for (int i = 0; i < projectGeometryList.numGeometries(); i++) {
                ProjectGeometry projectGeometry = projectGeometryList.getGeometry(i);
                double[][] cell = projectGeometry == null ? null : projectGeometry.getCell();
                double value = lattConst.getLatticeConst(cell);

                if (this.mdMode) {
                    boolean converged = projectGeometry == null ? false : projectGeometry.isConverged();
                    double time = projectGeometry == null ? 0.0 : projectGeometry.getTime();
                    if (converged && value > 0.0) {
                        series.getData().add(new Data<Number, Number>(time, value));
                    }

                } else {
                    if (value > 0.0) {
                        series.getData().add(new Data<Number, Number>(i + 1, value));
                    }
                }
            }

            lineChart.getData().add(series);
        }

        if (!this.mdMode) {
            int numConverged = 0;
            for (int i = 0; i < projectGeometryList.numGeometries(); i++) {
                ProjectGeometry projectGeometry = projectGeometryList.getGeometry(i);
                boolean converged = projectGeometry == null ? false : projectGeometry.isConverged();
                if (converged) {
                    numConverged++;
                }
            }

            int iteration = numConverged;
            String strIteration = iteration + " iteration" + (iteration > 1 ? "s were" : " was") + " done.";

            boolean converged = projectGeometryList.isConverged();
            String strConverged = "Optimization is " + (converged ? "" : "not ") + "converged.";

            String[] strCell = null;
            if (projectGeometryList.numGeometries() > 0) {
                ProjectGeometry projectGeometry = projectGeometryList.getGeometry(projectGeometryList.numGeometries() - 1);
                double[][] cell = projectGeometry == null ? null : projectGeometry.getCell();

                strCell = new String[numSeries];
                for (int iSeries = 0; iSeries < numSeries; iSeries++) {
                    String name = names == null ? null : names[iSeries];
                    String unit = units == null ? null : units[iSeries];
                    LatticeConstGetter lattConst = lattConsts == null ? null : lattConsts[iSeries];
                    if (name != null && unit != null && lattConst != null) {
                        double value = lattConst.getLatticeConst(cell);
                        if (value > 0.0) {
                            strCell[iSeries] = name + " = " + String.format("%.3f", value) + " " + unit;
                        } else {
                            strCell[iSeries] = name + " : ERROR";
                        }
                    }
                }
            }

            List<String> strList = new ArrayList<String>();
            strList.add(strIteration);
            strList.add(strConverged);
            if (strCell != null) {
                for (String str : strCell) {
                    strList.add(str);
                }
            }

            String[] strArray = strList.toArray(new String[strList.size()]);
            Node note = this.getNote(strArray);

            if (note != null) {
                this.stackNode(note, Pos.TOP_RIGHT);
            }
        }
    }

    @FunctionalInterface
    private static interface LatticeConstGetter {

        public abstract double getLatticeConst(double[][] cell);

    }
}
