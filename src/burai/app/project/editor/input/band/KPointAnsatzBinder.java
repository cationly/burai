/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.band;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javafx.scene.control.TableView;
import burai.input.card.QECardEvent;
import burai.input.card.QEKPoint;
import burai.input.card.QEKPoints;

public class KPointAnsatzBinder {

    private static final double DELTA_WEIGHT = 0.5;

    private TableView<KPointAnsatz> kpointTable;

    private QEKPoints kpointsCard;

    private boolean busyKxyz;

    public KPointAnsatzBinder(TableView<KPointAnsatz> kpointTable, QEKPoints kpointsCard) {
        if (kpointTable == null) {
            throw new IllegalArgumentException("kpointTable is null.");
        }

        if (kpointsCard == null) {
            throw new IllegalArgumentException("kpointsCard is null.");
        }

        this.kpointTable = kpointTable;
        this.kpointsCard = kpointsCard;
        this.busyKxyz = false;
    }

    public void bindTable() {
        this.setupKPointTable();
        this.setupKPointsCard();
    }

    private void setupKPointTable() {
        int numKPoints = this.kpointsCard.numKPoints();
        for (int i = 0; i < numKPoints; i++) {
            KPointAnsatz kpointAnsatz = this.createKPointAnsatz(i);
            if (kpointAnsatz != null) {
                this.kpointTable.getItems().add(kpointAnsatz);
            }
        }
    }

    private KPointAnsatz createKPointAnsatz(int index) {
        if (index < 0 || this.kpointsCard.numKPoints() <= index) {
            return null;
        }

        QEKPoint kpoint = this.kpointsCard.getKPoint(index);
        if (kpoint == null) {
            return null;
        }

        String symbol = kpoint.hasLetter() ? kpoint.getLetter() : null;
        double kx = kpoint.getX();
        double ky = kpoint.getY();
        double kz = kpoint.getZ();
        int nk = (int) (kpoint.getWeight() + DELTA_WEIGHT);

        KPointAnsatz kpointAnsatz = new KPointAnsatz(index);

        if (symbol == null || symbol.isEmpty()) {
            kpointAnsatz.setSymbol("");
            kpointAnsatz.setKx(kx);
            kpointAnsatz.setKy(ky);
            kpointAnsatz.setKz(kz);
        } else {
            kpointAnsatz.setSymbol(symbol);
            kpointAnsatz.setKx("");
            kpointAnsatz.setKy("");
            kpointAnsatz.setKz("");
        }
        kpointAnsatz.setNk(nk);

        kpointAnsatz.symbolProperty().addListener(o -> this.actionOnKPointAnsatzChanged(kpointAnsatz));
        kpointAnsatz.kxProperty().addListener(o -> this.actionOnKPointAnsatzChanged(kpointAnsatz));
        kpointAnsatz.kyProperty().addListener(o -> this.actionOnKPointAnsatzChanged(kpointAnsatz));
        kpointAnsatz.kzProperty().addListener(o -> this.actionOnKPointAnsatzChanged(kpointAnsatz));
        kpointAnsatz.nkProperty().addListener(o -> this.actionOnKPointAnsatzChanged(kpointAnsatz));

        return kpointAnsatz;
    }

    private void actionOnKPointAnsatzChanged(KPointAnsatz kpointAnsatz) {
        if (this.busyKxyz) {
            return;
        }

        if (kpointAnsatz == null) {
            return;
        }

        int index = kpointAnsatz.getIndex();
        if (index < 0 || this.kpointsCard.numKPoints() <= index) {
            return;
        }

        QEKPoint kpoint = this.kpointsCard.getKPoint(index);
        kpoint = this.createQEKPoint(kpointAnsatz, kpoint);
        this.kpointsCard.setKPoint(index, kpoint);
    }

    private QEKPoint createQEKPoint(KPointAnsatz kpointAnsatz, QEKPoint kpoint) {
        if (kpointAnsatz == null) {
            return null;
        }

        QEKPoint kpoint2 = kpoint;
        if (kpoint2 == null) {
            kpoint2 = new QEKPoint(0.0, 0.0, 0.0, 1);
        }

        String symbol = kpointAnsatz.getSymbol();
        symbol = symbol == null ? null : symbol.trim();

        boolean correctKxyz = false;
        double kx = kpoint2.getX();
        double ky = kpoint2.getY();
        double kz = kpoint2.getZ();
        int nk = (int) (kpoint2.getWeight() + DELTA_WEIGHT);

        try {
            kx = kpointAnsatz.getKxValue();
            correctKxyz = true;
        } catch (RuntimeException e) {
            // NOP
        }

        try {
            ky = kpointAnsatz.getKyValue();
            correctKxyz = true;
        } catch (RuntimeException e) {
            // NOP
        }

        try {
            kz = kpointAnsatz.getKzValue();
            correctKxyz = true;
        } catch (RuntimeException e) {
            // NOP
        }

        try {
            nk = kpointAnsatz.getNkValue();
        } catch (RuntimeException e) {
            // NOP
        }

        if (correctKxyz) {
            return new QEKPoint(kx, ky, kz, (double) nk);
        }

        if (symbol != null && (!symbol.isEmpty())) {
            return new QEKPoint(symbol, (double) nk);
        } else {
            return new QEKPoint(kx, ky, kz, (double) nk);
        }
    }

