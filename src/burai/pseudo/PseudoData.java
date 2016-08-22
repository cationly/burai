/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.pseudo;

import java.io.File;

public class PseudoData {

    public static final int UPF_VERSION_UNKNOWN = 0;
    public static final int UPF_VERSION_1 = 1;
    public static final int UPF_VERSION_2 = 2;

    public static final int PSEUDO_TYPE_UNKNOWN = 0;
    public static final int PSEUDO_TYPE_NC = 1;
    public static final int PSEUDO_TYPE_US = 2;
    public static final int PSEUDO_TYPE_PAW = 3;
    public static final int PSEUDO_TYPE_COULOMB = 4;

    public static final int RELATIVISTIC_UNKNOWN = 0;
    public static final int RELATIVISTIC_NO = 1;
    public static final int RELATIVISTIC_SCALAR = 2;
    public static final int RELATIVISTIC_FULL = 3;

    public static final int FUNCTIONAL_UNKNOWN = 0;
    public static final int FUNCTIONAL_PZ = 1;
    public static final int FUNCTIONAL_PW91 = 2;
    public static final int FUNCTIONAL_PBE = 3;
    public static final int FUNCTIONAL_REVPBE = 4;
    public static final int FUNCTIONAL_PBESOL = 5;
    public static final int FUNCTIONAL_BLYP = 6;

    private static final long INIT_TIME_STAMP = 0L;

    private long upfTimeStamp;

    private int upfVersion;

    private String generated;

    private String author;

    private String date;

    private String comment;

    private String element;

    private int pseudoType;

    private int relativistic;

    private boolean ultrasoft;

    private boolean paw;

    private boolean coulomb;

    private boolean withSo;

    private boolean withWfc;

    private boolean withGipaw;

    private boolean pawAsGipaw;

    private boolean coreCorrection;

    private int functional;

    private double zValence;

    private double totalPsenergy;

    private double wfcCutoff;

    private double rhoCutoff;

    private int lMax;

    private int lMaxRho;

    private int lLocal;

    private int meshSize;

    private int numberOfWfc;

    private int numberOfProj;

    public PseudoData() {
        this.upfTimeStamp = INIT_TIME_STAMP;
        this.upfVersion = UPF_VERSION_UNKNOWN;
        this.generated = null;
        this.author = null;
        this.date = null;
        this.comment = null;
        this.element = null;
        this.pseudoType = PSEUDO_TYPE_UNKNOWN;
        this.relativistic = RELATIVISTIC_UNKNOWN;
        this.ultrasoft = false;
        this.paw = false;
        this.coulomb = false;
        this.withSo = false;
        this.withWfc = false;
        this.withGipaw = false;
        this.pawAsGipaw = false;
        this.coreCorrection = false;
        this.functional = FUNCTIONAL_UNKNOWN;
        this.zValence = 0.0;
        this.totalPsenergy = 0.0;
        this.wfcCutoff = 0.0;
        this.rhoCutoff = 0.0;
        this.lMax = 0;
        this.lMaxRho = 0;
        this.lLocal = 0;
        this.meshSize = 0;
        this.numberOfWfc = 0;
        this.numberOfProj = 0;
    }

    public long getUpfTimeStamp() {
        return this.upfTimeStamp;
    }

    protected void setUpfTimeStamp(long upfTimeStamp) {
        this.upfTimeStamp = upfTimeStamp;
    }

    protected boolean setUpfTimeStamp(File upfFile) {
        if (upfFile == null) {
            return false;
        }

        long upfTimeStamp2 = INIT_TIME_STAMP;

        try {
            upfTimeStamp2 = upfFile.lastModified();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (this.upfTimeStamp != INIT_TIME_STAMP && this.upfTimeStamp == upfTimeStamp2) {
            return false;
        }

        this.upfTimeStamp = upfTimeStamp2;
        return true;
    }

    protected void initUpfTimeStamp() {
        this.upfTimeStamp = INIT_TIME_STAMP;
    }

    public int getUpfVersion() {
        return this.upfVersion;
    }

    protected void setUpfVersion(int upfVersion) {
        this.upfVersion = upfVersion;
    }

    public String getGenerated() {
        return this.generated;
    }

    protected void setGenerated(String generated) {
        this.generated = generated;
    }

    public String getAuthor() {
        return this.author;
    }

    protected void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return this.date;
    }

