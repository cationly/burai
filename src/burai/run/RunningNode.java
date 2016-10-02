/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.run;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import burai.com.file.FileTools;
import burai.input.QEInput;
import burai.project.Project;
import burai.run.parser.LogParser;

public class RunningNode implements Runnable {

    private static final RunningType DEFAULT_TYPE = RunningType.SCF;

    private boolean alive;

    private Project project;

    private RunningStatus status;

    private List<RunningStatusChanged> onStatusChangedList;

    private RunningType type;

    private int numProcesses;

    private int numThreads;

    private Process objProcess;

    public RunningNode(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        this.alive = true;

        this.project = project;

        this.status = RunningStatus.IDLE;
        this.onStatusChangedList = null;

        this.type = null;
        this.numProcesses = 1;
        this.numThreads = 1;

        this.objProcess = null;
    }

    public Project getProject() {
        return this.project;
    }

    public synchronized RunningStatus getStatus() {
        return this.status;
    }

    protected synchronized void setStatus(RunningStatus status) {
        if (status == null) {
            return;
        }

        this.status = status;

        if (this.onStatusChangedList != null) {
            for (RunningStatusChanged onStatusChanged : this.onStatusChangedList) {
                if (onStatusChanged != null) {
                    onStatusChanged.onRunningStatusChanged(this.status);
                }
            }
        }
    }

    public synchronized void addOnStatusChanged(RunningStatusChanged onStatusChanged) {
        if (onStatusChanged != null) {
            if (this.onStatusChangedList == null) {
                this.onStatusChangedList = new ArrayList<RunningStatusChanged>();
            }

            this.onStatusChangedList.add(onStatusChanged);
        }
    }

    public synchronized void removeOnStatusChanged(RunningStatusChanged onStatusChanged) {
        if (onStatusChanged != null) {
            if (this.onStatusChangedList != null) {
                this.onStatusChangedList.remove(onStatusChanged);
            }
        }
    }

    public synchronized RunningType getType() {
        return this.type;
    }

    public synchronized void setType(RunningType type) {
        this.type = type;
    }

    public synchronized int getNumProcesses() {
        return this.numProcesses;
    }

    public synchronized void setNumProcesses(int numProcesses) {
        this.numProcesses = numProcesses;
    }

    public synchronized int getNumThreads() {
        return this.numThreads;
    }

