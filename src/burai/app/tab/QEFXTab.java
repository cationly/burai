/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.tab;

import javafx.scene.control.Tab;

public abstract class QEFXTab<T> extends Tab {

    private static final int MAX_TEXT = 16;
    private static final String DEFAULT_TEXT = "No Name";

    protected T body;

    public QEFXTab(T body) {
        super();

        if (body == null) {
            throw new IllegalArgumentException("body is null.");
        }

        this.body = body;
        this.setClosable(true);
    }

    public T getBody() {
        return this.body;
    }

    protected void setTabTitle(String title) {
        String text = null;
        if (title == null || title.trim().isEmpty()) {
            text = DEFAULT_TEXT;
        } else {
            text = title.trim();
        }

        if (text.length() > (MAX_TEXT + 3)) {
            text = text.substring(0, MAX_TEXT) + "...";
        }

        this.setText(text);
    }

    @Override
    public int hashCode() {
        return this.body == null ? 0 : this.body.hashCode();
    }

    @Override
    public abstract boolean equals(Object obj);

}
