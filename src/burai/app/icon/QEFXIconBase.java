/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.icon;

import java.io.File;
import java.text.SimpleDateFormat;

public abstract class QEFXIconBase<T> extends QEFXIcon {

    protected static final double ICON_SCALE = 0.55;

    protected T content;

    public QEFXIconBase(T content) {
        super();

        if (content == null) {
            throw new IllegalArgumentException("content is null.");
        }

        if (content instanceof String) {
            if (((String) content).trim().isEmpty()) {
                throw new IllegalArgumentException("content is empty.");
            }
        }

        this.content = content;
    }

    public T getContent() {
        return this.content;
    }

    protected String getFileDetail(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        File file = new File(path);
        try {
            if (!file.exists()) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        Long timeStamp = 0L;
        try {
            timeStamp = file.lastModified();
        } catch (Exception e) {
            timeStamp = 0L;
        }

        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String date = dataFormat.format(timeStamp);

        String caption = "Path: " + path + System.lineSeparator() + "Date: " + date;
        return caption;
    }
}
