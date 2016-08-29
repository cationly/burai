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

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.paint.Color;
import burai.app.project.QEFXProjectController;
import burai.atoms.element.ElementUtil;
import burai.com.env.Environments;
import burai.com.parallel.Parallel;
import burai.project.property.DosData;
import burai.project.property.DosInterface;
import burai.project.property.DosType;
import burai.project.property.ProjectDos;
import burai.project.property.ProjectEnergies;

public class QEFXDosViewerController extends QEFXGraphViewerController {

    private static final double ENERGY_GRID = 0.05;
    private static final double DOS_THRESHOLD = 1.0e-8;

    private static final int NUM_LOADING_THREADS = Math.max(1, Environments.getNumCUPs() - 1);

    private DosData tdosData;

    private List<PDosData> pdosDataList;

    private ProjectEnergies projectEnergies;

    private ProjectDos projectDos;

    public QEFXDosViewerController(
            QEFXProjectController projectController, ProjectEnergies projectEnergies, ProjectDos projectDos) {

        super(projectController, Pos.BOTTOM_RIGHT);

        if (projectEnergies == null) {
            throw new IllegalArgumentException("projectEnergies is null.");
        }

        if (projectDos == null) {
            throw new IllegalArgumentException("projectDos is null.");
        }

        this.projectEnergies = projectEnergies;
        this.projectDos = projectDos;

        this.tdosData = null;
        this.pdosDataList = null;
    }

    private void createDosData() {

        this.projectDos.reload();

        List<DosData> dosDataList = this.projectDos.listDosData();
        if (dosDataList == null || dosDataList.isEmpty()) {
            return;
        }

        if (this.pdosDataList == null) {
            this.pdosDataList = new ArrayList<PDosData>();
        } else {
            this.pdosDataList.clear();
        }

        for (DosData dosData : dosDataList) {
            if (dosData == null) {
                continue;
            }

            DosType dosType = dosData.getType();
            if (dosType == null) {
                continue;
            }

            if (DosType.TOTAL.equals(dosType)) {
                this.tdosData = dosData;
                continue;
            }

            String atomName = dosData.getAtomName();
            //PDosData pdosData = new PDosData(dosType, atomName);
            PDosData pdosData = new PDosData(null, atomName);

            int index = this.pdosDataList.indexOf(pdosData);
            if (index < 0) {
                this.pdosDataList.add(pdosData);
            } else {
                pdosData = this.pdosDataList.get(index);
            }

            if (pdosData != null) {
                pdosData.addDosData(dosData);
            }
        }
    }

    @Override
    protected GraphProperty createProperty() {

        this.createDosData();

        GraphProperty property = new GraphProperty();

        property.setTitle("Density of states");
        property.setXLabel("Energy / eV");
        property.setYLabel("DOS / (states/eV)");

        if (this.tdosData != null) {
            String[] spinLabel = null;
            if (this.tdosData.isSpinPolarized()) {
                spinLabel = new String[] { " [up]", " [down]" };
            } else {
                spinLabel = new String[] { "" };
            }

            for (String label : spinLabel) {
                SeriesProperty seriesProperty = new SeriesProperty();
                seriesProperty.setName("Total" + label);
                seriesProperty.setColor("black");
                seriesProperty.setDash(SeriesProperty.DASH_NULL);
                seriesProperty.setWithSymbol(false);
                seriesProperty.setWidth(1.0);
                property.addSeries(seriesProperty);
            }
        }

        if (this.pdosDataList != null && this.pdosDataList.size() > 1) {
            for (PDosData pdosData : this.pdosDataList) {
                if (pdosData == null) {
                    continue;
                }

                String[] spinLabel = null;
                if (pdosData.isSpinPolarized()) {
                    spinLabel = new String[] { " [up]", " [down]" };
                } else {
                    spinLabel = new String[] { "" };
                }

                for (String label : spinLabel) {
                    SeriesProperty seriesProperty = new SeriesProperty();
                    //seriesProperty.setName(pdosData.toString() + label);
                    seriesProperty.setName(pdosData.getAtomName() + label);
                    seriesProperty.setColor(pdosData.getColor());
                    seriesProperty.setDash(pdosData.getDashType());
                    seriesProperty.setWithSymbol(false);
                    seriesProperty.setWidth(1.0);
                    property.addSeries(seriesProperty);
                }
            }
        }

        return property;
    }

