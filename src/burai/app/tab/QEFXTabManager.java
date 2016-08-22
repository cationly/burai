/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.tab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebEngine;
import burai.app.QEFXMainController;
import burai.app.project.QEFXProject;
import burai.app.web.QEFXWeb;
import burai.project.Project;
import burai.pseudo.PseudoLibrary;

public class QEFXTabManager {

    private QEFXMainController controller;

    private TabPane tabPane;

    public QEFXTabManager(QEFXMainController controller, TabPane tabPane) {
        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        if (tabPane == null) {
            throw new IllegalArgumentException("tabPane is null.");
        }

        this.controller = controller;
        this.tabPane = tabPane;
        this.setupTabPane();
    }

    private void setupTabPane() {
        this.tabPane.setOnKeyPressed(event -> {
            if (event == null) {
                return;
            }

            if (event.isControlDown() && KeyCode.W.equals(event.getCode())) {
                // Ctrl + W
                SingleSelectionModel<Tab> selectionModel = this.tabPane.getSelectionModel();
                if (selectionModel != null) {
                    Tab tab = null;
                    int index = selectionModel.getSelectedIndex();
                    if (index > 0) {
                        tab = this.tabPane.getTabs().get(index);
                    }

                    Event event2 = new Event(Event.ANY);

                    if (tab != null) {
                        if (!event2.isConsumed()) {
                            if (tab.getOnCloseRequest() != null) {
                                tab.getOnCloseRequest().handle(event2);
                            }
                        }
                    }

                    tab = null;
                    if (!event2.isConsumed()) {
                        if (index > 0) {
                            tab = this.tabPane.getTabs().remove(index);
                        }
                    }

                    if (tab != null) {
                        if (!event2.isConsumed()) {
                            if (tab.getOnClosed() != null) {
                                tab.getOnClosed().handle(event2);
                            }
                        }
                    }
                }
            }
        });
    }

    public boolean showHomeTab() {
        SingleSelectionModel<Tab> selectionModel = this.tabPane.getSelectionModel();
        if (selectionModel != null) {
            selectionModel.select(0);
            this.tabPane.requestFocus();
            return true;
        }

        return false;
    }

    public Project showTab(Project project) {
        if (project == null) {
            return null;
        }

        QEFXProjectTab projectTab = new QEFXProjectTab(project);
        int index = this.tabPane.getTabs().indexOf(projectTab);

        if (index > -1) {
            Tab tab = this.tabPane.getTabs().get(index);
            if (tab instanceof QEFXProjectTab) {
                projectTab = (QEFXProjectTab) tab;
            }

        } else {
            PseudoLibrary.getInstance().waitToLoad();
            project.resolveQEInputs();
            project.markQEInputs();

            QEFXProject qefxProject = null;
            try {
                qefxProject = new QEFXProject(this.controller, project);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            projectTab.setContent(qefxProject.getNode());
            projectTab.setProjectController(qefxProject.getController());
            projectTab.setOnClosed(event -> this.controller.refreshProjectOnExplorer(project));
            this.tabPane.getTabs().add(projectTab);
        }

        SingleSelectionModel<Tab> selectionModel = this.tabPane.getSelectionModel();
        if (selectionModel != null) {
            selectionModel.select(projectTab);
            this.tabPane.requestFocus();
            return project;
        }

        return null;
    }

    public boolean hideTab(Project project) {
        if (project == null) {
            return false;
        }

        Tab tab = new QEFXProjectTab(project);
        int index = this.tabPane.getTabs().indexOf(tab);

        if (index < 0) {
            return false;
        }

        tab = this.tabPane.getTabs().remove(index);
        if (tab != null) {
            Event event = new Event(Event.ANY);
            if (!event.isConsumed()) {
                if (tab.getOnClosed() != null) {
                    tab.getOnClosed().handle(event);
                }
            }
            return true;
        }

        return false;
    }

    public List<Project> getProjects() {
        List<Project> projects = new ArrayList<Project>();

        List<Tab> tabs = this.tabPane.getTabs();
        for (Tab tab : tabs) {
            if (tab != null && (tab instanceof QEFXProjectTab)) {
                Project project = ((QEFXProjectTab) tab).getBody();
                if (project != null) {
                    projects.add(project);
                }
            }
        }

        return projects;
    }

    public WebEngine showTab(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        QEFXWeb qefxWeb = null;
        try {
            qefxWeb = new QEFXWeb(this.controller, url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        WebEngine engine = qefxWeb.getEngine();
        if (engine == null) {
            return null;
        }

        Tab tab = new QEFXWebTab(engine);
        int index = this.tabPane.getTabs().indexOf(tab);

        if (index > -1) {
            tab = this.tabPane.getTabs().get(index);

        } else {
            tab.setContent(qefxWeb.getNode());
            this.tabPane.getTabs().add(tab);
        }

        SingleSelectionModel<Tab> selectionModel = this.tabPane.getSelectionModel();
        if (selectionModel != null) {
            selectionModel.select(tab);
            this.tabPane.requestFocus();
            return engine;
        }

        return null;
    }

    public boolean hideTab(WebEngine engine) {
        if (engine == null) {
            return false;
        }

        QEFXWebTab webTab = new QEFXWebTab(engine);
        List<Tab> tabs = this.tabPane.getTabs();

        int numTabs = tabs == null ? 0 : tabs.size();
        if (numTabs < 1) {
            return false;
        }

        for (int i = 0; i < numTabs; i++) {
            Tab tab = tabs.get(i);
            if (webTab.equalsEngine(tab)) {
                tabs.remove(i);
                Event event = new Event(Event.ANY);
                if (!event.isConsumed()) {
                    if (tab.getOnClosed() != null) {
                        tab.getOnClosed().handle(event);
                    }
                }
                return true;
            }
        }

        return false;
    }
}
