/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.matapi;

import burai.com.life.Life;

public class MaterialsAPIHolder {

    private static MaterialsAPIHolder instance = null;

    public static MaterialsAPIHolder getInstance() {
        if (instance == null) {
            instance = new MaterialsAPIHolder();
        }

        return instance;
    }

    private MaterialsAPILoader loader;

    private MaterialsAPIHolder() {
        this.loader = null;

        Life.getInstance().addOnDead(() -> this.deleteLoader());
    }

    public synchronized void setLoader(MaterialsAPILoader loader) {
        this.loader = loader;
    }

    public synchronized void deleteLoader() {
        if (this.loader != null) {
            if (this.loader.isAlive()) {
                this.loader.setToBeDead();
            }

            this.loader = null;
        }
    }
}
