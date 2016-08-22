/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.run.parser;

import java.io.File;
import java.io.IOException;

import burai.project.property.ProjectProperty;

public abstract class LogParser {

    private static final long SLEEP_TIME = 5000L;

    private boolean parsing;

    private boolean ending;

    protected ProjectProperty property;

    public LogParser(ProjectProperty property) {
        if (property == null) {
            throw new IllegalArgumentException("property is null.");
        }

        this.parsing = false;
        this.ending = false;
        this.property = property;
    }

    public abstract void parse(File file) throws IOException;

    public void startParsing(File file) {
        if (file == null) {
            return;
        }

        synchronized (this) {
            this.parsing = true;
            this.ending = false;
        }

        Thread thread = new Thread(() -> {
            while (true) {
                synchronized (this) {
                    if (!this.parsing) {
                        break;
                    }
                }

                try {
                    this.parse(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                synchronized (this) {
                    if (!this.parsing) {
                        break;
                    }

                    try {
                        this.wait(SLEEP_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                this.parse(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            synchronized (this) {
                this.ending = false;
                this.notifyAll();
            }
        });

        thread.start();
    }

    public void endParsing() {
        synchronized (this) {
            this.parsing = false;
            this.ending = true;
            this.notifyAll();
        }

        synchronized (this) {
            while (this.ending) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.parsing = false;
            this.ending = false;
        }
    }
}
