/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.key;

import javafx.scene.input.KeyCode;

public class KeyPressedAnsatz {

    private KeyCode keyCode;
    private boolean ctrlStatus;
    private boolean shiftStatus;
    private boolean altStatus;

    public KeyPressedAnsatz(KeyCode keyCode) {
        this(keyCode, false, false, false);
    }

    public KeyPressedAnsatz(KeyCode keyCode, boolean ctrlStatus, boolean shiftStatus, boolean altStatus) {
        this.keyCode = keyCode;
        this.ctrlStatus = ctrlStatus;
        this.shiftStatus = shiftStatus;
        this.altStatus = altStatus;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.altStatus ? 1231 : 1237);
        result = prime * result + (this.ctrlStatus ? 1231 : 1237);
        result = prime * result + ((this.keyCode == null) ? 0 : this.keyCode.hashCode());
        result = prime * result + (this.shiftStatus ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        KeyPressedAnsatz other = (KeyPressedAnsatz) obj;
        if (this.altStatus != other.altStatus) {
            return false;
        }
        if (this.ctrlStatus != other.ctrlStatus) {
            return false;
        }
        if (this.keyCode != other.keyCode) {
            return false;
        }
        if (this.shiftStatus != other.shiftStatus) {
            return false;
        }

        return true;
    }
}
