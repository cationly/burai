/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.run;

import java.util.ArrayList;
import java.util.List;

import burai.com.env.Environments;
import burai.input.QEInput;
import burai.input.namelist.QENamelist;
import burai.project.Project;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;
import burai.run.parser.FermiParser;
import burai.run.parser.GeometryParser;
import burai.run.parser.LogParser;
import burai.run.parser.ScfParser;
import burai.run.parser.VoidParser;

public enum RunningType {
    SCF("SCF", Project.INPUT_MODE_SCF),
    OPTIMIZ("Optimize", Project.INPUT_MODE_OPTIMIZ),
    MD("MD", Project.INPUT_MODE_MD),
    DOS("DOS", Project.INPUT_MODE_DOS),
    BAND("Band", Project.INPUT_MODE_BAND);

    private static final String VAR_PROC = "$NP";
    private static final String VAR_INPUT = "$IN";

    private static final String PROP_KEY_MPIRUN = "command_mpirun";
    private static final String PROP_KEY_PWSCF = "command_pwscf";
    private static final String PROP_KEY_DOS = "command_dos";
    private static final String PROP_KEY_PROJWFC = "command_projwfc";
    private static final String PROP_KEY_BAND = "command_band";

    private String label;

    private int inputMode;

    private RunningType(String label, int inputMode) {
        this.label = label;
        this.inputMode = inputMode;
    }

    @Override
    public String toString() {
        return label;
    }

    public static RunningType getRunningType(Project project) {
        if (project == null) {
            return null;
        }

        int inputMode = project.getInputMode();

        switch (inputMode) {
        case Project.INPUT_MODE_SCF:
            return SCF;

        case Project.INPUT_MODE_OPTIMIZ:
            return OPTIMIZ;

        case Project.INPUT_MODE_MD:
            return MD;

        case Project.INPUT_MODE_DOS:
            return DOS;

        case Project.INPUT_MODE_BAND:
            return BAND;

        default:
            return null;
        }
    }

    public QEInput getQEInput(Project project) {
        if (project == null) {
            return null;
        }

        QEInput srcInput = null;
        QEInput dstInput = null;

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            srcInput = project.getQEInputScf();
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            srcInput = project.getQEInputOptimiz();
            break;

        case Project.INPUT_MODE_MD:
            srcInput = project.getQEInputMd();
            break;

        case Project.INPUT_MODE_DOS:
            srcInput = project.getQEInputDos();
            break;

        case Project.INPUT_MODE_BAND:
            srcInput = project.getQEInputBand();
            break;

        default:
            srcInput = null;
            break;
        }

        if (srcInput != null) {
            dstInput = srcInput.copy();
        }

        if (dstInput != null) {
            this.modifyQEInput(dstInput, project);
        }

