/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.icon.web;

import java.util.LinkedList;
import java.util.Queue;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import burai.com.life.Life;

public final class WebLoader implements Runnable {

    private static final long SLEEP_TIME = 250L;

    private static WebLoader webLoader = null;

    public static WebLoader getInstance() {
        if (webLoader == null) {
            webLoader = new WebLoader();
        }

        return webLoader;
    }

    private boolean alive;

    private Queue<WebUnit> webUnits;

    private WebLoader() {
        this.alive = true;
        this.webUnits = new LinkedList<WebUnit>();

        Thread thread = new Thread(this);
        thread.start();

        Life.getInstance().addOnDead(() -> this.stop());
    }

    private synchronized boolean isAlive() {
        return this.alive;
    }

    public void stop() {
        synchronized (this) {
            this.alive = false;
            this.notifyAll();
        }

        synchronized (this.webUnits) {
            this.webUnits.notifyAll();
        }
    }

    public void loadWebEngine(WebEngineWrapper webEngineWrapper) {
        if (webEngineWrapper == null) {
            return;
        }

        WebEngine webEngine = webEngineWrapper.getWebEngine();
        this.loadWebEngine(webEngine);
    }

    public void loadWebEngine(WebEngine webEngine) {
        this.loadWebEngine(null, webEngine);
    }

    public void loadWebEngine(String url, WebEngineWrapper webEngineWrapper) {
        if (webEngineWrapper == null) {
            return;
        }

        WebEngine webEngine = webEngineWrapper.getWebEngine();
        this.loadWebEngine(url, webEngine);
    }

    public void loadWebEngine(String url, WebEngine webEngine) {
        if (webEngine == null) {
            return;
        }

        synchronized (this.webUnits) {
            this.webUnits.offer(new WebUnit(url, webEngine));
            this.webUnits.notifyAll();
        }
    }

    public void removeWebEngine(WebEngineWrapper webEngineWrapper) {
        if (webEngineWrapper == null) {
            return;
        }

        WebEngine webEngine = webEngineWrapper.getWebEngine();
        this.removeWebEngine(webEngine);
    }

    public void removeWebEngine(WebEngine webEngine) {
        if (webEngine == null) {
            return;
        }

        synchronized (this.webUnits) {
            WebUnit webUnit = new WebUnit(null, webEngine);
            while (true) {
                if (this.webUnits.contains(webUnit)) {
                    this.webUnits.remove(webUnit);
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public void run() {
        while (this.isAlive()) {
            synchronized (this) {
                try {
                    this.wait(SLEEP_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            synchronized (this.webUnits) {
                if (this.webUnits.isEmpty()) {
                    try {
                        this.webUnits.wait();
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }

                } else {
                    WebUnit webUnit = this.webUnits.poll();
                    if (webUnit != null) {
                        String url = webUnit.getURL();
                        WebEngine webEngine = webUnit.getWebEngine();

                        if (url != null) {
                            Platform.runLater(() -> webEngine.load(url));
                        } else {
                            Platform.runLater(() -> webEngine.reload());
                        }
                    }
                }
            }
        }
    }

    private static class WebUnit {

        private String url;

        private WebEngine webEngine;

        public WebUnit(String url, WebEngine webEngine) {
            if (webEngine == null) {
                throw new IllegalArgumentException("webEngine is null.");
            }

            this.url = url;
            this.webEngine = webEngine;
        }

        public String getURL() {
            return this.url;
        }

        public WebEngine getWebEngine() {
            return this.webEngine;
        }

        @Override
        public int hashCode() {
            return this.webEngine == null ? 0 : this.webEngine.hashCode();
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

            WebUnit other = (WebUnit) obj;
            return this.webEngine == other.webEngine;
        }
    }
}
