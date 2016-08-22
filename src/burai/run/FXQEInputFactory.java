/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.run;

import javafx.application.Platform;
import burai.input.QEInput;
import burai.project.Project;

public class FXQEInputFactory {

    private RunningType type;

    private QEInput qeInput;

    private boolean hasQEInput;

    protected FXQEInputFactory(RunningType type) {
        if (type == null) {
            throw new IllegalArgumentException("type is null.");
        }

        this.type = type;
        this.qeInput = null;
        this.hasQEInput = false;
    }

    protected QEInput getQEInput(Project project) {
        if (project == null) {
            return null;
        }

        this.hasQEInput = false;

        Platform.runLater(() -> {
            project.resolveQEInputs();
            this.qeInput = this.type.getQEInput(project);

            synchronized (this) {
                this.hasQEInput = true;
                this.notifyAll();
            }
        });

        synchronized (this) {
            while (!this.hasQEInput) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        this.hasQEInput = false;

        return this.qeInput;
    }
}
