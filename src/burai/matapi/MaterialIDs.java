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
import java.util.List;

import com.google.gson.Gson;

public class MaterialIDs {

    private static final String MATERIALS_API_MIDS = "/mids";

    public static MaterialIDs getInstance(String formula) {
        if (formula == null || formula.trim().isEmpty()) {
            throw new IllegalArgumentException("formula is empty.");
        }

        MaterialIDs matIDs = null;

        try {
            matIDs = readURL(formula);
        } catch (IOException e) {
            e.printStackTrace();
            matIDs = null;
        }

        return matIDs;
    }

    private static MaterialIDs readURL(String formula) throws IOException {
        Reader reader = null;
        MaterialIDs matIDs = null;

        try {
            URL url = new URL(MaterialsAPI.MATERIALS_API_URL + formula.trim() + MATERIALS_API_MIDS);
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
            matIDs = gson.<MaterialIDs> fromJson(reader, MaterialIDs.class);

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

    private List<String> response;

    private MaterialIDs() {
        this.response = null;
    }

    public int numIDs() {
        return this.response == null ? 0 : this.response.size();
    }

    public String getID(int index) throws IndexOutOfBoundsException {
        if (this.response == null) {
            return null;
        }

        if (index < 0 || this.response.size() <= index) {
            throw new IndexOutOfBoundsException("incorrect index: " + index);
        }

        return this.response.get(index);
    }
}