    @Override
    protected void reloadData(LineChart<Number, Number> lineChart) {
        if (lineChart == null) {
            return;
        }

        ProjectEnergies projectEnergies = this.projectEnergies.copyEnergies();
        if (projectEnergies == null || projectEnergies.numEnergies() < 1) {
            lineChart.getData().clear();
            return;
        }

        this.createDosData();

        lineChart.getData().clear();

        double fermi = projectEnergies.getEnergy(0);
        double energyMin = this.getMinimumEnergy();
        double energyMax = this.getMaximumEnergy();

        if (this.tdosData != null) {
            this.reloadDosData(lineChart, this.tdosData, fermi, energyMin, energyMax);
        }

        if (this.pdosDataList != null && this.pdosDataList.size() > 1) {
            for (PDosData pdosData : this.pdosDataList) {
                if (pdosData != null) {
                    this.reloadDosData(lineChart, pdosData, fermi, energyMin, energyMax);
                }
            }
        }
    }

    private double getMinimumEnergy() {
        double energyMin = -Double.MAX_VALUE;

        if (this.tdosData != null) {
            int numData = this.tdosData.numPoints();
            for (int i = 0; i < numData; i++) {
                double energy = this.tdosData.getEnergy(i);
                double dos1 = Math.abs(this.tdosData.getDosUp(i));
                double dos2 = Math.abs(this.tdosData.getDosDown(i));
                double dos = Math.max(dos1, dos2);
                if (dos > DOS_THRESHOLD) {
                    energyMin = energy;
                    break;
                }
            }
        }

        return energyMin;
    }

    private double getMaximumEnergy() {
        double energyMax = Double.MAX_VALUE;

        if (this.tdosData != null) {
            int numData = this.tdosData.numPoints();
            for (int i = (numData - 1); i >= 0; i--) {
                double energy = this.tdosData.getEnergy(i);
                double dos1 = Math.abs(this.tdosData.getDosUp(i));
                double dos2 = Math.abs(this.tdosData.getDosDown(i));
                double dos = Math.max(dos1, dos2);
                if (dos > DOS_THRESHOLD) {
                    energyMax = energy;
                    break;
                }
            }
        }

        return energyMax;
    }

    private void reloadDosData(LineChart<Number, Number> lineChart,
            DosInterface dosData, double fermi, double energyMin, double energyMax) {

        if (lineChart == null) {
            return;
        }

        if (dosData == null) {
            return;
        }

        int numData = dosData.numPoints();
        boolean spinPolarized = dosData.isSpinPolarized();

        Series<Number, Number> series1 = new Series<Number, Number>();
        Series<Number, Number> series2 = spinPolarized ? new Series<Number, Number>() : null;

        Platform.runLater(() -> {
            int idelta = 1;
            if (numData > 1) {
                double energy1 = dosData.getEnergy(0);
                double energy2 = dosData.getEnergy(1);
                idelta = (int) (Math.rint(ENERGY_GRID / (energy2 - energy1)) + 0.1);
                idelta = Math.max(1, idelta);
            }

            List<Integer> indexList = new ArrayList<Integer>();
            for (int i = 0; i < numData; i += idelta) {
                double energy = dosData.getEnergy(i);
                if (energy < energyMin || energyMax < energy) {
                    continue;
                }
                indexList.add(i);
            }

            Integer[] indexes = indexList.toArray(new Integer[indexList.size()]);
            List<Data<Number, Number>> dataList1 = new ArrayList<Data<Number, Number>>();
            List<Data<Number, Number>> dataList2 = spinPolarized ? new ArrayList<Data<Number, Number>>() : null;

            Parallel<Integer, Object> parallel = new Parallel<Integer, Object>(indexes);
            parallel.setNumThreads(NUM_LOADING_THREADS);
            parallel.forEach(i -> {
                double energy = dosData.getEnergy(i);
                energy -= fermi;

                double dos1 = dosData.getDosUp(i);
                synchronized (dataList1) {
                    dataList1.add(new Data<Number, Number>(energy, dos1));
                }

                if (spinPolarized) {
                    double dos2 = -dosData.getDosDown(i);
                    synchronized (dataList2) {
                        dataList2.add(new Data<Number, Number>(energy, dos2));
                    }
                }

                return null;
            });

            series1.getData().addAll(dataList1);
            if (spinPolarized) {
                series2.getData().addAll(dataList2);
            }
        });

        lineChart.getData().add(series1);
        if (spinPolarized) {
            lineChart.getData().add(series2);
        }
    }

    private static class PDosData implements DosInterface {

        private DosType dosType;

        private String atomName;

        private List<DosData> dosDataList;

