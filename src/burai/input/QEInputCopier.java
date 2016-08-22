/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input;

import burai.input.card.QECard;
import burai.input.namelist.QENamelist;

public class QEInputCopier {

    private QEInput srcInput;

    public QEInputCopier(QEInput srcInput) {
        if (srcInput == null) {
            throw new IllegalArgumentException("srcInput is null.");
        }

        this.srcInput = srcInput;
    }

    public void copyTo(QEInput dstInput) {
        this.copyTo(dstInput, true);
    }

    public void copyTo(QEInput dstInput, boolean protect) {
        if (dstInput == null) {
            throw new IllegalArgumentException("dstInput is null.");
        }

        String[] keyNamelists = QEInput.listNamelistKeys();
        for (String keyNamelist : keyNamelists) {
            QENamelist srcNamelist = this.srcInput.getNamelist(keyNamelist);
            QENamelist dstNamelist = dstInput.getNamelist(keyNamelist);
            if (srcNamelist != null && dstNamelist != null) {
                srcNamelist.copyTo(dstNamelist, protect);
            }
        }

        String[] keyCards = QEInput.listCardKeys();
        for (String keyCard : keyCards) {
            QECard srcCard = this.srcInput.getCard(keyCard);
            QECard dstCard = dstInput.getCard(keyCard);
            if (srcCard != null && dstCard != null) {
                srcCard.copyTo(dstCard, protect);
            }
        }
    }
}
