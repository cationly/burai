/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import burai.app.QEFXAppController;
import burai.app.QEFXMainController;
import burai.app.project.ProjectAction;
import burai.app.project.QEFXProjectController;
import burai.app.project.editor.EditorActions;
import burai.app.project.editor.QEFXEditorComponent;
import burai.app.project.editor.input.band.QEFXBand;
import burai.app.project.editor.input.dos.QEFXDos;
import burai.app.project.editor.input.geom.QEFXGeom;
import burai.app.project.editor.input.md.QEFXMd;
import burai.app.project.editor.input.neb.QEFXNeb;
import burai.app.project.editor.input.opt.QEFXOpt;
import burai.app.project.editor.input.phonon.QEFXPhonon;
import burai.app.project.editor.input.scf.QEFXScf;
import burai.app.project.editor.input.tddft.QEFXTddft;
import burai.atoms.model.Cell;
import burai.input.QEInput;
import burai.project.Project;

public class InputEditorActions extends EditorActions {

    private static final String[] EDITOR_ITEMS = {
            "Geometry", "SCF", "Optimize", "MD", "DOS", "Band", "NEB", "TD-DFT", "Phonon" };

    private static final int[] EDITOR_INPUT_MODE = {
            Project.INPUT_MODE_GEOMETRY,
            Project.INPUT_MODE_SCF,
            Project.INPUT_MODE_OPTIMIZ,
            Project.INPUT_MODE_MD,
            Project.INPUT_MODE_DOS,
            Project.INPUT_MODE_BAND,
            Project.INPUT_MODE_NEB,
            Project.INPUT_MODE_TDDFT,
            Project.INPUT_MODE_PHONON
    };

    private Map<String, QEFXEditorComponent<? extends QEFXAppController>> components;

    public InputEditorActions(Project project, QEFXProjectController controller) throws IOException {
        super(project, controller);

        this.createComponents();
        this.setupActions();
    }

    @Override
    public void actionInitially() {
        if (this.controller == null) {
            return;
        }

        this.controller.addEditorMenuItems(EDITOR_ITEMS);

        String item = EDITOR_ITEMS[0];
        ProjectAction action = this.actions.get(item);
        if (action != null) {
            action.actionOnProject(this.controller);
        }
    }

    private void createComponents() throws IOException {
        this.components = new HashMap<String, QEFXEditorComponent<? extends QEFXAppController>>();

        QEFXMainController mainController = null;
        if (this.controller != null) {
            mainController = this.controller.getMainController();
        }

        if (mainController != null) {
            Cell cell = null;
            QEInput input = null;

            cell = this.project == null ? null : this.project.getCell();
            input = this.project == null ? null : this.project.getQEInputGeometry();
            if (input != null && cell != null) {
                this.components.put(EDITOR_ITEMS[0], new QEFXGeom(mainController, input, cell));
            }

            input = this.project == null ? null : this.project.getQEInputScf();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[1], new QEFXScf(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputOptimiz();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[2], new QEFXOpt(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputMd();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[3], new QEFXMd(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputDos();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[4], new QEFXDos(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputBand();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[5], new QEFXBand(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputGeometry();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[6], new QEFXNeb(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputGeometry();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[7], new QEFXTddft(mainController, input));
            }

            input = this.project == null ? null : this.project.getQEInputGeometry();
            if (input != null) {
                this.components.put(EDITOR_ITEMS[8], new QEFXPhonon(mainController, input));
            }
        }
    }

    private void setupActions() {
        if (EDITOR_ITEMS.length != EDITOR_INPUT_MODE.length) {
            throw new RuntimeException("EDITOR_ITEMS.length != EDITOR_INPUT_MODE.length.");
        }

        for (int i = 0; i < EDITOR_ITEMS.length; i++) {
            String item = EDITOR_ITEMS[i];
            int inputMode = EDITOR_INPUT_MODE[i];

            QEFXEditorComponent<? extends QEFXAppController> component = this.components.get(item);
            if (component == null) {
                continue;
            }

            this.actions.put(item, controller2 -> {
                if (controller2 == null) {
                    return;
                }

                controller2.setEditorText(item);
                controller2.setEditorPane(component.getNode());
                component.notifyEditorOpened();

                if (this.project != null) {
                    this.project.resolveQEInputs();
                    this.project.setInputMode(inputMode);
                }
            });
        }
    }
}