    private void setupKPointsCard() {
        this.kpointsCard.addListener(event -> {
            if (this.busyKxyz) {
                return;
            }

            if (event == null) {
                return;
            }

            int eventType = event.getEventType();
            int index = event.getKPointIndex();

            if (eventType == QECardEvent.EVENT_TYPE_KPOINT_CHANGED) {
                this.actionOnKPointChanged(index);

            } else if (eventType == QECardEvent.EVENT_TYPE_KPOINT_ADDED) {
                this.actionOnKPointAdded(index);

            } else if (eventType == QECardEvent.EVENT_TYPE_KPOINT_REMOVED) {
                this.actionOnKPointRemoved(index);

            } else if (eventType == QECardEvent.EVENT_TYPE_KPOINT_CLEARED) {
                this.actionOnKPointCleared();

            } else {
                this.actionForAllKPoints();
            }
        });
    }

    private KPointAnsatz pickOutKPointAnsatz(int index) {
        List<KPointAnsatz> kpointAnsatzList = this.kpointTable.getItems();
        if (kpointAnsatzList == null) {
            return null;
        }

        for (KPointAnsatz kpointAnsatz : kpointAnsatzList) {
            if (kpointAnsatz == null) {
                continue;
            }
            if (index == kpointAnsatz.getIndex()) {
                return kpointAnsatz;
            }
        }

        return null;
    }

    private void actionOnKPointChanged(int index) {
        QEKPoint kpoint = this.kpointsCard.getKPoint(index);
        if (kpoint == null) {
            return;
        }

        KPointAnsatz kpointAnsatz = this.pickOutKPointAnsatz(index);
        if (kpointAnsatz == null) {
            return;
        }

        String symbol = kpoint.hasLetter() ? kpoint.getLetter() : null;
        if (symbol == null || symbol.isEmpty()) {
            this.busyKxyz = true;
            kpointAnsatz.setSymbol("");
            kpointAnsatz.setKx(kpoint.getX());
            kpointAnsatz.setKy(kpoint.getY());
            kpointAnsatz.setKz(kpoint.getZ());
            this.busyKxyz = false;
        } else {
            this.busyKxyz = true;
            kpointAnsatz.setSymbol(symbol);
            kpointAnsatz.setKx("");
            kpointAnsatz.setKy("");
            kpointAnsatz.setKz("");
            this.busyKxyz = false;
        }
        kpointAnsatz.setNk((int) (kpoint.getWeight() + DELTA_WEIGHT));
    }

    private void actionOnKPointAdded(int index) {
        KPointAnsatz kpointAnsatz = this.createKPointAnsatz(index);
        if (kpointAnsatz != null) {
            this.kpointTable.getItems().add(kpointAnsatz);
        }
    }

    private void actionOnKPointRemoved(int index) {
        KPointAnsatz kpointRemoved = this.pickOutKPointAnsatz(index);
        if (kpointRemoved == null) {
            return;
        }

        this.kpointTable.getItems().remove(kpointRemoved);

        for (KPointAnsatz kpointAnsatz : this.kpointTable.getItems()) {
            if (kpointAnsatz.getIndex() >= index) {
                kpointAnsatz.setIndex(kpointAnsatz.getIndex() - 1);
            }
        }
    }

    private void actionOnKPointCleared() {
        this.kpointTable.getItems().clear();
    }

    private void actionForAllKPoints() {
        this.kpointTable.getItems().clear();
        this.setupKPointTable();
    }

    public void addKPoint(KPointAnsatz kpointAnsatz) {
        if (kpointAnsatz == null) {
            return;
        }

        QEKPoint kpoint = this.createQEKPoint(kpointAnsatz, null);
        if (kpoint != null) {
            this.kpointsCard.addKPoint(kpoint);
        }
    }

    public void addKPoint(KPointAnsatz kpointAnsatz, int index) {
        if (kpointAnsatz == null) {
            return;
        }

        int insert = Math.max(0, index + 1);

        int numKPoints = this.kpointsCard.numKPoints();
        if (insert >= numKPoints) {
            this.addKPoint(kpointAnsatz);
            return;
        }

        Deque<QEKPoint> kpointTails = new LinkedList<QEKPoint>();
        for (int i = (numKPoints - 1); i >= insert; i--) {
            kpointTails.push(this.kpointsCard.getKPoint(i));
            this.kpointsCard.removeKPoint(i);
        }

        QEKPoint kpointNew = this.createQEKPoint(kpointAnsatz, null);
        if (kpointNew != null) {
            this.kpointsCard.addKPoint(kpointNew);
        }

        while (!kpointTails.isEmpty()) {
            QEKPoint kpointTail = kpointTails.pop();
            this.kpointsCard.addKPoint(kpointTail);
        }
    }

    public void removeKPoint(KPointAnsatz kpointAnsatz) {
        if (kpointAnsatz == null) {
            return;
        }

        this.kpointsCard.removeKPoint(kpointAnsatz.getIndex());
    }

    public void swapKPoints(KPointAnsatz kpointAnsatz1, KPointAnsatz kpointAnsatz2) {
        if (kpointAnsatz1 == null || kpointAnsatz2 == null) {
            return;
        }

        int index1 = kpointAnsatz1.getIndex();
        int index2 = kpointAnsatz2.getIndex();
        QEKPoint kpoint1 = this.kpointsCard.getKPoint(index1);
        QEKPoint kpoint2 = this.kpointsCard.getKPoint(index2);
        this.kpointsCard.setKPoint(index1, kpoint2);
        this.kpointsCard.setKPoint(index2, kpoint1);
    }

    public void setKPointSymbol(KPointAnsatz kpointAnsatz, String symbol) {
        if (kpointAnsatz == null) {
            return;
        }

        if (symbol == null || symbol.isEmpty()) {
            return;
        }

        this.busyKxyz = true;
        kpointAnsatz.setSymbol(symbol);
        kpointAnsatz.setKx("");
        kpointAnsatz.setKy("");
        kpointAnsatz.setKz("");
        this.busyKxyz = false;
    }
}
