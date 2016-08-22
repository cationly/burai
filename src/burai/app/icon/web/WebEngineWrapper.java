/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.icon.web;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.web.WebEngine;
import burai.com.env.Environments;

public class WebEngineWrapper {

    private WebEngine engine;

    private ReadOnlyStringWrapper title;

    private ReadOnlyStringWrapper location;

    public WebEngineWrapper() {
        this(null);
    }

    public WebEngineWrapper(String url) {
        String url2 = url == null ? null : url.trim();

        String strTitle = null;
        if (url2 != null && (!url2.isEmpty())) {
            strTitle = Environments.titleWebsite(url2);
        }

        this.location = new ReadOnlyStringWrapper(this, "location");
        this.location.set(url2);

        this.title = new ReadOnlyStringWrapper(this, "title");
        if (strTitle != null && (!strTitle.isEmpty())) {
            this.title.set(strTitle);
        }

        this.engine = null;
        if (strTitle == null || strTitle.isEmpty()) {
            this.engine = new WebEngine();
        }
    }

    public WebEngine getWebEngine() {
        return this.engine;
    }

    public void load(String url) {
        if (this.engine != null) {
            this.engine.load(url);
        }
    }

    public String getTitle() {
        if (this.engine == null) {
            return this.title.getValue();
        } else {
            return this.engine.getTitle();
        }
    }

    public ReadOnlyStringProperty titleProperty() {
        if (this.engine == null) {
            return this.title.getReadOnlyProperty();
        } else {
            return this.engine.titleProperty();
        }
    }

    public String getLocation() {
        if (this.engine == null) {
            return this.location.getValue();
        } else {
            return this.engine.getLocation();
        }
    }

    public ReadOnlyStringProperty locationProperty() {
        if (this.engine == null) {
            return this.location.getReadOnlyProperty();
        } else {
            return this.engine.locationProperty();
        }
    }
}
