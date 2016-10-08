/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.TilePane;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.band.QEFXBandButton;
import burai.app.project.viewer.result.graph.EnergyType;
import burai.app.project.viewer.result.graph.LatticeViewerType;
import burai.app.project.viewer.result.graph.QEFXDosButton;
import burai.app.project.viewer.result.graph.QEFXMdEnergyButton;
import burai.app.project.viewer.result.graph.QEFXMdLatticeButton;
import burai.app.project.viewer.result.graph.QEFXOptEnergyButton;
import burai.app.project.viewer.result.graph.QEFXOptForceButton;
import burai.app.project.viewer.result.graph.QEFXOptLatticeButton;
import burai.app.project.viewer.result.graph.QEFXOptStressButton;
import burai.app.project.viewer.result.graph.QEFXScfButton;
import burai.app.project.viewer.result.log.QEFXCrashButton;
import burai.app.project.viewer.result.log.QEFXErrorButton;
import burai.app.project.viewer.result.log.QEFXInputButton;
import burai.app.project.viewer.result.log.QEFXOutputButton;
import burai.project.Project;
import burai.run.RunningManager;

public class QEFXResultExplorer {

    private static final double PANE_HEIGHT = 100.0;
    private static final double PANE_WIDTH = 100.0;

    private static final String SCROLL_CLASS = "result-expr-scroll";
    private static final String TILE_CLASS = "result-expr-tile";

    private static final long AUTORELOADING_TIME = 2000L;

    private Project project;

    private QEFXProjectController projectController;

    private List<QEFXResultButton<?, ?>> buttonList;
    private Map<String, QEFXResultButton<?, ?>> buttonMap;

    private boolean autoReloading;

    private ScrollPane scrollPane;

    private TilePane tilePane;

    public QEFXResultExplorer(QEFXProjectController projectController, Project project) {
        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        this.projectController = projectController;
        this.project = project;

        this.buttonList = null;
        this.buttonMap = null;

        this.autoReloading = false;

        this.createScrollPane();
        this.createTilePane();

        this.reload();
    }

    public Node getNode() {
        return this.scrollPane;
    }

    private void createScrollPane() {
        this.scrollPane = new ScrollPane();
        this.scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setFitToHeight(true);
        this.scrollPane.setFitToWidth(true);
        this.scrollPane.setPrefHeight(PANE_HEIGHT);
        this.scrollPane.setPrefWidth(PANE_WIDTH);
        this.scrollPane.setPannable(false);
        this.scrollPane.getStyleClass().add(SCROLL_CLASS);
    }

    private void createTilePane() {
        this.tilePane = new TilePane();
        this.tilePane.getStyleClass().add(TILE_CLASS);
        this.scrollPane.setContent(this.tilePane);
    }

    public void reload() {
        if (this.buttonList != null) {
            this.buttonList.clear();
        }

        this.updateLogButtons();
        this.updateScfButtons();
        this.updateOptButtons();
        this.updateMdButtons();
        this.updateDosButtons();
        this.updateBandButtons();

        int numNode1 = this.buttonList == null ? 0 : this.buttonList.size();
        int numNode2 = this.tilePane.getChildren().size();
        boolean changed = (numNode1 != numNode2);

        if (!changed) {
            for (int i = 0; i < numNode1; i++) {
                QEFXResultButton<?, ?> button = this.buttonList.get(i);
                Node node1 = button == null ? null : button.getNode();
                Node node2 = this.tilePane.getChildren().get(i);
                if (node1 != node2) {
                    changed = true;
                    break;
                }
            }
        }

        if (changed && this.buttonList != null) {
            this.tilePane.getChildren().clear();
            for (QEFXResultButton<?, ?> button : this.buttonList) {
                Node node = button == null ? null : button.getNode();
                if (node != null) {
                    this.tilePane.getChildren().add(node);
                }
            }
        }

        synchronized (this) {
            if (!this.autoReloading) {
                this.autoReloading = true;
                this.autoReload();
            }
        }
    }

