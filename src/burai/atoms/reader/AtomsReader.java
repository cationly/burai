/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import burai.atoms.model.Cell;

public abstract class AtomsReader {

    private static final int FILE_TYPE_NULL = 0;
    private static final int FILE_TYPE_XYZ = 1;
    private static final int FILE_TYPE_CIF = 2;
    private static final int FILE_TYPE_CUBE = 3;
    private static final int FILE_TYPE_QE = 4;

    private static int getFileType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return FILE_TYPE_NULL;
        }

        String[] subNames = fileName.trim().split("\\.");
        if (subNames == null || subNames.length < 1) {
            return FILE_TYPE_NULL;
        }

        String extName = subNames[subNames.length - 1];
        if (extName == null || extName.isEmpty()) {
            return FILE_TYPE_NULL;
        }

        if ("xyz".equalsIgnoreCase(extName)) {
            return FILE_TYPE_XYZ;
        } else if ("cif".equalsIgnoreCase(extName)) {
            return FILE_TYPE_CIF;
        } else if ("cube".equalsIgnoreCase(extName) || "cub".equalsIgnoreCase(extName)) {
            return FILE_TYPE_CUBE;
        } else if ("in".equalsIgnoreCase(extName)) {
            return FILE_TYPE_QE;
        } else {
            return FILE_TYPE_NULL;
        }
    }

    public static boolean isToBeInstance(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        return getFileType(fileName) != FILE_TYPE_NULL;
    }

    public static AtomsReader getInstance(String fileName) throws IOException {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("file name is empty.");
        }

        AtomsReader atomsReader = null;
        int fileType = getFileType(fileName);

        switch (fileType) {
        case FILE_TYPE_XYZ:
            atomsReader = new XYZReader(fileName);
            break;
        case FILE_TYPE_CIF:
            atomsReader = new CIFReader(fileName);
            break;
        case FILE_TYPE_CUBE:
            atomsReader = new CubeReader(fileName);
            break;
        case FILE_TYPE_QE:
            atomsReader = new QEReader(fileName);
            break;

        default:
            throw new IOException("cannot read a file: " + fileName);
        }

        return atomsReader;
    }

    protected BufferedReader reader;

    protected AtomsReader() {
        this.reader = null;
    }

    protected AtomsReader(String fileName) throws FileNotFoundException {
        this(fileName == null || fileName.isEmpty() ? null : new File(fileName));
    }

    protected AtomsReader(File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("file is null.");
        }

        this.reader = new BufferedReader(new FileReader(file));
    }

    public abstract Cell readCell() throws IOException;

    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }
}
