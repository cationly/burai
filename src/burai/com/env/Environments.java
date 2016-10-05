/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.env;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import burai.com.math.Calculator;

public final class Environments {

    private static final String PROJECTS_NAME = ".burai";

    private static final String DOWNLOADS_NAME = ".download";

    private static final String PSEUDOS_NAME = ".pseudopot";

    private static final String PSEUDOLIST_NAME = ".pseudolist";

    private static final String MATERIALSAPI_NAME = ".materialsapi";

    private static final String RECENTS_NAME = ".recent";

    private static final String WEBDATA_NAME = ".webdata";

    private static final String WEBSITES_NAME = ".websites";

    private static final String PROPERTIES_NAME = ".properties";

    private static EnvFile recentsEnvFile = null;

    private static EnvFile websitesEnvFile = null;

    private static EnvProperties envProperties = null;

    private static Map<String, String> websiteTitles = null;

    private Environments() {
        // NOP
    }

    public static String getOSName() {
        return System.getProperty("os.name", null);
    }

    public static boolean isWindows() {
        String osName = getOSName();
        if (osName == null) {
            return false;
        }

        osName = osName.toLowerCase().trim();
        return osName.startsWith("windows");
    }

    public static boolean isMac() {
        String osName = getOSName();
        if (osName == null) {
            return false;
        }

        osName = osName.toLowerCase().trim();
        return osName.startsWith("mac");
    }

    public static boolean isLinux() {
        String osName = getOSName();
        if (osName == null) {
            return false;
        }

        osName = osName.toLowerCase().trim();
        return osName.startsWith("linux");
    }

    public static int getNumCUPs() {
        CPUInfo cpuInfo = CPUInfo.getInstance();
        return cpuInfo == null ? 1 : cpuInfo.getNumCPUs();
    }

