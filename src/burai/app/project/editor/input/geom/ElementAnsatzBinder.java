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
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECardEvent;

public class ElementAnsatzBinder {

    private TableView<ElementAnsatz> elementTable;

    private QEAtomicSpecies atomicSpecies;

    public ElementAnsatzBinder(TableView<ElementAnsatz> elementTable, QEAtomicSpecies atomicSpecies) {
        if (elementTable == null) {
            throw new IllegalArgumentException("elementTable is null.");
        }

        if (atomicSpecies == null) {
            throw new IllegalArgumentException("atomicSpecies is null.");
        }

        this.elementTable = elementTable;
        this.atomicSpecies = atomicSpecies;
    }

    public void bindTable() {
        this.setupElementTable();
        this.setupAtomicSpecies();
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

        double mass = this.atomicSpecies.getMass(index);

        String pseudo = this.atomicSpecies.getPseudoName(index);
        if (pseudo == null) {
            return null;
        }

        ElementAnsatz element = new ElementAnsatz(index);

        element.setName(label);
        element.setMass(mass);
        element.setPseudo(pseudo);

        element.massProperty().addListener(o -> this.actionOnMassChanged(element));
        element.pseudoProperty().addListener(o -> this.actionOnPseudoChanged(element));

        return element;
    }

    private void actionOnMassChanged(ElementAnsatz element) {
        if (element == null) {
            return;
        }

        int index = element.getIndex();
        if (index < 0 || this.atomicSpecies.numSpecies() <= index) {
            return;
        }

        double mass = 0.0;

        try {
            mass = element.getMassValue();
        } catch (RuntimeException e) {
            mass = this.atomicSpecies.getMass(index);
        }

        this.atomicSpecies.setMass(index, mass);
    }

    private void actionOnPseudoChanged(ElementAnsatz element) {
        if (element == null) {
            return;
        }

        int index = element.getIndex();
        if (index < 0 || this.atomicSpecies.numSpecies() <= index) {
            return;
        }

        String pseudo = element.getPseudo();
        this.atomicSpecies.setPseudoPotential(index, pseudo);
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

        double mass = this.atomicSpecies.getMass(index);

        String pseudo = this.atomicSpecies.getPseudoName(index);
        if (pseudo == null) {
            return;
        }

        ElementAnsatz element = this.pickOutElement(index);
        if (element == null) {
            return;
        }

        element.setName(label);
        element.setMass(mass);
        element.setPseudo(pseudo);
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
}
