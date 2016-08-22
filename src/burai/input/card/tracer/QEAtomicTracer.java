/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.card.tracer;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import burai.atoms.element.ElementUtil;
import burai.input.QEInput;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECardEvent;
import burai.input.card.QECardListener;
import burai.input.namelist.QEInteger;
import burai.input.namelist.QENamelist;
import burai.pseudo.PseudoLibrary;
import burai.pseudo.PseudoPotential;

public class QEAtomicTracer implements QECardListener {

    private QENamelist nmlSystem;

    private QEAtomicSpecies atomicSpecies;

    private QEAtomicPositions atomicPositions;

    private Map<String, Double> traceMapMass;

    private Map<String, String> traceMapPseudo;

    public QEAtomicTracer(QENamelist nmlSystem, QEAtomicSpecies atomicSpecies) {
        if (nmlSystem == null || !QEInput.NAMELIST_SYSTEM.equals(nmlSystem.getName())) {
            throw new IllegalArgumentException("nmlSystem is incorrect.");
        }

        if (atomicSpecies == null) {
            throw new IllegalArgumentException("atomicSpecies is null.");
        }

        this.nmlSystem = nmlSystem;
        this.atomicSpecies = atomicSpecies;
        this.atomicPositions = null;
        this.traceMapMass = null;
        this.traceMapPseudo = null;
    }

    public void traceAtomicPositions(QEAtomicPositions atomicPositions) {
        this.atomicPositions = atomicPositions;

        if (this.atomicPositions != null) {
            this.updateQEProperties();
            this.atomicPositions.addListener(this);
        }
    }

    @Override
    public void onCardChanged(QECardEvent event) {
        if (event == null) {
            return;
        }

        int eventType = event.getEventType();
        if (eventType == QECardEvent.EVENT_TYPE_ATOM_MOVED || eventType == QECardEvent.EVENT_TYPE_UNIT_CHANGED) {
            return;
        }

        if (this.atomicPositions != null) {
            this.updateQEProperties();
        }
    }

    private void updateQEProperties() {
        int numSpecs = 0;
        int numAtoms = 0;
        String[] arraySpecs = null;
        Set<String> setSpecs = null;

        numAtoms = this.atomicPositions.numPositions();
        setSpecs = new LinkedHashSet<String>();
        for (int i = 0; i < numAtoms; i++) {
            String label = this.atomicPositions.getLabel(i);
            if (label != null) {
                label = label.trim();
            }
            if (!label.isEmpty()) {
                setSpecs.add(label);
            }
        }

        numSpecs = setSpecs.size();
        arraySpecs = setSpecs.toArray(new String[numSpecs]);

        this.nmlSystem.setValue(new QEInteger("ntyp", numSpecs));
        this.nmlSystem.setValue(new QEInteger("nat", numAtoms));

        int numSpecsOld = this.atomicSpecies.numSpecies();
        for (int i = numSpecsOld - 1; i > -1; i--) {
            String label = this.atomicSpecies.getLabel(i);
            if (!setSpecs.contains(label)) {
                this.updateTraceMap(i);
                this.atomicSpecies.removeSpecies(i);
            }
        }

        for (int i = 0; i < arraySpecs.length; i++) {
            String label = arraySpecs[i];
            if (!this.atomicSpecies.hasSpecies(label)) {
                double mass = this.getMass(label);
                String pseudo = this.getPseudo(label);
                this.atomicSpecies.addSpecies(label, mass, pseudo);
            }
        }

        if (numSpecs != this.atomicSpecies.numSpecies()) {
            throw new RuntimeException("numSpecs != this.atomicSpecies.getNumSpecies()");
        }
    }

    private void updateTraceMap(int index) {
        if (this.traceMapMass == null) {
            this.traceMapMass = new HashMap<String, Double>();
        }

        if (this.traceMapPseudo == null) {
            this.traceMapPseudo = new HashMap<String, String>();
        }

        String label = this.atomicSpecies.getLabel(index);
        double mass = this.atomicSpecies.getMass(index);
        String pseudo = this.atomicSpecies.getPseudoName(index);

        this.traceMapMass.put(label, mass);
        this.traceMapPseudo.put(label, pseudo);
    }

    private double getMass(String label) {
        if (this.traceMapMass != null && this.traceMapMass.containsKey(label)) {
            return this.traceMapMass.get(label);
        }

        return ElementUtil.getMass(label);
    }

    private String getPseudo(String label) {
        if (this.traceMapPseudo != null && this.traceMapPseudo.containsKey(label)) {
            return this.traceMapPseudo.get(label);
        }

        String element = label == null ? null : ElementUtil.toElementName(label);
        PseudoPotential pseudoPot = element == null ? null : PseudoLibrary.getInstance().getPseudoPotential(element);
        return pseudoPot == null ? null : pseudoPot.getName();
    }
}