        return dstInput;
    }

    private void modifyQEInput(QEInput input, Project project) {
        if (input == null) {
            return;
        }

        if (project == null) {
            return;
        }

        String fileName = project.getRelatedFileName();
        fileName = fileName == null ? null : fileName.trim();

        String prefix = project.getPrefixName();
        prefix = prefix == null ? null : prefix.trim();

        QENamelist nmlControl = input.getNamelist(QEInput.NAMELIST_CONTROL);
        if (nmlControl != null) {
            if (fileName != null && (!fileName.isEmpty())) {
                nmlControl.setValue("title = '" + fileName + "(" + this.label + ")'");
            }

            if (prefix != null && (!prefix.isEmpty())) {
                nmlControl.setValue("prefix = '" + prefix + "'");
            }

            if (nmlControl.getValue("wf_collect") == null) {
                nmlControl.setValue("wf_collect = .TRUE.");
            }

            nmlControl.setValue("outdir = ./");
            nmlControl.setValue("wfcdir = ./");
            nmlControl.setValue("pseudo_dir = '" + Environments.getPseudosPath() + "'");
        }

        QENamelist nmlDos = input.getNamelist(QEInput.NAMELIST_DOS);
        if (nmlDos != null) {
            if (prefix != null && (!prefix.isEmpty())) {
                nmlDos.setValue("prefix = '" + prefix + "'");
                nmlDos.setValue("fildos = '" + prefix + ".dos'");
            }

            nmlDos.setValue("outdir = ./");
        }

        QENamelist nmlProjwfc = input.getNamelist(QEInput.NAMELIST_PROJWFC);
        if (nmlProjwfc != null) {
            if (prefix != null && (!prefix.isEmpty())) {
                nmlProjwfc.setValue("prefix = '" + prefix + "'");
                nmlProjwfc.setValue("filpdos = '" + prefix + "'");
            }

            nmlProjwfc.setValue("outdir = ./");
        }

        QENamelist nmlBand = input.getNamelist(QEInput.NAMELIST_BANDS);
        if (nmlBand != null) {
            if (prefix != null && (!prefix.isEmpty())) {
                nmlBand.setValue("prefix = '" + prefix + "'");
                nmlBand.setValue("filband = '" + prefix + ".band1'");
                nmlBand.setValue("spin_component = 1");
            }

            nmlBand.setValue("outdir = ./");
        }
    }

    public List<String[]> getCommandList(String fileName) {
        return this.getCommandList(fileName, 1);
    }

    public List<String[]> getCommandList(String fileName, int numProc) {
        String fileName2 = fileName == null ? null : fileName.trim();
        if (fileName2 == null || fileName2.isEmpty()) {
            return null;
        }

        int numProc2 = Math.max(1, numProc);

        String[] command = null;
        List<String[]> commandList = new ArrayList<String[]>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
        case Project.INPUT_MODE_OPTIMIZ:
        case Project.INPUT_MODE_MD:
            command = this.createCommand(PROP_KEY_PWSCF, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            break;

        case Project.INPUT_MODE_DOS:
            command = this.createCommand(PROP_KEY_PWSCF, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            command = this.createCommand(PROP_KEY_DOS, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            command = this.createCommand(PROP_KEY_PROJWFC, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            break;

        case Project.INPUT_MODE_BAND:
            command = this.createCommand(PROP_KEY_PWSCF, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            command = this.createCommand(PROP_KEY_BAND, fileName2, numProc2);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            break;

        default:
            // NOP
            break;
        }

        return commandList;
    }

    private String[] createCommand(String propKey, String fileName, int numProc) {
        if (propKey == null) {
            return null;
        }

        if (fileName == null) {
            return null;
        }

        String strMain = Environments.getProperty(propKey);
        if (strMain == null) {
            return null;
        }

        String strMPI = numProc < 2 ? null : Environments.getProperty(PROP_KEY_MPIRUN);
        if (strMPI != null) {
            strMain = strMPI + " " + strMain;
        }

        String[] command = strMain.trim().split("\\s+");
        if (command != null) {
            for (int i = 0; i < command.length; i++) {
                if (VAR_PROC.equals(command[i])) {
                    command[i] = Integer.toString(numProc);
                } else if (VAR_INPUT.equals(command[i])) {
                    command[i] = fileName;
                }
            }
        }

        return command;
    }

    public List<LogParser> getParserList(Project project) {
        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        List<LogParser> parserList = new ArrayList<LogParser>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            parserList.add(new ScfParser(projectProperty));
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            parserList.add(new GeometryParser(projectProperty, false));
            break;

        case Project.INPUT_MODE_MD:
            parserList.add(new GeometryParser(projectProperty, true));
            break;

        case Project.INPUT_MODE_DOS:
            parserList.add(new FermiParser(projectProperty));
            parserList.add(new VoidParser(projectProperty));
            parserList.add(new VoidParser(projectProperty));
            break;

        case Project.INPUT_MODE_BAND:
            parserList.add(new FermiParser(projectProperty));
            parserList.add(new VoidParser(projectProperty));
            break;

        default:
            // NOP
            break;
        }

        return parserList;
    }

    public void setProjectStatus(Project project) {
        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return;
        }

        ProjectStatus projectStatus = projectProperty.getStatus();
        if (projectStatus == null) {
            return;
        }

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            projectStatus.setScfDone(true);
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            projectStatus.setOptDone(true);
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_MD:
            projectStatus.setMdDone(true);
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_DOS:
            projectStatus.setDosDone(true);
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_BAND:
            projectStatus.setBandDone(true);
            projectProperty.saveStatus();
            break;
        }
    }
}