        public PDosData(DosType dosType, String atomName) {
            this.dosType = dosType;
            this.atomName = atomName;
            this.dosDataList = null;
        }

        public void addDosData(DosData dosData) {
            if (dosData == null) {
                return;
            }

            DosType dosType = dosData.getType();
            if (this.dosType == null) {
                // NOP
            } else {
                if (!this.dosType.equals(dosType)) {
                    return;
                }
            }

            String atomName = dosData.getAtomName();
            if (this.atomName == null) {
                // NOP
            } else {
                if (!this.atomName.equals(atomName)) {
                    return;
                }
            }

            if (this.dosDataList == null) {
                this.dosDataList = new ArrayList<DosData>();
            }

            this.dosDataList.add(dosData);
        }

        @Override
        public DosType getType() {
            return this.dosType;
        }

        @Override
        public boolean isSpinPolarized() {
            if (this.dosDataList != null) {
                for (DosData dosData : this.dosDataList) {
                    if (dosData != null && dosData.isSpinPolarized()) {
                        return true;
                    }
                }
            }

            return false;
        }

        @Override
        public int getAtomIndex() {
            return -1;
        }

        @Override
        public String getAtomName() {
            return this.atomName;
        }

        @Override
        public int numPoints() {
            if (this.dosDataList == null || this.dosDataList.isEmpty()) {
                return 0;
            }

            DosData dosData = this.dosDataList.get(0);
            int num = dosData == null ? 0 : dosData.numPoints();

            for (int i = 1; i < this.dosDataList.size(); i++) {
                dosData = this.dosDataList.get(i);
                num = Math.min(num, dosData == null ? 0 : dosData.numPoints());
            }

            return num;
        }

        @Override
        public double getEnergy(int i) {
            if (this.dosDataList == null || this.dosDataList.isEmpty()) {
                return 0.0;
            }

            DosData dosData = this.dosDataList.get(0);
            return dosData == null ? 0.0 : dosData.getEnergy(i);
        }

        @Override
        public double getDosUp(int i) {
            if (this.dosDataList == null || this.dosDataList.isEmpty()) {
                return 0.0;
            }

            double dos = 0.0;
            for (DosData dosData : this.dosDataList) {
                if (dosData != null) {
                    dos += dosData.getDosUp(i);
                }
            }

            return dos;
        }

        @Override
        public double getDosDown(int i) {
            if (this.dosDataList == null || this.dosDataList.isEmpty()) {
                return 0.0;
            }

            double dos = 0.0;
            for (DosData dosData : this.dosDataList) {
                if (dosData != null) {
                    dos += dosData.getDosDown(i);
                }
            }

            return dos;
        }

        public String getColor() {
            Color color = this.atomName == null ? null : ElementUtil.getColor(this.atomName, Color.LIGHTGRAY);
            String strColor = color == null ? null : color.toString();
            strColor = strColor == null ? "black" : strColor.replaceAll("0x", "#");
            return strColor;
        }

        public int getDashType() {
            if (DosType.PDOS_S.equals(this.dosType)) {
                return SeriesProperty.DASH_NULL;

            } else if (DosType.PDOS_P.equals(this.dosType)) {
                return SeriesProperty.DASH_SMALL;

            } else if (DosType.PDOS_D.equals(this.dosType)) {
                return SeriesProperty.DASH_LARGE;

            } else if (DosType.PDOS_F.equals(this.dosType)) {
                return SeriesProperty.DASH_HYBRID;
            }

            return SeriesProperty.DASH_NULL;
        }

        @Override
        public String toString() {
            String strOrbital = this.dosType == null ? null : this.dosType.getOrbital();
            if (strOrbital == null) {
                strOrbital = "*";
            }

            String strAtomName = this.atomName;
            if (strAtomName == null) {
                strAtomName = "X";
            }

            return strAtomName + " (" + strOrbital + ")";
        }

        @Override
        public int hashCode() {
            return (this.dosType == null ? 0 : this.dosType.getMomentum())
                    + (this.atomName == null ? 0 : this.atomName.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }

            PDosData other = (PDosData) obj;

            if (this.dosType == null) {
                if (this.dosType != other.dosType) {
                    return false;
                }
            } else {
                if (!this.dosType.equals(other.dosType)) {
                    return false;
                }
            }

            if (this.atomName == null) {
                if (this.atomName != other.atomName) {
                    return false;
                }
            } else {
                if (!this.atomName.equals(other.atomName)) {
                    return false;
                }
            }

            return true;
        }
    }
}
