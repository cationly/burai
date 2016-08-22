/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.env;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public final class EnvFile {

    private static final String SEPARATOR = "#<---->#";

    private String filePath;

    protected EnvFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("filePath is empty.");
        }

        this.filePath = filePath.trim();
    }

    protected synchronized void addLine(String line, String... opts) {
        if (line == null) {
            return;
        }

        String line2 = line.trim();
        if (line2.isEmpty()) {
            return;
        }

        List<String> lines = this.loadFileKernel();
        if (lines != null && (!lines.isEmpty())) {
            for (String lineTmp : lines) {
                if (lineTmp == null) {
                    continue;
                }
                String[] subLine = lineTmp.trim().split(SEPARATOR);
                if (subLine != null && subLine.length > 0) {
                    if (line2.equals(subLine[0])) {
                        return;
                    }
                }
            }
        }

        String lineTot = line2;
        if (opts != null && opts.length > 0) {
            for (String opt : opts) {
                String opt2 = opt == null ? null : opt.trim();
                if (opt2 != null && (!opt2.isEmpty())) {
                    lineTot = lineTot + SEPARATOR + opt2;
                }
            }
        }

        this.appendLineKernel(lineTot);
    }

    protected synchronized void removeLine(String line) {
        if (line == null) {
            return;
        }

        String[] subLine = line.trim().split(SEPARATOR);
        if (subLine == null || subLine.length < 1) {
            return;
        }

        String line2 = subLine[0];
        line2 = line2 == null ? null : line2.trim();
        if (line2 == null || line2.isEmpty()) {
            return;
        }

        List<String> lines = this.loadFileKernel();
        if (lines == null || lines.isEmpty()) {
            return;
        }

        List<String> linesLeft = new ArrayList<String>(lines.size());
        for (String lineTmp : lines) {
            if (lineTmp == null) {
                linesLeft.add(lineTmp);
                continue;
            }

            String lineTmp2 = lineTmp.trim();
            String[] subLineTmp = lineTmp2.split(SEPARATOR);
            if (subLineTmp == null || subLineTmp.length < 1) {
                linesLeft.add(lineTmp);
                continue;
            }

            lineTmp2 = subLineTmp[0];
            lineTmp2 = lineTmp2 == null ? null : lineTmp2.trim();
            if (!line2.equals(lineTmp2)) {
                linesLeft.add(lineTmp);
            }
        }

        this.writeLinesKernel(linesLeft);
    }

    protected synchronized String pollLine() {
        List<String> lines = this.loadFileKernel();
        if (lines == null || lines.isEmpty()) {
            return null;
        }

        String line = lines.remove(0);
        this.writeLinesKernel(lines);
        return line;
    }

    protected synchronized List<String> loadFile() {
        return this.loadFileKernel();
    }

    protected String splitLine(int i, String line) {
        if (line == null) {
            return null;
        }

        String line2 = line.trim();
        if (line2.isEmpty()) {
            return null;
        }

        String[] subLine = line2.split(SEPARATOR);
        if (subLine == null || subLine.length < 1) {
            return null;
        }

        if (i < 0 || subLine.length <= i) {
            return null;
        }

        String str = subLine[i];
        return str == null ? null : str.trim();
    }

    private void appendLineKernel(String line) {
        if (line == null) {
            return;
        }

        String line2 = line.trim();
        if (line2.isEmpty()) {
            return;
        }

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(this.filePath, true)));
            writer.println(line2);

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void writeLinesKernel(List<String> lines) {
        if (lines == null) {
            return;
        }

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(this.filePath, false)));
            for (String line : lines) {
                if (line != null) {
                    writer.println(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private List<String> loadFileKernel() {
        File file = new File(this.filePath);

        try {
            if ((!file.exists()) || (!file.isFile())) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        BufferedReader reader = null;
        List<String> lines = new ArrayList<String>();

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = (reader.readLine())) != null) {
                lines.add(line.trim());
            }

        } catch (IOException e1) {
            e1.printStackTrace();
            lines.clear();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                    lines.clear();
                }
            }
        }

        return lines;
    }
}
