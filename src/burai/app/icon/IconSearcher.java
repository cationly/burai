/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.icon;

import java.util.Vector;

public class IconSearcher {

    private static final long SLEEP_TIME = 800L;

    private String currentCode;

    private boolean toRemoveCurrentCode;

    private Vector<QEFXIcon> icons;

    public IconSearcher() {
        this.currentCode = null;
        this.toRemoveCurrentCode = true;
        this.icons = new Vector<QEFXIcon>();
    }

    public void addIcon(QEFXIcon icon) {
        if (icon == null) {
            return;
        }

        this.icons.addElement(icon);
    }

    public void removeIcon(QEFXIcon icon) {
        if (icon == null) {
            return;
        }

        if (!this.icons.isEmpty()) {
            this.icons.removeElement(icon);
        }
    }

    public void insertIcon(QEFXIcon srcIcon, QEFXIcon dstIcon) {
        if (srcIcon == null || dstIcon == null) {
            return;
        }

        int index = this.icons.indexOf(srcIcon);
        if (index > -1) {
            this.icons.insertElementAt(dstIcon, index);
        }
    }

    public void swapIcon(QEFXIcon srcIcon, QEFXIcon dstIcon) {
        if (srcIcon == null || dstIcon == null) {
            return;
        }

        int index = this.icons.indexOf(srcIcon);
        if (index > -1) {
            this.icons.insertElementAt(dstIcon, index);
            this.icons.removeElementAt(index + 1);
        }
    }

    public synchronized void clear() {
        this.currentCode = null;
        this.toRemoveCurrentCode = true;
        this.icons = null;
        this.notifyAll();
    }

    public QEFXIcon search(String code) {
        if (code == null) {
            return null;
        }

        if (this.icons.isEmpty()) {
            return null;
        }

        String currentCode = this.getCurrentCode(code);
        if (currentCode == null) {
            return null;
        }

        currentCode = currentCode.toUpperCase();

        for (int i = 0; i < this.icons.size(); i++) {
            QEFXIcon icon = this.icons.get(i);
            String caption = null;
            if (icon != null) {
                caption = icon.getCaption();
            }
            if (caption == null) {
                continue;
            }

            caption = caption.toUpperCase();
            if (caption.startsWith(currentCode)) {
                return icon;
            }
        }

        return null;
    }

    private synchronized String getCurrentCode(String code) {
        if (code == null) {
            return null;
        }

        if (this.currentCode == null) {
            this.currentCode = code;
            this.runCurrentCodeRemover();

        } else {
            this.toRemoveCurrentCode = false;
            this.currentCode = this.currentCode + code;
            this.notifyAll();
        }

        return this.currentCode;
    }

    private void runCurrentCodeRemover() {
        Thread thread = new Thread(() -> {
            synchronized (this) {
                while (true) {
                    this.toRemoveCurrentCode = true;

                    try {
                        this.wait(SLEEP_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (this.toRemoveCurrentCode) {
                        break;
                    }
                }

                this.currentCode = null;
            }
        });

        thread.start();
    }
}
