/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.scf;

import java.util.List;

import javafx.scene.control.TableView;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECardEvent;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class ElementAnsatzBinder {

    private static final int MAX_NUM_ELEMENTS = 16;

    private TableView<ElementAnsatz> elementTable;

    private QEAtomicSpecies atomicSpecies;

    private QENamelist nmlSystem;

    private boolean busyMags;

    public ElementAnsatzBinder(
            TableView<ElementAnsatz> elementTable, QEAtomicSpecies atomicSpecies, QENamelist nmlSystem) {

        if (elementTable == null) {
            throw new IllegalArgumentException("elementTable is null.");
        }

        if (atomicSpecies == null) {
            throw new IllegalArgumentException("atomicSpecies is null.");
        }

        if (nmlSystem == null) {
            throw new IllegalArgumentException("nmlSystem is null.");
        }

        this.elementTable = elementTable;
        this.atomicSpecies = atomicSpecies;
        this.nmlSystem = nmlSystem;
        this.busyMags = false;
    }

    public void bindTable() {
        this.setupElementTable();
        this.setupAtomicSpecies();
        this.setupNmlSystem();
    }

    private double[] xyzToPolar(double x, double y, double z) {
        double r = 0.0;
        double theta = 0.0;
        double phi = 0.0;

        QEValue noncolinValue = this.nmlSystem.getValue("noncolin");
        if (noncolinValue != null && noncolinValue.getLogicalValue()) {
            r = Math.sqrt(x * x + y * y + z * z);
            theta = (180.0 / Math.PI) * Math.atan2(Math.sqrt(x * x + y * y), z);
            phi = (180.0 / Math.PI) * Math.atan2(y, x);

        } else {
            r = z;
            theta = 0.0;
            phi = 0.0;
        }

        return new double[] { r, theta, phi };
    }

    private double[] polarToXYZ(double r, double theta, double phi) {
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        QEValue noncolinValue = this.nmlSystem.getValue("noncolin");
        if (noncolinValue != null && noncolinValue.getLogicalValue()) {
            double sin1 = Math.sin((Math.PI / 180.0) * theta);
            double cos1 = Math.cos((Math.PI / 180.0) * theta);
            double sin2 = Math.sin((Math.PI / 180.0) * phi);
            double cos2 = Math.cos((Math.PI / 180.0) * phi);
            x = r * sin1 * cos2;
            y = r * sin1 * sin2;
            z = r * cos1;

        } else {
            x = 0.0;
            y = 0.0;
            z = r;
        }

        return new double[] { x, y, z };
    }

    private void setupElementTable() {
        int numElems = this.atomicSpecies.numSpecies();
        for (int i = 0; i < numElems; i++) {
            ElementAnsatz element = this.createElement(i);
            if (element != null) {
                this.elementTable.getItems().add(element);
            }
        }
    }

    private ElementAnsatz createElement(int index) {
        if (index < 0 || this.atomicSpecies.numSpecies() <= index) {
            return null;
        }

        String label = this.atomicSpecies.getLabel(index);
        if (label == null) {
            return null;
        }

        QEValue radMagValue = this.nmlSystem.getValue("starting_magnetization(" + (index + 1) + ")");
        double radMag = radMagValue == null ? 0.0 : radMagValue.getRealValue();

        QEValue angle1Value = this.nmlSystem.getValue("angle1(" + (index + 1) + ")");
        double angle1 = angle1Value == null ? 0.0 : angle1Value.getRealValue();

        QEValue angle2Value = this.nmlSystem.getValue("angle2(" + (index + 1) + ")");
        double angle2 = angle2Value == null ? 0.0 : angle2Value.getRealValue();

        double[] xyzMag = this.polarToXYZ(radMag, angle1, angle2);
        double xMag = xyzMag[0];
        double yMag = xyzMag[1];
        double zMag = xyzMag[2];

        QEValue hubbardValue = this.nmlSystem.getValue("hubbard_u(" + (index + 1) + ")");
        double hubbard = hubbardValue == null ? 0.0 : hubbardValue.getRealValue();

        ElementAnsatz element = new ElementAnsatz(index);

        element.setName(label);
        element.setMagX(xMag);
        element.setMagY(yMag);
        element.setMagZ(zMag);
        element.setHubbard(hubbard);

        element.magXProperty().addListener(o -> this.actionOnMagChanged(element));
        element.magYProperty().addListener(o -> this.actionOnMagChanged(element));
        element.magZProperty().addListener(o -> this.actionOnMagChanged(element));
        element.hubbardProperty().addListener(o -> this.actionOnHubbardChanged(element));

        return element;
    }

    private void actionOnMagChanged(ElementAnsatz element) {
        if (this.busyMags) {
            return;
        }

        if (element == null) {
            return;
        }

        int index = element.getIndex();
        if (index < 0 || this.atomicSpecies.numSpecies() <= index) {
            return;
        }

        double xMag = 0.0;
        boolean xReset = false;
        try {
            xMag = element.getMagXValue();
        } catch (RuntimeException e) {
            xReset = true;
        }

        double yMag = 0.0;
        boolean yReset = false;
        try {
            yMag = element.getMagYValue();
        } catch (RuntimeException e) {
            yReset = true;
        }

        double zMag = 0.0;
        boolean zReset = false;
        try {
            zMag = element.getMagZValue();
        } catch (RuntimeException e) {
            zReset = true;
        }

        if (xReset || yReset || zReset) {
            QEValue radMagValue = this.nmlSystem.getValue("starting_magnetization(" + (index + 1) + ")");
            double radMag = radMagValue == null ? 0.0 : radMagValue.getRealValue();

            QEValue angle1Value = this.nmlSystem.getValue("angle1(" + (index + 1) + ")");
            double angle1 = angle1Value == null ? 0.0 : angle1Value.getRealValue();

            QEValue angle2Value = this.nmlSystem.getValue("angle2(" + (index + 1) + ")");
            double angle2 = angle2Value == null ? 0.0 : angle2Value.getRealValue();

            double[] xyzMag = this.polarToXYZ(radMag, angle1, angle2);

            if (xReset) {
                xMag = xyzMag[0];
            }
            if (yReset) {
                yMag = xyzMag[1];
            }
            if (zReset) {
                zMag = xyzMag[2];
            }
        }

        double[] polarMag = this.xyzToPolar(xMag, yMag, zMag);

        this.busyMags = true;

        this.nmlSystem.setValue("starting_magnetization(" + (index + 1) + ") = " + polarMag[0]);
        this.nmlSystem.setValue("angle1(" + (index + 1) + ") = " + polarMag[1]);
        this.nmlSystem.setValue("angle2(" + (index + 1) + ") = " + polarMag[2]);

        this.busyMags = false;

        this.actionOnInputMagChanged(index);
    }

    private void actionOnHubbardChanged(ElementAnsatz element) {
        if (element == null) {
            return;
        }

        int index = element.getIndex();
        if (index < 0 || this.atomicSpecies.numSpecies() <= index) {
            return;
        }

        double hubbard = 0.0;
        try {
            hubbard = element.getHubbardValue();
        } catch (RuntimeException e) {
            QEValue hubbardValue = this.nmlSystem.getValue("hubbard_u(" + (index + 1) + ")");
            hubbard = hubbardValue == null ? 0.0 : hubbardValue.getRealValue();
        }

        this.nmlSystem.setValue("hubbard_u(" + (index + 1) + ") = " + hubbard);
    }

    private void setupAtomicSpecies() {
        this.atomicSpecies.addListener(event -> {
            if (event == null) {
                return;
            }

            int eventType = event.getEventType();
            int index = event.getSpeciesIndex();

            if (eventType == QECardEvent.EVENT_TYPE_SPECIES_CHANGED) {
                this.actionOnSpeciesChanged(index);

            } else if (eventType == QECardEvent.EVENT_TYPE_SPECIES_ADDED) {
                this.actionOnSpeciesAdded(index);

            } else if (eventType == QECardEvent.EVENT_TYPE_SPECIES_REMOVED) {
                this.actionOnSpeciesRemoved(index);

            } else if (eventType == QECardEvent.EVENT_TYPE_SPECIES_CLEARED) {
                this.actionOnSpeciesCleared();

            } else {
                this.actionForAllSpecies();
            }
        });
    }

    private ElementAnsatz pickOutElement(int index) {
        List<ElementAnsatz> elements = this.elementTable.getItems();
        if (elements == null) {
            return null;
        }

        for (ElementAnsatz element : elements) {
            if (element == null) {
                continue;
            }
            if (index == element.getIndex()) {
                return element;
            }
        }

        return null;
    }

    private void actionOnSpeciesChanged(int index) {
        String label = this.atomicSpecies.getLabel(index);
        if (label == null) {
            return;
        }

        ElementAnsatz element = this.pickOutElement(index);
        if (element == null) {
            return;
        }

        element.setName(label);
    }

    private void actionOnSpeciesAdded(int index) {
        ElementAnsatz element = this.createElement(index);
        if (element != null) {
            this.elementTable.getItems().add(element);
        }
    }

    private void actionOnSpeciesRemoved(int index) {
        ElementAnsatz removedElement = this.pickOutElement(index);
        if (removedElement == null) {
            return;
        }

        this.elementTable.getItems().remove(removedElement);

        for (ElementAnsatz element : this.elementTable.getItems()) {
            if (element.getIndex() >= index) {
                element.setIndex(element.getIndex() - 1);
            }
        }
    }

    private void actionOnSpeciesCleared() {
        this.elementTable.getItems().clear();
    }

    private void actionForAllSpecies() {
        this.elementTable.getItems().clear();
        this.setupElementTable();
    }

    private void setupNmlSystem() {
        for (int i = 0; i < MAX_NUM_ELEMENTS; i++) {
            QEValueBuffer radMagBuffer = this.nmlSystem.getValueBuffer("starting_magnetization(" + (i + 1) + ")");
            QEValueBuffer angle1Buffer = this.nmlSystem.getValueBuffer("angle1(" + (i + 1) + ")");
            QEValueBuffer angle2Buffer = this.nmlSystem.getValueBuffer("angle2(" + (i + 1) + ")");
            QEValueBuffer hubbardBuffer = this.nmlSystem.getValueBuffer("hubbard_u(" + (i + 1) + ")");

            int index = i;
            radMagBuffer.addListener(value -> this.actionOnInputMagChanged(index));
            angle1Buffer.addListener(value -> this.actionOnInputMagChanged(index));
            angle2Buffer.addListener(value -> this.actionOnInputMagChanged(index));
            hubbardBuffer.addListener(value -> this.actionOnInputHubbardChanged(index));
        }

        QEValueBuffer noncolinBuffer = this.nmlSystem.getValueBuffer("noncolin");
        noncolinBuffer.addListener(value -> {
            for (int i = 0; i < MAX_NUM_ELEMENTS; i++) {
                boolean status = this.actionOnInputMagChanged(i);
                if (!status) {
                    break;
                }
            }
        });
    }

    private boolean actionOnInputMagChanged(int index) {
        if (this.busyMags) {
            return false;
        }

        ElementAnsatz element = this.pickOutElement(index);
        if (element == null) {
            return false;
        }

        QEValue radMagValue = this.nmlSystem.getValue("starting_magnetization(" + (index + 1) + ")");
        double radMag = radMagValue == null ? 0.0 : radMagValue.getRealValue();

        QEValue angle1Value = this.nmlSystem.getValue("angle1(" + (index + 1) + ")");
        double angle1 = angle1Value == null ? 0.0 : angle1Value.getRealValue();

        QEValue angle2Value = this.nmlSystem.getValue("angle2(" + (index + 1) + ")");
        double angle2 = angle2Value == null ? 0.0 : angle2Value.getRealValue();

        double[] xyzMag = this.polarToXYZ(radMag, angle1, angle2);

        String xMagOld = element.getMagX();
        String yMagOld = element.getMagY();
        String zMagOld = element.getMagZ();

        this.busyMags = true;

        element.setMagX(xyzMag[0]);
        element.setMagY(xyzMag[1]);
        element.setMagZ(xyzMag[2]);

        this.busyMags = false;

        String xMagNew = element.getMagX();
        String yMagNew = element.getMagY();
        String zMagNew = element.getMagZ();

        boolean xSame = false;
        if (xMagOld == null) {
            xSame = xMagOld == xMagNew;
        } else {
            xSame = xMagOld.equals(xMagNew);
        }

        boolean ySame = false;
        if (yMagOld == null) {
            ySame = yMagOld == yMagNew;
        } else {
            ySame = yMagOld.equals(yMagNew);
        }

        boolean zSame = false;
        if (zMagOld == null) {
            zSame = zMagOld == zMagNew;
        } else {
            zSame = zMagOld.equals(zMagNew);
        }

        if ((!xSame) || (!ySame) || (!zSame)) {
            this.actionOnMagChanged(element);
        }

        return true;
    }

    private boolean actionOnInputHubbardChanged(int index) {
        ElementAnsatz element = this.pickOutElement(index);
        if (element == null) {
            return false;
        }

        QEValue hubbardValue = this.nmlSystem.getValue("hubbard_u(" + (index + 1) + ")");
        double hubbard = hubbardValue == null ? 0.0 : hubbardValue.getRealValue();

        element.setHubbard(hubbard);

        return true;
    }
}
