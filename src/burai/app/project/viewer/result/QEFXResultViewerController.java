/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result;

import burai.app.QEFXAppController;
import burai.app.project.QEFXProjectController;

public abstract class QEFXResultViewerController extends QEFXAppController {

    private static final long INTER_LOADING_TIME = 2500L;

    private boolean loading;
    private Object loadingLock;

    protected QEFXProjectController projectController;

    public QEFXResultViewerController(QEFXProjectController projectController) {
        super(projectController == null ? null : projectController.getMainController());

        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        this.loading = false;
        this.loadingLock = new Object();

        this.projectController = projectController;
    }

    public abstract void reload();

    public void reloadSafely() {
        synchronized (this.loadingLock) {
            if (this.loading) {
                return;
            }

            this.loading = true;
        }

        this.reload();

        Thread thread = new Thread(() -> {
            synchronized (this.loadingLock) {
                try {
                    this.loadingLock.wait(INTER_LOADING_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                this.loading = false;
            }
        });

        thread.start();
    }
}
