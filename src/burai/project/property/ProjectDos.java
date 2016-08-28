/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project.property;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProjectDos {

    private String path;

    private String prefix;

    private Map<File, DosData> dosDataMap;

    public ProjectDos(String path, String prefix) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is empty.");
        }

        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("prefix is empty.");
        }

        this.path = path;
        this.prefix = prefix;

        this.dosDataMap = null;
        this.reload();
    }

    public String getPath() {
        return this.path;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void reload() {
        if (this.dosDataMap == null) {
            this.dosDataMap = new HashMap<File, DosData>();
        }

        try {
            File dirFile = new File(this.path);
            if (!dirFile.isDirectory()) {
                return;
            }

            File[] files = dirFile.listFiles((dir, name) -> {
                if (name == null || name.isEmpty()) {
                    return false;
                } else if (name.equals(this.prefix + ".dos")) {
                    return true;
                } else if (name.startsWith(this.prefix + ".pdos_atm")) {
                    return true;
                } else {
                    return false;
                }
            });

            if (files == null) {
                return;
            }

            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }

                if (!this.dosDataMap.containsKey(file)) {
                    this.dosDataMap.put(file, new DosData(file));

                } else {
                    DosData dosData = this.dosDataMap.get(file);
                    if (dosData != null) {
                        dosData.reload();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
