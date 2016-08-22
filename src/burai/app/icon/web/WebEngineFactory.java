/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.icon.web;

import java.util.HashMap;
import java.util.Map;

import burai.com.env.Environments;

public class WebEngineFactory {

    private static WebEngineFactory webEngineFactory;

    public static WebEngineFactory getInstance() {
        if (webEngineFactory == null) {
            webEngineFactory = new WebEngineFactory();
        }

        return webEngineFactory;
    }

    private Map<String, WebEngineWrapper> webEngineMap;

    private WebEngineFactory() {
        this.webEngineMap = new HashMap<String, WebEngineWrapper>();
    }

    public synchronized WebEngineWrapper getWebEngine(String url) {
        String url2 = url == null ? null : url.trim();
        if (url2 == null || url2.isEmpty()) {
            return new WebEngineWrapper();
        }

        if (this.webEngineMap.containsKey(url2)) {
            return this.webEngineMap.get(url2);
        }

        WebEngineWrapper webEngine = new WebEngineWrapper(url2);
        WebLoader.getInstance().loadWebEngine(url2, webEngine);
        this.webEngineMap.put(url2, webEngine);
        return webEngine;
    }

    public void touchAllWebEngines() {
        String[] urls = Environments.listWebsites();
        if (urls == null || urls.length < 1) {
            return;
        }

        for (String url : urls) {
            String url2 = url == null ? null : url.trim();
            if (url2 != null && (!url2.isEmpty())) {
                this.getWebEngine(url2);
            }
        }
    }
}