    protected void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return this.comment;
    }

    protected void setComment(String comment) {
        this.comment = comment;
    }

    public String getElement() {
        return this.element;
    }

    protected void setElement(String element) {
        this.element = element;
    }

    public int getPseudoType() {
        return this.pseudoType;
    }

    public String getPseudoTypeName() {
        switch (this.pseudoType) {
        case PSEUDO_TYPE_NC:
            return "Norm-Conserving";
        case PSEUDO_TYPE_US:
            return "Ultrasoft";
        case PSEUDO_TYPE_PAW:
            return "PAW";
        case PSEUDO_TYPE_COULOMB:
            return "1/r";
        default:
            return "unknown";
        }
    }

    public boolean hasPseudoType() {
        return this.pseudoType != PSEUDO_TYPE_UNKNOWN;
    }

    protected void setPseudoType(int pseudoType) {
        this.pseudoType = pseudoType;
    }

    protected void setPseudoType(String str) {
        if ("NC".equalsIgnoreCase(str)) {
            this.pseudoType = PSEUDO_TYPE_NC;

        } else if ("NCPP".equalsIgnoreCase(str)) {
            this.pseudoType = PSEUDO_TYPE_NC;

        } else if ("US".equalsIgnoreCase(str)) {
            this.pseudoType = PSEUDO_TYPE_US;

        } else if ("USPP".equalsIgnoreCase(str)) {
            this.pseudoType = PSEUDO_TYPE_US;

        } else if ("PAW".equalsIgnoreCase(str)) {
            this.pseudoType = PSEUDO_TYPE_PAW;

        } else if ("1/r".equalsIgnoreCase(str)) {
            this.pseudoType = PSEUDO_TYPE_COULOMB;

        } else {
            this.pseudoType = PSEUDO_TYPE_UNKNOWN;
        }
    }

    public int getRelativistic() {
        return this.relativistic;
    }

    public String getRelativisticName() {
        switch (this.relativistic) {
        case RELATIVISTIC_NO:
            return "No";
        case RELATIVISTIC_SCALAR:
            return "Scalar";
        case RELATIVISTIC_FULL:
            return "Full";
        default:
            return "unknown";
        }
    }

    public boolean hasRelativistic() {
        return this.relativistic != RELATIVISTIC_UNKNOWN;
    }

    protected void setRelativistic(int relativistic) {
        this.relativistic = relativistic;
    }

    protected void setRelativistic(String str) {
        if ("NO".equalsIgnoreCase(str)) {
            this.relativistic = RELATIVISTIC_NO;

        } else if ("SCALAR".equalsIgnoreCase(str)) {
            this.relativistic = RELATIVISTIC_SCALAR;

        } else if ("FULL".equalsIgnoreCase(str)) {
            this.relativistic = RELATIVISTIC_FULL;

        } else {
            this.relativistic = RELATIVISTIC_UNKNOWN;
        }
    }

    public boolean isUltrasoft() {
        return this.ultrasoft;
    }

    protected void setUltrasoft(boolean ultrasoft) {
        this.ultrasoft = ultrasoft;
    }

    public boolean isPaw() {
        return this.paw;
    }

    protected void setPaw(boolean paw) {
        this.paw = paw;
    }

    public boolean isCoulomb() {
        return this.coulomb;
    }

    protected void setCoulomb(boolean coulomb) {
        this.coulomb = coulomb;
    }

    public boolean isWithSo() {
        return this.withSo;
    }

    protected void setWithSo(boolean withSo) {
        this.withSo = withSo;
    }

    public boolean isWithWfc() {
        return this.withWfc;
    }

    protected void setWithWfc(boolean withWfc) {
        this.withWfc = withWfc;
    }

    public boolean isWithGipaw() {
        return this.withGipaw;
    }

    protected void setWithGipaw(boolean withGipaw) {
        this.withGipaw = withGipaw;
    }

    public boolean isPawAsGipaw() {
        return this.pawAsGipaw;
    }

    protected void setPawAsGipaw(boolean pawAsGipaw) {
        this.pawAsGipaw = pawAsGipaw;
    }

    public boolean isCoreCorrection() {
        return this.coreCorrection;
    }

    protected void setCoreCorrection(boolean coreCorrection) {
        this.coreCorrection = coreCorrection;
    }

    public int getFunctional() {
        return this.functional;
    }

    public String getFunctionalName() {
        switch (this.functional) {
        case FUNCTIONAL_PZ:
            return "PZ";
        case FUNCTIONAL_PW91:
            return "PW91";
        case FUNCTIONAL_PBE:
            return "PBE";
        case FUNCTIONAL_REVPBE:
            return "revPBE";
        case FUNCTIONAL_PBESOL:
            return "PBEsol";
        case FUNCTIONAL_BLYP:
            return "BLYP";
        default:
            return "unknown";
        }
    }

    public boolean hasFunctional() {
        return this.functional != FUNCTIONAL_UNKNOWN;
    }

    protected void setFunctional(int functional) {
        this.functional = functional;
    }

    protected void setFunctional(String str) {
        if (str == null || str.isEmpty()) {
            this.functional = FUNCTIONAL_UNKNOWN;
            return;
        }

        String[] subStr = str.trim().split("[\\s\\-,]+");
        if (subStr == null || subStr.length < 1) {
            this.functional = FUNCTIONAL_UNKNOWN;
            return;
        }

        if (subStr.length == 1) {
            this.setFunctional1(subStr[0]);
            return;
        }

        String nameExch = (subStr.length > 0 && subStr[0] != null) ? subStr[0].trim() : "NOX";
        String nameCorr = (subStr.length > 1 && subStr[1] != null) ? subStr[1].trim() : "NOC";
        String nameGrdX = (subStr.length > 2 && subStr[2] != null) ? subStr[2].trim() : "NOGX";
        String nameGrdC = (subStr.length > 3 && subStr[3] != null) ? subStr[3].trim() : "NOGC";

        if ("PBE".equalsIgnoreCase(nameGrdX)) {
            nameGrdX = "PBX";
        }
        if ("PBE".equalsIgnoreCase(nameGrdC)) {
            nameGrdC = "PBC";
        }

        this.setFunctional4(nameExch + " " + nameCorr + " " + nameGrdX + " " + nameGrdC);
    }

    private void setFunctional1(String name) {
        if ("LDA".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_PZ;

        } else if ("PZ".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_PZ;

        } else if ("PW91".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_PW91;

        } else if ("PBE".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_PBE;

        } else if ("REVPBE".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_REVPBE;

        } else if ("PBESOL".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_PBESOL;

        } else if ("BLYP".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_BLYP;

        } else {
            this.functional = FUNCTIONAL_UNKNOWN;
        }
    }

    private void setFunctional4(String name) {
        if ("SLA PZ NOGX NOGC".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_PZ;

        } else if ("SLA PW GGX GGC".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_PW91;

        } else if ("SLA PW PBX PBC".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_PBE;

        } else if ("SLA PW RPB PBC".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_REVPBE;

        } else if ("SLA PW PSX PSC".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_PBESOL;

        } else if ("SLA LYP B88 BLYP".equalsIgnoreCase(name)) {
            this.functional = FUNCTIONAL_BLYP;

        } else {
            this.functional = FUNCTIONAL_UNKNOWN;
        }
    }

    public double getZValence() {
        return this.zValence;
    }

    public void setZValence(double zValence) {
        this.zValence = zValence;
    }

    public double getTotalPsenergy() {
        return this.totalPsenergy;
    }

    public void setTotalPsenergy(double totalPsenergy) {
        this.totalPsenergy = totalPsenergy;
    }

    public double getWfcCutoff() {
        return this.wfcCutoff;
    }

    public void setWfcCutoff(double wfcCutoff) {
        this.wfcCutoff = wfcCutoff;
    }

    public double getRhoCutoff() {
        return this.rhoCutoff;
    }

    public void setRhoCutoff(double rhoCutoff) {
        this.rhoCutoff = rhoCutoff;
    }

    public int getLMax() {
        return this.lMax;
    }

    public void setLMax(int lMax) {
        this.lMax = lMax;
    }

    public int getLMaxRho() {
        return this.lMaxRho;
    }

    public void setLMaxRho(int lMaxRho) {
        this.lMaxRho = lMaxRho;
    }

    public int getLLocal() {
        return this.lLocal;
    }

    public void setLLocal(int lLocal) {
        this.lLocal = lLocal;
    }

    public int getMeshSize() {
        return this.meshSize;
    }

    public void setMeshSize(int meshSize) {
        this.meshSize = meshSize;
    }

    public int getNumberOfWfc() {
        return this.numberOfWfc;
    }

    public void setNumberOfWfc(int numberOfWfc) {
        this.numberOfWfc = numberOfWfc;
    }

    public int getNumberOfProj() {
        return this.numberOfProj;
    }

    public void setNumberOfProj(int numberOfProj) {
        this.numberOfProj = numberOfProj;
    }
}
