/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.env;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import burai.ver.Version;

public final class EnvProperties {

    private Properties propertiesSystem;
    private Properties propertiesUser;

    private String filePathUser;

    protected EnvProperties(String propName) {
        if (propName == null) {
            throw new IllegalArgumentException("propName is null.");
        }

        String propName2 = propName.trim();
        if (propName2.isEmpty()) {
            throw new IllegalArgumentException("propName is empty.");
        }

        this.createPropertiesSytem(propName2);
        this.propertiesUser = null;
        this.filePathUser = null;
    }

    protected void setUserFile(String filePath) {
        if (filePath == null) {
            return;
        }

        String filePath2 = filePath.trim();
        if (filePath2.isEmpty()) {
            return;
        }

        try {
            File file = new File(filePath2);
            if (!file.exists()) {
                this.clonePropertiesSystem(filePath2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.createPropertiesUser(filePath2);

        if (this.propertiesUser == null) {
            return;
        }

        // check version
        String ver = this.propertiesUser.getProperty("version");

        double verNum = 0.0;
        if (ver != null) {
            try {
                verNum = Double.parseDouble(ver);
            } catch (NumberFormatException e) {
                verNum = 0.0;
            }
        }

        if (Math.abs(verNum - Version.VERSION_NUMBER) > 1.0e-8) {
            try {
                this.clonePropertiesSystem(filePath2);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            this.createPropertiesUser(filePath2);
        }
    }

    protected String getProperty(String key) {
        if (key == null) {
            return null;
        }

        if (this.propertiesUser != null) {
            String value = this.propertiesUser.getProperty(key);
            if (value != null) {
                return value;
            }
        }

        if (this.propertiesSystem != null) {
            String value = this.propertiesSystem.getProperty(key);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    protected void setProperty(String key, String value) {
        if (key == null) {
            return;
        }

        if (this.propertiesUser != null) {
            if (value == null) {
                this.propertiesUser.remove(key);
            } else {
                this.propertiesUser.setProperty(key, value);
            }

            this.printPropertiesUser();
        }
    }

    private void createPropertiesSytem(String propName) {
        URL url = propName == null ? null : EnvProperties.class.getResource(propName);
        if (url == null) {
            return;
        }

        this.propertiesSystem = null;

        try {
            this.loadPropertiesSystem(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPropertiesUser(String filePath) {
        if (filePath == null) {
            return;
        }

        this.propertiesUser = null;
        this.filePathUser = filePath;

        try {
            this.loadPropertiesUser(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printPropertiesUser() {
        if (this.filePathUser == null) {
            return;
        }

        try {
            this.storePropertiesUser(this.filePathUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPropertiesSystem(URL url) throws IOException {
        if (url == null) {
            return;
        }

        BufferedInputStream inputStream = null;

        try {
            inputStream = new BufferedInputStream(url.openStream());
            this.propertiesSystem = new Properties();
            this.propertiesSystem.load(inputStream);

        } catch (IOException e1) {
            throw e1;

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {
                    throw e2;
                }
            }
        }
    }

    private void loadPropertiesUser(String filePath) throws IOException {
        if (filePath == null) {
            return;
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filePath.trim()));
            this.propertiesUser = new Properties();
            this.propertiesUser.load(reader);

        } catch (IOException e1) {
            throw e1;

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {
                    throw e2;
                }
            }
        }
    }

    private void storePropertiesUser(String filePath) throws IOException {
        if (filePath == null) {
            return;
        }

        if (this.propertiesUser == null) {
            return;
        }

        this.writeProperties(filePath, this.propertiesUser);
    }

    private void clonePropertiesSystem(String filePath) throws IOException {
        if (filePath == null) {
            return;
        }

        if (this.propertiesSystem == null) {
            return;
        }

        this.writeProperties(filePath, this.propertiesSystem);
    }

    private void writeProperties(String filePath, Properties properties) throws IOException {
        if (filePath == null) {
            return;
        }

        if (properties == null) {
            return;
        }

        Set<Object> keySet = properties.keySet();
        if (keySet == null || keySet.isEmpty()) {
            return;
        }

        List<String> keyList = new ArrayList<String>();
        for (Object key : keySet) {
            if (key != null) {
                keyList.add(key.toString());
            }
        }

        if (keyList.isEmpty()) {
            return;
        }

        Collections.sort(keyList);

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath.trim())));

            for (String key : keyList) {
                String value = properties.getProperty(key);
                value = value == null ? null : value.trim();
                if (value != null && (!value.isEmpty())) {
                    writer.println(key + " = " + value);
                }
            }

        } catch (IOException e) {
            throw e;

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