    public synchronized void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public synchronized void stop() {
        this.alive = false;

        if (this.objProcess != null) {
            this.objProcess.destroy();
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            if (!this.alive) {
                return;
            }
        }

        File directory = this.getDirectory();
        if (directory == null) {
            return;
        }

        RunningType type2 = null;
        int numProcesses2 = -1;
        int numThreads2 = -1;

        synchronized (this) {
            type2 = this.type;
            numProcesses2 = this.numProcesses;
            numThreads2 = this.numThreads;
        }

        if (type2 == null) {
            type2 = DEFAULT_TYPE;
        }
        if (numProcesses2 < 1) {
            numProcesses2 = 1;
        }
        if (numThreads2 < 1) {
            numThreads2 = 1;
        }

        QEInput input = new FXQEInputFactory(type2).getQEInput(this.project);
        if (input == null) {
            return;
        }

        String inpName = this.project.getInpFileName();
        inpName = inpName == null ? null : inpName.trim();
        File inpFile = (inpName == null || inpName.isEmpty()) ? null : new File(directory, inpName);
        if (inpFile == null) {
            return;
        }

        List<String[]> commandList = type2.getCommandList(inpName, numProcesses2);
        if (commandList == null || commandList.isEmpty()) {
            return;
        }

        List<RunningCondition> conditionList = type2.getConditionList();
        if (conditionList == null || conditionList.size() < commandList.size()) {
            return;
        }

        List<InputEditor> inputEditorList = type2.getInputEditorList(this.project);
        if (inputEditorList == null || inputEditorList.size() < commandList.size()) {
            return;
        }

        List<LogParser> parserList = type2.getParserList(this.project);
        if (parserList == null || parserList.size() < commandList.size()) {
            return;
        }

        List<PostOperation> postList = type2.getPostList();
        if (postList == null || postList.size() < commandList.size()) {
            return;
        }

        this.deleteLogFiles(directory);

        int iCommand = 0;
        boolean errOccurred = false;

        for (int i = 0; i < commandList.size(); i++) {
            synchronized (this) {
                if (!this.alive) {
                    return;
                }
            }

            String[] command = commandList.get(i);
            if (command == null || command.length < 1) {
                continue;
            }

            RunningCondition condition = conditionList.get(i);
            if (condition == null) {
                continue;
            }

            InputEditor inputEditor = inputEditorList.get(i);
            if (inputEditor == null) {
                continue;
            }

            LogParser parser = parserList.get(i);
            if (parser == null) {
                continue;
            }

            PostOperation post = postList.get(i);
            if (post == null) {
                continue;
            }

            QEInput input2 = inputEditor.editInput(input);
            if (input2 == null) {
                continue;
            }

            if (!condition.toRun(this.project, input2)) {
                continue;
            }

            boolean inpStatus = this.writeQEInput(input2, inpFile);
            if (!inpStatus) {
                continue;
            }

            String logName = this.project.getLogFileName(iCommand);
            logName = logName == null ? null : logName.trim();
            File logFile = (logName == null || logName.isEmpty()) ? null : new File(directory, logName);
            if (logFile == null) {
                continue;
            }

            String errName = this.project.getErrFileName(iCommand);
            errName = errName == null ? null : errName.trim();
            File errFile = (errName == null || errName.isEmpty()) ? null : new File(directory, errName);
            if (errFile == null) {
                continue;
            }

            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(directory);
            builder.command(command);
            builder.environment().put("OMP_NUM_THREADS", Integer.toString(numThreads2));
            builder.redirectOutput(logFile);
            builder.redirectError(errFile);

            try {
                synchronized (this) {
                    this.objProcess = builder.start();
                }

                parser.startParsing(logFile);

                if (this.objProcess != null) {
                    if (this.objProcess.waitFor() != 0) {
                        errOccurred = true;
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                errOccurred = true;
                break;

            } finally {
                synchronized (this) {
                    this.objProcess = null;
                }

                parser.endParsing();
            }

            if (!errOccurred) {
                post.operate(this.project);
            }

            iCommand++;
        }

        if (!errOccurred) {
            type2.setProjectStatus(this.project);
        }
    }

    private File getDirectory() {
        String dirPath = this.project.getDirectoryPath();
        if (dirPath == null) {
            return null;
        }

        File dirFile = new File(dirPath);
        try {
            if (!dirFile.isDirectory()) {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return dirFile;
    }

    private boolean writeQEInput(QEInput input, File file) {
        if (input == null) {
            return false;
        }

        if (file == null) {
            return false;
        }

        String strInput = input.toString();
        if (strInput == null) {
            return false;
        }

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            writer.println(strInput);

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        return true;
    }

    private void deleteLogFiles(File directory) {
        if (directory == null) {
            return;
        }

        final int maxIndex = 9;

        for (int i = 0; true; i++) {
            String logName = this.project.getLogFileName(i);
            logName = logName == null ? null : logName.trim();
            if (logName == null || logName.isEmpty()) {
                continue;
            }

            boolean status = false;
            try {
                File logFile = new File(directory, logName);
                if (logFile.exists()) {
                    status = FileTools.deleteAllFiles(logFile, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ((!status) && (i > maxIndex)) {
                break;
            }
        }

        for (int i = 0; true; i++) {
            String errName = this.project.getErrFileName(i);
            errName = errName == null ? null : errName.trim();
            if (errName == null || errName.isEmpty()) {
                continue;
            }

            boolean status = false;
            try {
                File errFile = new File(directory, errName);
                if (errFile.exists()) {
                    status = FileTools.deleteAllFiles(errFile, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ((!status) && (i > maxIndex)) {
                break;
            }
        }

        String exitName = this.project.getExitFileName();
        exitName = exitName == null ? null : exitName.trim();
        if (exitName != null && (!exitName.isEmpty())) {
            try {
                File exitFile = new File(directory, exitName);
                if (exitFile.exists()) {
                    FileTools.deleteAllFiles(exitFile, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
