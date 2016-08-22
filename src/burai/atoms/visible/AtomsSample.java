/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.visible;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import burai.atoms.element.ElementUtil;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.event.AtomEvent;
import burai.atoms.model.event.AtomEventListener;
import burai.atoms.model.event.CellEvent;
import burai.atoms.model.event.CellEventListener;
import burai.atoms.model.event.ModelEvent;

public class AtomsSample extends Group implements AtomEventListener, CellEventListener {

    private static final double BETWEEN_ATOMS = 2.0;
    private static final double TEXT_SIZE = 0.8;
    private static final String TEXT_FONT = "Times New Roman";

    private Cell cell;

    private List<Atom> sampleAtoms;
    private List<Text> sampleTexts;

    public AtomsSample(Cell cell) {
        super();

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;
        this.initializeSampleAtoms();
        this.setOneselfAsListener();
    }

    private void initializeSampleAtoms() {
        this.sampleAtoms = new ArrayList<Atom>();
        this.sampleTexts = new ArrayList<Text>();

        Atom[] atoms = this.cell.listAtoms();
        if (atoms != null) {
            for (Atom atom : atoms) {
                if ((atom != null) && (!this.hasElementInSampleAtoms(atom))) {
                    this.addElementToSampleAtoms(atom);
                }
            }
        }
    }

    private void setOneselfAsListener() {
        this.cell.addListener(this);

        Atom[] atoms = this.cell.listAtoms();
        if (atoms != null) {
            for (Atom atom : atoms) {
                if (atom != null) {
                    atom.addListener(this);
                }
            }
        }
    }

    private boolean hasElementInSampleAtoms(Atom atom) {
        return this.hasElementInSampleAtoms(atom.getName());
    }

    private boolean hasElementInSampleAtoms(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        String name2 = ElementUtil.toElementName(name);

        for (Atom sampleAtom : sampleAtoms) {
            String sampleName = ElementUtil.toElementName(sampleAtom.getName());
            if (name2.equals(sampleName)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasElementInCell(Atom atom) {
        return this.hasElementInCell(atom.getName());
    }

    private boolean hasElementInCell(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        String name2 = ElementUtil.toElementName(name);

        Atom[] atomsInCell = this.cell.listAtoms();
        if (atomsInCell != null) {
            for (Atom atomInCell : atomsInCell) {
                String nameInCell = ElementUtil.toElementName(atomInCell.getName());
                if (name2.equals(nameInCell)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void addElementToSampleAtoms(Atom atom) {
        this.addElementToSampleAtoms(atom.getName());
    }

    private void addElementToSampleAtoms(String name) {
        if (name == null || name.isEmpty()) {
            return;
        }

        String name2 = ElementUtil.toElementName(name);
        if (name2 == null || name2.trim().isEmpty()) {
            name2 = name;
        }

        double y = this.sampleAtoms.size() * BETWEEN_ATOMS;

        Atom sampleAtom = new Atom(name2, 0.0, y, 0.0);
        this.sampleAtoms.add(sampleAtom);
        this.getChildren().add(new VisibleAtom(sampleAtom, true));

        int lenName = name2.length();
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < lenName; i++) {
            nameBuilder.append(name.charAt(i));
            if (i < (lenName - 1)) {
                nameBuilder.append("  ");
            }
        }

        Text sampleText = new Text(nameBuilder.toString());
        sampleText.setFont(Font.font(TEXT_FONT, TEXT_SIZE));
        sampleText.setWrappingWidth(5.0 * TEXT_SIZE);
        sampleText.setTranslateX(0.60 * BETWEEN_ATOMS);
        sampleText.setTranslateY(y + 0.15 * BETWEEN_ATOMS);
        this.sampleTexts.add(sampleText);
        this.getChildren().add(sampleText);
    }

    private void removeElementFromSampleAtoms(Atom atom) {
        this.removeElementFromSampleAtoms(atom.getName());
    }

    private void removeElementFromSampleAtoms(String name) {
        if (name == null || name.isEmpty()) {
            return;
        }

        int iRemoved = -1;

        String name2 = ElementUtil.toElementName(name);

        for (int i = 0; i < this.sampleAtoms.size(); i++) {
            Atom sampleAtom = sampleAtoms.get(i);
            String sampleName = ElementUtil.toElementName(sampleAtom.getName());

            if (name2.equals(sampleName)) {
                iRemoved = i;

                this.sampleAtoms.remove(i);
                this.removeVisibleAtom(sampleAtom);

                if (i < this.sampleTexts.size()) {
                    Text sampleText = this.sampleTexts.remove(i);
                    if (sampleText != null) {
                        this.getChildren().remove(sampleText);
                    }
                }

                break;
            }
        }

        if (iRemoved < 0) {
            return;
        }

        for (int i = iRemoved; i < this.sampleAtoms.size(); i++) {
            Atom sampleAtom = sampleAtoms.get(i);
            sampleAtom.moveBy(0.0, -BETWEEN_ATOMS, 0.0);

            if (i < this.sampleTexts.size()) {
                Text sampleText = this.sampleTexts.get(i);
                double y = sampleText.getTranslateY();
                sampleText.setTranslateY(y - BETWEEN_ATOMS);
            }
        }
    }

    private void removeVisibleAtom(Atom sampleAtom) {
        List<Node> children = this.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (child instanceof VisibleAtom) {
                VisibleAtom visibleAtom = (VisibleAtom) child;
                if (sampleAtom == visibleAtom.getModel()) {
                    children.remove(i);
                    return;
                }
            }
        }
    }

    @Override
    public boolean isToBeFlushed() {
        return false;
    }

    @Override
    public void onModelDisplayed(ModelEvent event) {
        // NOP
    }

    @Override
    public void onModelNotDisplayed(ModelEvent event) {
        // NOP
    }

    @Override
    public void onLatticeMoved(CellEvent event) {
        // NOP
    }

    @Override
    public void onAtomAdded(CellEvent event) {
        Atom atom = event.getAtom();

        if (atom == null) {
            return;
        }

        atom.addListener(this);

        if (this.hasElementInSampleAtoms(atom)) {
            return;
        }

        this.addElementToSampleAtoms(atom);
    }

    @Override
    public void onAtomRemoved(CellEvent event) {
        Atom atom = event.getAtom();

        if (atom == null) {
            return;
        }

        if (this.hasElementInCell(atom)) {
            return;
        }

        this.removeElementFromSampleAtoms(atom);
    }

    @Override
    public void onBondAdded(CellEvent event) {
        // NOP
    }

    @Override
    public void onBondRemoved(CellEvent event) {
        // NOP
    }

    @Override
    public void onAtomRenamed(AtomEvent event) {
        String newName = event.getName();
        String oldName = event.getOldName();

        if ((oldName != null) && (!this.hasElementInCell(oldName))) {
            this.removeElementFromSampleAtoms(oldName);
        }

        if ((newName != null) && (!this.hasElementInSampleAtoms(newName))) {
            this.addElementToSampleAtoms(newName);
        }
    }

    @Override
    public void onAtomMoved(AtomEvent event) {
        // NOP
    }
}
