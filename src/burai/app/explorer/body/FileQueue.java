/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body;

public abstract class FileQueue {

    private FileElementDeleted onFileElementDeleted;

    protected FileQueue() {
        this.onFileElementDeleted = null;
    }

    public abstract FileElement pollFileElement();

    public abstract FileElement peekFileElement();

    public abstract void addFileElement(FileElement fileElement);

    public abstract boolean hasFileElements();

    public abstract void stopFileElements();

    public void setOnFileElementDeleted(FileElementDeleted onFileElementDeleted) {
        this.onFileElementDeleted = onFileElementDeleted;
    }

    protected void actionOnFileElementDeleted(FileElement fileElement) {
        if (this.onFileElementDeleted != null) {
            this.onFileElementDeleted.onFileElementDeleted(fileElement);
        }
    }
}