    private void autoReload() {
        Thread thread = new Thread(() -> {
            synchronized (this) {
                while (RunningManager.getInstance().getNode(this.project) != null) {
                    try {
                        this.wait(AUTORELOADING_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Platform.runLater(() -> this.reload());
                }

                this.autoReloading = false;
            }
        });

        thread.start();
    }

    private void updateLogButtons() {
        this.updateButton("QEFXCrashButton", () -> {
            return QEFXCrashButton.getWrapper(this.projectController, this.project);
        });

        this.updateButton("QEFXInputButton", () -> {
            return QEFXInputButton.getWrapper(this.projectController, this.project);
        });

        int numOutput = 0;
        for (int i = 0; true; i++) {
            final int i_ = i;
            boolean buttonShown = this.updateButton("QEFXOutputButton#" + i, () -> {
                return QEFXOutputButton.getWrapper(this.projectController, this.project, i_);
            });

            if (buttonShown) {
                numOutput++;
            } else {
                break;
            }
        }

        for (int i = 0; i < numOutput; i++) {
            final int i_ = i;
            this.updateButton("QEFXErrorButton#" + i, () -> {
                return QEFXErrorButton.getWrapper(this.projectController, this.project, i_);
            });
        }
    }

    private void updateScfButtons() {
        this.updateButton("QEFXScfButton", () -> {
            return QEFXScfButton.getWrapper(this.projectController, this.project);
        });
    }

    private void updateOptButtons() {
        this.updateButton("QEFXOptEnergyButton", () -> {
            return QEFXOptEnergyButton.getWrapper(this.projectController, this.project);
        });

        this.updateButton("QEFXOptForceButton", () -> {
            return QEFXOptForceButton.getWrapper(this.projectController, this.project);
        });

        this.updateButton("QEFXOptStressButton", () -> {
            return QEFXOptStressButton.getWrapper(this.projectController, this.project);
        });

        this.updateButton("QEFXOptLatticeButton#A", () -> {
            return QEFXOptLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.A);
        });

        this.updateButton("QEFXOptLatticeButton#B", () -> {
            return QEFXOptLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.B);
        });

        this.updateButton("QEFXOptLatticeButton#C", () -> {
            return QEFXOptLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.C);
        });

        this.updateButton("QEFXOptLatticeButton#ANGLE", () -> {
            return QEFXOptLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.ANGLE);
        });

        //this.updateButton("QEFXOptMovieButton", () -> {
        //    return QEFXOptMovieButton.getWrapper(this.projectController, this.project);
        //});
    }

    private void updateMdButtons() {
        this.updateButton("QEFXMdEnergyButton#TOTAL", () -> {
            return QEFXMdEnergyButton.getWrapper(this.projectController, this.project, EnergyType.TOTAL);
        });

        this.updateButton("QEFXMdEnergyButton#KINETIC", () -> {
            return QEFXMdEnergyButton.getWrapper(this.projectController, this.project, EnergyType.KINETIC);
        });

        this.updateButton("QEFXMdEnergyButton#CONSTANT", () -> {
            return QEFXMdEnergyButton.getWrapper(this.projectController, this.project, EnergyType.CONSTANT);
        });

        this.updateButton("QEFXMdEnergyButton#TEMPERATURE", () -> {
            return QEFXMdEnergyButton.getWrapper(this.projectController, this.project, EnergyType.TEMPERATURE);
        });

        this.updateButton("QEFXMdLatticeButton#A", () -> {
            return QEFXMdLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.A);
        });

        this.updateButton("QEFXMdLatticeButton#B", () -> {
            return QEFXMdLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.B);
        });

        this.updateButton("QEFXMdLatticeButton#C", () -> {
            return QEFXMdLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.C);
        });

        this.updateButton("QEFXMdLatticeButton#ANGLE", () -> {
            return QEFXMdLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.ANGLE);
        });

        //this.updateButton("QEFXMdMovieButton", () -> {
        //    return QEFXMdMovieButton.getWrapper(this.projectController, this.project);
        //});
    }

    private void updateDosButtons() {
        this.updateButton("QEFXDosButton", () -> {
            return QEFXDosButton.getWrapper(this.projectController, this.project);
        });
    }

    private void updateBandButtons() {
        this.updateButton("QEFXBandButton", () -> {
            return QEFXBandButton.getWrapper(this.projectController, this.project);
        });
    }

    private <T extends QEFXResultButton<?, ?>> boolean updateButton(String key, ButtonGetter<T> buttonGetter) {
        if (key == null) {
            return false;
        }
        if (buttonGetter == null) {
            return false;
        }

        QEFXResultButton<?, ?> button = null;
        QEFXResultButtonWrapper<T> wrapper = buttonGetter.getWrapper();
        if (wrapper != null) {
            if (this.buttonMap != null && this.buttonMap.containsKey(key)) {
                button = this.buttonMap.get(key);
            } else {
                button = wrapper.getInstance();
            }
        }

        if (button != null) {
            if (this.buttonMap == null) {
                this.buttonMap = new HashMap<String, QEFXResultButton<?, ?>>();
            }
            this.buttonMap.put(key, button);

            if (this.buttonList == null) {
                this.buttonList = new ArrayList<QEFXResultButton<?, ?>>();
            }

            this.buttonList.add(button);
            return true;
        }

        return false;
    }

    @FunctionalInterface
    private static interface ButtonGetter<T extends QEFXResultButton<?, ?>> {

        public abstract QEFXResultButtonWrapper<T> getWrapper();

    }
}
