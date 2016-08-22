/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import burai.com.env.Environments;
import burai.project.Project;

public class FileLister {

    private static final String HEAD_TO_AVOID = ".";

    private QEFXExplorerBody explorerBody;

    private String[] fileNames;

    private Project[] fileProjects;

    protected FileLister(QEFXExplorerBody explorerBody) {
        if (explorerBody == null) {
            throw new IllegalArgumentException("explorerBody is null.");
        }

        this.explorerBody = explorerBody;
        this.fileNames = null;
        this.fileProjects = null;
    }

    protected String[] getFileNames() {
        return this.fileNames;
    }

    protected Project[] getFileProjects() {
        return this.fileProjects;
    }

    protected void list(List<Project> shownProjects) throws IOException {
        this.fileNames = null;
        this.fileProjects = null;
        this.createFileNames(shownProjects == null ? new ArrayList<Project>() : shownProjects);
    }

    private void createFileNames(List<Project> shownProjects) throws IOException {
        String directoryName = this.explorerBody.getDirectoryName();

        if (this.explorerBody.isExplorerMode()) {
            this.createFileNamesRegular(directoryName, shownProjects);

        } else if (this.explorerBody.isRecentlyUsedMode()) {
            this.createFileNamesRecentlyUsed(shownProjects);

        } else if (this.explorerBody.isCalculatingMode()) {
            this.createFileNamesCalculating(shownProjects);

        } else if (this.explorerBody.isSearchedMode()) {
            this.createFileNamesSearched(shownProjects);

        } else if (this.explorerBody.isWebMode()) {
            this.createFileNamesWeb();
        }
    }

    private void createFileNamesRegular(String directoryName, List<Project> shownProjects) throws IOException {
        File[] files = this.listFilesInDirectory(directoryName);
        this.setupFileProperties(files, shownProjects, false);
    }

    private void createFileNamesRecentlyUsed(List<Project> shownProjects) {
        File[] files = Environments.listRecentFiles();
        this.setupFileProperties(files, shownProjects, true);
    }

    private void createFileNamesCalculating(List<Project> shownProjects) {
        // NOP
    }

    private void createFileNamesSearched(List<Project> shownProjects) {
        // NOP
    }

    private void createFileNamesWeb() {
        this.fileNames = Environments.listWebsites();
        this.fileProjects = null;
    }

    private void setupFileProperties(File[] files, List<Project> shownProjects, boolean asPath) {
        if (files == null || files.length < 1) {
            this.fileNames = null;
            this.fileProjects = null;
            return;
        }

        this.fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            if (asPath) {
                this.fileNames[i] = files[i].getPath();
            } else {
                this.fileNames[i] = files[i].getName();
            }
        }

        this.fileProjects = new Project[files.length];
        for (int i = 0; i < files.length; i++) {
            if (Project.isProjectFile(files[i].getPath())) {
                for (Project project : shownProjects) {
                    if (project != null && project.isRelatedFile(files[i])) {
                        this.fileProjects[i] = project;
                        break;
                    }
                }
            } else {
                this.fileProjects[i] = null;
            }
        }
    }

    private File[] listFilesInDirectory(String directoryName) throws IOException {
        File[] files = null;

        try {
            File directoryFile = new File(directoryName);
            if ((!directoryFile.exists()) || (!directoryFile.isDirectory())) {
                throw new IOException(directoryName + ": not a directory.");
            }

            files = directoryFile.listFiles();
            if (files == null) {
                throw new IOException(directoryName + ": cannot list files.");
            }

            List<File> fileList = new ArrayList<File>();
            for (File file : files) {
                String fileName = file == null ? null : file.getName();
                if (fileName != null && (!fileName.startsWith(HEAD_TO_AVOID))) {
                    fileList.add(file);
                }
            }

            if (files.length > fileList.size()) {
                files = fileList.toArray(new File[fileList.size()]);
            }

            if (files.length > 1) {
                Arrays.sort(files, (file1, file2) -> {
                    if (file1.isDirectory()) {
                        if (file2.isDirectory()) {
                            return file1.compareTo(file2);
                        } else {
                            return -1;
                        }
                    } else {
                        if (file2.isDirectory()) {
                            return 1;
                        } else {
                            return file1.compareTo(file2);
                        }
                    }
                });
            }

        } catch (IOException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);
        }

        return files;
    }
}
