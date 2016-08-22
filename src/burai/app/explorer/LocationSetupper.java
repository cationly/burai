/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer;

import java.io.File;
import java.util.Deque;
import java.util.LinkedList;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import burai.app.explorer.body.QEFXExplorerBody;
import burai.com.env.Environments;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class LocationSetupper extends ExplorerSetupper {

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "piclight-button";

    private Deque<String> locationQueue;
    private Deque<String> locationQueueSub;

    protected LocationSetupper(QEFXExplorerController controller) {
        super(controller);
        this.locationQueue = new LinkedList<String>();
        this.locationQueueSub = new LinkedList<String>();
    }

    protected String getPreviousLocation() {
        if (this.locationQueue.isEmpty()) {
            return null;
        }

        return this.locationQueue.peek();
    }

    protected void storeLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return;
        }

        this.locationQueue.push(location);
    }

    protected void setupLocationField(TextField locationField) {
        File[] files = Environments.listRecentFiles();
        if (files == null || files.length < 1) {
            String path = Environments.getProjectsPath();
            if (path != null && (!path.isEmpty())) {
                locationField.setText(path);
            }
        } else {
            locationField.setText(QEFXExplorerBody.CODE_RECENTLY_USED);
        }

        locationField.setOnAction(event -> this.controller.updateExplorerBody());
    }

    protected void clearRedoLocations() {
        this.locationQueueSub.clear();
    }

    protected void setupBackwardButton(Button backwardButton) {
        if (backwardButton == null) {
            return;
        }

        backwardButton.setText("");

        backwardButton.setTooltip(new Tooltip("backward"));

        backwardButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.ARROW_LEFT, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        backwardButton.setOnAction(event -> {
            if (this.locationQueue.size() < 2) {
                return;
            }

            this.locationQueueSub.push(this.locationQueue.poll());
            this.controller.getLocationField().setText(this.locationQueue.poll());
            this.controller.updateExplorerBody(true);
        });
    }

    protected void setupForwardButton(Button forwardButton) {
        if (forwardButton == null) {
            return;
        }

        forwardButton.setText("");

        forwardButton.setTooltip(new Tooltip("forward"));

        forwardButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.ARROW_RIGHT, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        forwardButton.setOnAction(event -> {
            if (this.locationQueueSub.isEmpty()) {
                return;
            }

            this.controller.getLocationField().setText(this.locationQueueSub.poll());
            this.controller.updateExplorerBody(true);
        });
    }

    protected void setupUpwardButton(Button upwardButton) {
        if (upwardButton == null) {
            return;
        }

        upwardButton.setText("");

        upwardButton.setTooltip(new Tooltip("upward"));

        upwardButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.ARROW_UP, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        upwardButton.setOnAction(event -> {
            String location = this.controller.getLocationField().getText();
            if (location == null || location.trim().isEmpty()) {
                return;
            }

            File locationFile = new File(location.trim());
            File parentFile = locationFile.getParentFile();
            if (parentFile == null || (!parentFile.exists()) || (!parentFile.isDirectory())) {
                return;
            }

            this.controller.getLocationField().setText(parentFile.getPath());
            this.controller.updateExplorerBody();
        });
    }
}
