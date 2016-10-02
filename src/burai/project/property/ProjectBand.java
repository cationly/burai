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

public class ProjectBand {

    private String path;

    private String prefix;

    private BandData[] bandDatas;

    public ProjectBand(String path, String prefix) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is empty.");
        }

        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("prefix is empty.");
        }

        this.path = path;
        this.prefix = prefix;

        this.bandDatas = new BandData[2];
        this.reload();
    }

    public String getPath() {
        return this.path;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public BandData getBandData() {
        return this.getBandData(true);
    }

    public BandData getBandData(boolean spin) {
        return this.bandDatas[(spin ? 0 : 1)];
    }

    public boolean reload() {
        boolean reloaded = false;

        try {
            File dirFile = new File(this.path);
            if (!dirFile.isDirectory()) {
                return false;
            }

            File file1 = new File(dirFile, this.prefix + ".band1.gnu");
            File file2 = new File(dirFile, this.prefix + ".band2.gnu");
            File[] files = { file1, file2 };

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (!file.isFile()) {
                    continue;
                }

                if (this.bandDatas[i] == null) {
                    this.bandDatas[i] = new BandData(file);
                    reloaded = true;

                } else {
                    BandData bandData = this.bandDatas[i];
                    if (bandData != null) {
                        reloaded = reloaded || bandData.reload();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reloaded;
    }
}
