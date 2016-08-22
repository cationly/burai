/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.tab;

import javafx.scene.control.Tooltip;
import javafx.scene.web.WebEngine;

public class QEFXWebTab extends QEFXTab<WebEngine> {

    public QEFXWebTab(WebEngine engine) {
        super(engine);

        this.setupOnClosed();
        this.setupContent();
        this.updateTabTitle();
    }

    private void setupOnClosed() {
        this.setOnClosed(event -> {
            this.body.load("about:blank");
        });
    }

    private void setupContent() {
        this.body.titleProperty().addListener(o -> {
            this.updateTabTitle();
        });

        this.body.locationProperty().addListener(o -> {
            this.updateTabTitle();
        });
    }

    private void updateTabTitle() {
        String title = this.body.getTitle();
        String location = this.body.getLocation();

        if (title == null || title.trim().isEmpty()) {
            this.setTabTitle(this.body.getLocation());
        } else {
            this.setTabTitle(title);
        }

        this.setTooltip(new Tooltip(location));
    }

    public boolean equalsEngine(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        QEFXWebTab other = (QEFXWebTab) obj;
        return this.body == other.body;
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

        QEFXWebTab other = (QEFXWebTab) obj;

        String location1 = this.body.getLocation();
        String location2 = other.body.getLocation();
        if (location1 != null) {
            return location1.equals(location2);
        }

        return location1 == location2;
    }
}
