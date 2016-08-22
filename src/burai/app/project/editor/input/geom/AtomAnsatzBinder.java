/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.geom;

import java.util.List;

import javafx.scene.control.TableView;
import burai.atoms.element.ElementUtil;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QECardEvent;

public class AtomAnsatzBinder {

    private TableView<AtomAnsatz> atomTable;

    private QEAtomicPositions atomicPositions;

    public AtomAnsatzBinder(TableView<AtomAnsatz> atomTable, QEAtomicPositions atomicPositions) {
        if (atomTable == null) {
            throw new IllegalArgumentException("atomTable is null.");
        }

        if (atomicPositions == null) {
            throw new IllegalArgumentException("atomicPositions is null.");
        }

        this.atomTable = atomTable;
        this.atomicPositions = atomicPositions;
    }

    public void bindTable() {
        this.setupAtomTable();
        this.setupAtomicPositions();
    }

    private void setupAtomTable() {
        int numAtoms = this.atomicPositions.numPositions();
        for (int i = 0; i < numAtoms; i++) {
            AtomAnsatz atom = this.createAtom(i);
            if (atom != null) {
                this.atomTable.getItems().add(atom);
            }
        }
    }

    private AtomAnsatz createAtom(int index) {
        if (index < 0 || this.atomicPositions.numPositions() <= index) {
            return null;
        }

        String label = this.atomicPositions.getLabel(index);
        if (label == null) {
            return null;
        }

        double[] position = this.atomicPositions.getPosition(index);
        if (position == null || position.length < 3) {
            return null;
        }

        boolean[] mobile = this.atomicPositions.getMobile(index);
        if (mobile == null || mobile.length < 3) {
            return null;
        }

        AtomAnsatz atom = new AtomAnsatz(index);

        atom.setElement(label);
        atom.setX(position[0], mobile[0]);
        atom.setY(position[1], mobile[1]);
        atom.setZ(position[2], mobile[2]);

        atom.elementProperty().addListener(o -> this.actionOnElementChanged(atom));
        atom.xProperty().addListener(o -> this.actionOnXYZChanged(atom));
        atom.yProperty().addListener(o -> this.actionOnXYZChanged(atom));
        atom.zProperty().addListener(o -> this.actionOnXYZChanged(atom));

        return atom;
    }

    private void actionOnElementChanged(AtomAnsatz atom) {
        if (atom == null) {
            return;
        }

        int index = atom.getIndex();
        if (index < 0 || this.atomicPositions.numPositions() <= index) {
            return;
        }

        String element = atom.getElement();
        String label = ElementUtil.toAvailableName(element);
        this.atomicPositions.setLabel(index, label);
    }

    private void actionOnXYZChanged(AtomAnsatz atom) {
        if (atom == null) {
            return;
        }

        int index = atom.getIndex();
        if (index < 0 || this.atomicPositions.numPositions() <= index) {
            return;
        }

        double[] position = this.atomicPositions.getPosition(index);
        boolean[] mobile = this.atomicPositions.getMobile(index);
        this.createPosition(atom, position, mobile);

        this.atomicPositions.setPosition(index, position);
        this.atomicPositions.setMobile(index, mobile);
    }

    private void createPosition(AtomAnsatz atom, double[] position, boolean[] mobile) {
        if (atom == null) {
            return;
        }

        if (position == null || position.length < 3) {
            return;
        }

        if (mobile == null || mobile.length < 3) {
            return;
        }

        try {
            double xPosition = atom.getXValue();
            position[0] = xPosition;
        } catch (RuntimeException e) {
            // NOP
        }

        try {
            double yPosition = atom.getYValue();
            position[1] = yPosition;
        } catch (RuntimeException e) {
            // NOP
        }

        try {
            double zPosition = atom.getZValue();
            position[2] = zPosition;
        } catch (RuntimeException e) {
            // NOP
        }

        try {
            boolean xMobile = atom.isXMobile();
            mobile[0] = xMobile;
        } catch (RuntimeException e) {
            // NOP
        }

        try {
            boolean yMobile = atom.isYMobile();
            mobile[1] = yMobile;
        } catch (RuntimeException e) {
            // NOP
        }

        try {
            boolean zMobile = atom.isZMobile();
            mobile[2] = zMobile;
        } catch (RuntimeException e) {
            // NOP
        }
    }

    private void setupAtomicPositions() {
        this.atomicPositions.addListener(event -> {
            if (event == null) {
                return;
            }

            int eventType = event.getEventType();
            int index = event.getAtomIndex();

            if (eventType == QECardEvent.EVENT_TYPE_ATOM_CHANGED) {
                this.actionOnAtomChanged(index);

            } else if (eventType == QECardEvent.EVENT_TYPE_ATOM_MOVED) {
                this.actionOnAtomChanged(index);

            } else if (eventType == QECardEvent.EVENT_TYPE_ATOM_ADDED) {
                this.actionOnAtomAdded(index);

            } else if (eventType == QECardEvent.EVENT_TYPE_ATOM_REMOVED) {
                this.actionOnAtomRemoved(index);

            } else if (eventType == QECardEvent.EVENT_TYPE_ATOM_CLEARED) {
                this.actionOnAtomsCleared();

            } else {
                this.actionForAllAtoms();
            }
        });
    }

    private AtomAnsatz pickOutAtom(int index) {
        List<AtomAnsatz> atoms = this.atomTable.getItems();
        if (atoms == null) {
            return null;
        }

        for (AtomAnsatz atom : atoms) {
            if (atom == null) {
                continue;
            }
            if (index == atom.getIndex()) {
                return atom;
            }
        }

        return null;
    }

    private void actionOnAtomChanged(int index) {
        String label = this.atomicPositions.getLabel(index);
        if (label == null) {
            return;
        }

        double[] position = this.atomicPositions.getPosition(index);
        if (position == null || position.length < 3) {
            return;
        }

        boolean[] mobile = this.atomicPositions.getMobile(index);
        if (mobile == null || mobile.length < 3) {
            return;
        }

        AtomAnsatz atom = this.pickOutAtom(index);
        if (atom == null) {
            return;
        }

        atom.setElement(label);
        atom.setX(position[0], mobile[0]);
        atom.setY(position[1], mobile[1]);
        atom.setZ(position[2], mobile[2]);
    }

    private void actionOnAtomAdded(int index) {
        AtomAnsatz atom = this.createAtom(index);
        if (atom != null) {
            this.atomTable.getItems().add(atom);
        }
    }

    private void actionOnAtomRemoved(int index) {
        AtomAnsatz removedAtom = this.pickOutAtom(index);
        if (removedAtom == null) {
            return;
        }

        this.atomTable.getItems().remove(removedAtom);

        for (AtomAnsatz atom : this.atomTable.getItems()) {
            if (atom.getIndex() >= index) {
                atom.setIndex(atom.getIndex() - 1);
            }
        }
    }

    private void actionOnAtomsCleared() {
        this.atomTable.getItems().clear();
    }

    private void actionForAllAtoms() {
        this.atomTable.getItems().clear();
        this.setupAtomTable();
    }

    public void addAtom(AtomAnsatz atom) {
        if (atom == null) {
            return;
        }

        String label = atom.getElement();
        double[] position = { 0.0, 0.0, 0.0 };
        boolean[] mobile = { true, true, true };
        this.createPosition(atom, position, mobile);

        this.atomicPositions.addPosition(label, position, mobile);
    }

    public void removeAtom(AtomAnsatz atom) {
        if (atom == null) {
            return;
        }

        this.atomicPositions.removePosition(atom.getIndex());
    }
}