    public static String getHomePath() {
        String homeProp = System.getProperty("user.home", null);
        if (homeProp != null) {
            return homeProp;
        }

        try {
            if (isWindows()) {
                String homeDrive = System.getenv("HOMEDRIVE");
                if (homeDrive == null) {
                    return null;
                }

                String homePath = System.getenv("HOMEPATH");
                if (homePath == null) {
                    return null;
                }

                return homeDrive + homePath;

            } else {
                return System.getenv("HOME");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getRootPath() {
        String homePath = getHomePath();

        if (homePath != null) {
            File homeFile = new File(homePath);
            while (true) {
                File parentFile = homeFile.getParentFile();
                if (parentFile != null) {
                    homeFile = parentFile;
                } else {
                    break;
                }
            }

            return homeFile.getPath();
        }

        if (isWindows()) {
            return System.getenv("HOMEDRIVE");
        } else {
            return "/";
        }
    }

    public static String getProjectsName() {
        return PROJECTS_NAME;
    }

    public static String getProjectsPath() {
        File projectsFile = null;
        String homePath = getHomePath();
        if (homePath == null) {
            projectsFile = new File(PROJECTS_NAME);
        } else {
            projectsFile = new File(homePath, PROJECTS_NAME);
        }

        try {
            if (!projectsFile.isDirectory()) {
                projectsFile.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return projectsFile.getPath();
    }

    public static boolean existsProjectsPath() {
        File projectsFile = null;
        String homePath = getHomePath();
        if (homePath == null) {
            projectsFile = new File(PROJECTS_NAME);
        } else {
            projectsFile = new File(homePath, PROJECTS_NAME);
        }

        try {
            if (projectsFile.isDirectory()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getDownloadsName() {
        return DOWNLOADS_NAME;
    }

    public static String getDownloadsPath() {
        File downloadsFile = null;
        String projPath = getProjectsPath();
        if (projPath == null) {
            downloadsFile = new File(DOWNLOADS_NAME);
        } else {
            downloadsFile = new File(projPath, DOWNLOADS_NAME);
        }

        try {
            if (!downloadsFile.isDirectory()) {
                downloadsFile.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return downloadsFile.getPath();
    }

    public static String getPseudosName() {
        return PSEUDOS_NAME;
    }

    public static String getPseudosPath() {
        File pseudosFile = null;
        String projPath = getProjectsPath();
        if (projPath == null) {
            pseudosFile = new File(PSEUDOS_NAME);
        } else {
            pseudosFile = new File(projPath, PSEUDOS_NAME);
        }

        try {
            if (!pseudosFile.isDirectory()) {
                pseudosFile.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pseudosFile.getPath();
    }

    public static String getPseudoListName() {
        return PSEUDOLIST_NAME;
    }

    public static String getPseudoListPath() {
        File pseudoListFile = null;
        String pseudosPath = getPseudosPath();
        if (pseudosPath == null) {
            pseudoListFile = new File(PSEUDOLIST_NAME);
        } else {
            pseudoListFile = new File(pseudosPath, PSEUDOLIST_NAME);
        }

        return pseudoListFile.getPath();
    }

    public static String getMaterialsAPIName(String label) {
        if (label == null) {
            return MATERIALSAPI_NAME;
        }

        String label2 = label.trim();
        if (label2.isEmpty()) {
            return MATERIALSAPI_NAME;
        }

        return MATERIALSAPI_NAME + "." + label;
    }

    public static String getMaterialsAPIPath(String label) {
        File matapiFile = null;
        String projPath = getProjectsPath();
        if (projPath == null) {
            matapiFile = new File(getMaterialsAPIName(label));
        } else {
            matapiFile = new File(projPath, getMaterialsAPIName(label));
        }

        try {
            if (!matapiFile.isDirectory()) {
                matapiFile.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matapiFile.getPath();
    }

    public static String getEspressoWebsite() {
        return getProperty("espresso_website");
    }

    public static String getPseudoWebsite() {
        return getProperty("pseudos_website");
    }

    public static String getManPwscfWebsite() {
        return getProperty("man_pwscf_website");
    }

    public static String getManDosWebsite() {
        return getProperty("man_dos_website");
    }

    public static String getManProjwfcWebsite() {
        return getProperty("man_projwfc_website");
    }

    public static String getManBandsWebsite() {
        return getProperty("man_bands_website");
    }

    public static String getRecentsName() {
        return RECENTS_NAME;
    }

    public static String getRecentsPath() {
        File recentsFile = null;
        String projPath = getProjectsPath();
        if (projPath == null) {
            recentsFile = new File(RECENTS_NAME);
        } else {
            recentsFile = new File(projPath, RECENTS_NAME);
        }

        return recentsFile.getPath();
    }

    private static EnvFile getRecentsEnvFile() {
        if (recentsEnvFile == null) {
            String filePath = getRecentsPath();
            filePath = filePath == null ? null : filePath.trim();
            if (filePath != null && !(filePath.isEmpty())) {
                recentsEnvFile = new EnvFile(filePath);
            }
        }

        return recentsEnvFile;
    }

    public static void addRecentFilePath(String filePath) {
        String filePath2 = filePath == null ? null : filePath.trim();
        if (filePath2 == null || filePath2.isEmpty()) {
            return;
        }

        EnvFile envFile = getRecentsEnvFile();
        if (envFile == null) {
            return;
        }

        envFile.removeLine(filePath2);
        envFile.addLine(filePath2);

        File[] files = listRecentFiles();
        int maxRecents = getIntProperty("maximum_recents");
        for (int i = maxRecents; i < files.length; i++) {
            envFile.pollLine();
        }
    }

    public static void removeRecentFilePath(String filePath) {
        String filePath2 = filePath == null ? null : filePath.trim();
        if (filePath2 == null || filePath2.isEmpty()) {
            return;
        }

        EnvFile envFile = getRecentsEnvFile();
        if (envFile == null) {
            return;
        }

        envFile.removeLine(filePath2);
    }

    public static File[] listRecentFiles() {
        List<String> filePaths = null;

        EnvFile envFile = getRecentsEnvFile();
        if (envFile != null) {
            filePaths = envFile.loadFile();
        }

        if (filePaths == null || filePaths.isEmpty()) {
            return null;
        }

        List<File> files = new ArrayList<File>();
        for (String filePath : filePaths) {
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    files.add(file);
                }
            }
        }

        if (files == null || files.isEmpty()) {
            return null;
        }

        File[] fileArray = new File[files.size()];
        for (int i = 0; i < files.size(); i++) {
            fileArray[i] = files.get(files.size() - i - 1);
        }

        return fileArray;
    }

    public static String getWebdataName() {
        return WEBDATA_NAME;
    }

    public static String getWebdataPath() {
        File webdataFile = null;
        String projPath = getProjectsPath();
        if (projPath == null) {
            webdataFile = new File(WEBDATA_NAME);
        } else {
            webdataFile = new File(projPath, WEBDATA_NAME);
        }

        try {
            if (!webdataFile.isDirectory()) {
                webdataFile.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return webdataFile.getPath();
    }

    public static String getWebsitesName() {
        return WEBSITES_NAME;
    }

    public static String getWebsitesPath() {
        File websitesFile = null;
        String projPath = getProjectsPath();
        if (projPath == null) {
            websitesFile = new File(WEBSITES_NAME);
        } else {
            websitesFile = new File(projPath, WEBSITES_NAME);
        }

        return websitesFile.getPath();
    }

    private static EnvFile getWebsitesEnvFile() {
        if (websitesEnvFile == null) {
            String filePath = getWebsitesPath();
            filePath = filePath == null ? null : filePath.trim();
            if (filePath != null && !(filePath.isEmpty())) {
                websitesEnvFile = new EnvFile(filePath);
            }
        }

        return websitesEnvFile;
    }

    public static void addWebsite(String url) {
        addWebsite(null, url);
    }

    public static void addWebsite(String title, String url) {
        String title2 = title == null ? null : title.trim();
        String url2 = url == null ? null : url.trim();
        if (url2 == null || url2.isEmpty()) {
            return;
        }

        EnvFile envFile = getWebsitesEnvFile();
        if (envFile == null) {
            return;
        }

        if (title2 == null || title2.isEmpty()) {
            envFile.addLine(url2);

        } else {
            if (websiteTitles == null || websiteTitles.isEmpty()) {
                listWebsites();
            }
            if (websiteTitles != null) {
                websiteTitles.put(url2, title2);
            }
            envFile.addLine(url2, title2);
        }
    }

    public static void removeWebsite(String url) {
        String url2 = url == null ? null : url.trim();
        if (url2 == null || url2.isEmpty()) {
            return;
        }

        EnvFile envFile = getWebsitesEnvFile();
        if (envFile == null) {
            return;
        }

        envFile.removeLine(url2);
    }

    public static String[] listWebsites() {
        EnvFile envFile = getWebsitesEnvFile();
        if (envFile == null) {
            return null;
        }

        List<String> websites = envFile.loadFile();
        if (websites == null || websites.isEmpty()) {
            String website = getProperty("default_website");
            website = website == null ? null : website.trim();
            if (website != null && (!website.isEmpty())) {
                envFile.addLine(website);
                websites = envFile.loadFile();
            }
        }

        if (websites == null || websites.isEmpty()) {
            return null;
        }

        List<String> urls = new ArrayList<String>();
        List<String> titles = new ArrayList<String>();
        for (String website : websites) {
            if (website != null) {
                String url = envFile.splitLine(0, website);
                String title = envFile.splitLine(1, website);
                if (url != null) {
                    urls.add(url);
                    titles.add(title == null ? "" : title);
                }
            }
        }

        if (urls.isEmpty()) {
            return null;
        }

        if (websiteTitles == null || websiteTitles.isEmpty()) {
            int numUrls = urls.size();
            if (numUrls == titles.size()) {
                if (websiteTitles == null) {
                    websiteTitles = new HashMap<String, String>();
                }
                for (int i = 0; i < numUrls; i++) {
                    String url = urls.get(i);
                    if (url == null || url.isEmpty()) {
                        continue;
                    }
                    String title = titles.get(i);
                    if (title == null || title.isEmpty()) {
                        continue;
                    }
                    websiteTitles.put(url, title);
                }
            }
        }

        return urls.toArray(new String[urls.size()]);
    }

    public static String titleWebsite(String url) {
        String url2 = url == null ? null : url.trim();
        if (url2 == null || url2.isEmpty()) {
            return null;
        }

        if (websiteTitles == null || websiteTitles.isEmpty()) {
            listWebsites();
        }

        if (websiteTitles == null || websiteTitles.isEmpty()) {
            return null;
        }

        String title = websiteTitles.get(url2);
        title = title == null ? null : title.trim();

        if (title == null || title.isEmpty()) {
            return null;
        } else {
            return title;
        }
    }

    public static String getPropertiesName() {
        return PROPERTIES_NAME;
    }

    public static String getPropertiesPath() {
        File propFile = null;
        String parentPath = getProjectsPath();
        if (parentPath == null) {
            propFile = new File(PROPERTIES_NAME);
        } else {
            propFile = new File(parentPath, PROPERTIES_NAME);
        }

        return propFile.getPath();
    }

    private static EnvProperties getEnvProperties() {
        if (envProperties == null) {
            if (isWindows()) {
                envProperties = new EnvProperties("Environments.win.prop");
            } else {
                envProperties = new EnvProperties("Environments.unix.prop");
            }

            String filePath = getPropertiesPath();
            filePath = filePath == null ? null : filePath.trim();
            if (filePath != null && !(filePath.isEmpty())) {
                envProperties.setUserFile(filePath);
            }
        }

        return envProperties;
    }

    public static boolean hasProperty(String key) {
        return getProperty(key) != null;
    }

    public static String getProperty(String key) {
        if (key == null) {
            return null;
        }

        String key2 = key.trim();
        if (key2.isEmpty()) {
            return null;
        }

        EnvProperties envProperties = getEnvProperties();
        if (envProperties != null) {
            return envProperties.getProperty(key2);
        }

        return null;
    }

    public static boolean getBoolProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            return false;
        }

        return Boolean.parseBoolean(value);
    }

    public static int getIntProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            return 0;
        }

        int i = 0;

        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            i = 0;
        }

        return i;
    }

    public static double getDoubleProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            return 0;
        }

        double x = 0.0;

        try {
            x = Calculator.expr(value);
        } catch (NumberFormatException e) {
            x = 0.0;
        }

        return x;
    }

    public static void setProperty(String key, String value) {
        if (key == null) {
            return;
        }

        String key2 = key.trim();
        if (key2.isEmpty()) {
            return;
        }

        EnvProperties envProperties = getEnvProperties();
        if (envProperties != null) {
            envProperties.setProperty(key2, value);
        }
    }

    public static void setProperty(String key, boolean b) {
        setProperty(key, Boolean.toString(b));
    }

    public static void setProperty(String key, int i) {
        setProperty(key, Integer.toString(i));
    }

    public static void setProperty(String key, double d) {
        setProperty(key, Double.toString(d));
    }
}
