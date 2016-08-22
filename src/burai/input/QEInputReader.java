/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import burai.com.str.SmartSplitter;
import burai.input.card.QECard;
import burai.input.namelist.QENamelist;

public class QEInputReader {

    private static final char[] COMMENT_CHARS = { '!', '#' };
    private static final char[] LINE_SEPARATORS = { '\n', '\r', '\f', ';' };
    private static final char[] WORD_SEPARATORS = { '\n', '\r', '\f', ';', '=', ':', ' ', ',' };

    private File inputFile;

    private List<String> linesOfFile;

    private boolean readingNamelist;

    public QEInputReader() {
        this.inputFile = null;
        this.initFileStatus();
    }

    public QEInputReader(String inputName) {
        this(inputName == null || inputName.isEmpty() ? null : new File(inputName));
    }

    public QEInputReader(File inputFile) {
        if (inputFile == null) {
            throw new IllegalArgumentException("inputFile is null.");
        }

        this.inputFile = inputFile;
        this.initFileStatus();
    }

    public void readNamelist(QENamelist namelist) throws IOException {
        if (namelist == null) {
            return;
        }

        if (this.linesOfFile == null || this.linesOfFile.isEmpty()) {
            this.loadInputFile();
        }

        namelist.read(this.linesOfFile);
    }

    public void readCard(QECard card) throws IOException {
        if (card == null) {
            return;
        }

        if (this.linesOfFile == null || this.linesOfFile.isEmpty()) {
            this.loadInputFile();
        }

        card.read(this.linesOfFile);
    }

    private void initFileStatus() {
        this.linesOfFile = null;
        this.readingNamelist = false;
    }

    private void loadInputFile() throws IOException {
        if (this.inputFile == null) {
            this.initFileStatus();
            throw new IOException("input file is null.");
        }

        if (!this.inputFile.isFile()) {
            this.initFileStatus();
            throw new IOException(this.inputFile.toString() + " : no such file.");
        }

        if (this.linesOfFile == null) {
            this.linesOfFile = new ArrayList<String>();
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(this.inputFile));

            this.readingNamelist = false;

            String line = null;
            while ((line = reader.readLine()) != null) {
                this.parseLine(line);
            }

        } catch (IOException e1) {
            this.initFileStatus();
            throw e1;

        } finally {
            this.readingNamelist = false;

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {
                    this.initFileStatus();
                    throw e2;
                }
            }
        }

        if (this.linesOfFile == null || this.linesOfFile.isEmpty()) {
            this.initFileStatus();
            throw new IOException(this.inputFile.toString() + " is empty.");
        }
    }

    public void appendInputData(String inputData) {
        if (inputData == null || inputData.trim().isEmpty()) {
            return;
        }

        if (this.linesOfFile == null) {
            this.linesOfFile = new ArrayList<String>();
        }

        this.readingNamelist = false;

        String[] lines = inputData.split("[\\n\\r\\f]+");
        for (String line : lines) {
            this.parseLine(line);
        }

        this.readingNamelist = false;

        if (this.linesOfFile == null || this.linesOfFile.isEmpty()) {
            this.initFileStatus();
        }
    }

    private String trimComment(String line) {
        String line2 = line.trim();
        if (line2.isEmpty()) {
            return "";
        }

        char head = line2.charAt(0);
        for (char commentChar : COMMENT_CHARS) {
            if (head == commentChar) {
                return "";
            }
        }

        String[] subLine = SmartSplitter.split(line2, false, COMMENT_CHARS);
        if (subLine == null || subLine.length < 1) {
            return "";
        }
        return subLine[0];
    }

    private void parseLine(String line) {
        String trimedLine = this.trimComment(line);
        if (trimedLine == null || trimedLine.isEmpty()) {
            return;
        }

        boolean onheadNamelist = false;
        boolean insideNamelist = false;

        if (!this.readingNamelist) {
            if (trimedLine.startsWith("&")) {
                this.readingNamelist = true;
                onheadNamelist = true;
                insideNamelist = true;
            }

        } else {
            if (trimedLine.startsWith("/") || trimedLine.toUpperCase().startsWith("&END")) {
                this.readingNamelist = false;
            } else {
                insideNamelist = true;
            }
        }

        if (!insideNamelist) {
            String[] subLines = SmartSplitter.split(trimedLine, false, LINE_SEPARATORS);
            for (String subLine : subLines) {
                if (subLine != null && !subLine.isEmpty()) {
                    this.linesOfFile.add(subLine);
                }
            }

        } else {
            String[] words = SmartSplitter.split(trimedLine, true, WORD_SEPARATORS);

            int ihead = onheadNamelist ? 1 : 0;
            if (ihead > 0) {
                if (words[0] != null && !words[0].isEmpty()) {
                    this.linesOfFile.add(words[0]);
                }
            }

            for (int i = ihead; i < words.length; i += 2) {
                String word1 = words[i];
                String word2 = null;
                if ((i + 1) < words.length) {
                    word2 = words[i + 1];
                }

                String subLine = null;
                if (word1 != null && !word1.isEmpty()) {
                    subLine = word1;
                    if (word2 != null && !word2.isEmpty()) {
                        subLine = subLine + " = " + word2;
                    }
                }

                if (subLine != null && !subLine.isEmpty()) {
                    this.linesOfFile.add(subLine);
                }
            }
        }
    }
}
