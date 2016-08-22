/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.matapi;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;

public class MaterialData {

    private static final String MATERIALS_API_CIF = "/cif";

    public static MaterialData getInstance(String matID) {
        if (matID == null || matID.trim().isEmpty()) {
            throw new IllegalArgumentException("matID is empty.");
        }

        MaterialData matData = null;

        try {
            matData = readURL(matID);
        } catch (IOException e) {
            e.printStackTrace();
            matData = null;
        }

        return matData;
    }

    private static MaterialData readURL(String matID) throws IOException {
        Reader reader = null;
        MaterialData matIDs = null;

        try {
            URL url = new URL(MaterialsAPI.MATERIALS_API_URL + matID.trim() + MATERIALS_API_CIF);
            URLConnection urlConnection = url.openConnection();
            if (urlConnection == null) {
                throw new IOException("urlConnection is null.");
            }

            InputStream input = urlConnection.getInputStream();
            input = input == null ? null : new BufferedInputStream(input);
            if (input == null) {
                throw new IOException("input is null.");
            }

            reader = new InputStreamReader(input);

            Gson gson = new Gson();
            matIDs = gson.<MaterialData> fromJson(reader, MaterialData.class);

        } catch (IOException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }

        return matIDs;
    }

    private String cif;

    private MaterialData() {
        this.cif = null;
    }

    public String getCIF() {
        return this.cif;
    }
}
