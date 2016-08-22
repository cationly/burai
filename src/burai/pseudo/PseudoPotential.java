/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.pseudo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBase;

public class PseudoPotential implements Comparable<PseudoPotential> {

    private boolean available;

    private File upfFile;

    private PseudoData data;

    public PseudoPotential(String upfPath) {
        this(upfPath == null ? null : new File(upfPath));
    }

    public PseudoPotential(File upfFile) {
        this(upfFile, null);
    }

    public PseudoPotential(File upfFile, PseudoData data) {
        if (upfFile == null) {
            throw new IllegalArgumentException("upfFile is null.");
        }

        this.upfFile = upfFile;

        if (data != null) {
            this.available = true;
            this.data = data;

        } else {
            this.available = false;
            this.data = new PseudoData();
        }

        this.reload();
    }

    public boolean isAvairable() {
        return this.available;
    }

    public File getFile() {
        return this.upfFile;
    }

    public String getPath() {
        return this.upfFile.getPath();
    }

    public String getName() {
        return this.upfFile.getName();
    }

    public boolean exists() {
        return this.upfFile.exists();
    }

    public PseudoData getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return this.upfFile.toString();
    }

    @Override
    public int hashCode() {
        return this.upfFile.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PseudoPotential) {
            return this.upfFile.equals(((PseudoPotential) obj).upfFile);
        }

        return false;
    }

    @Override
    public int compareTo(PseudoPotential other) {
        String name1 = this.getName();
        String name2 = other == null ? null : other.getName();
        return name1 == null ? 1 : name1.compareTo(name2);
    }

    public boolean reload() {
        if (this.data.setUpfTimeStamp(this.upfFile)) {
            try {
                this.available = true;
                this.readUpfFile();
                return true;

            } catch (IOException e) {
                System.err.println("Cannot read: " + this.upfFile);
                e.printStackTrace();
                this.available = false;
            }
        }

        return false;
    }

    private void readUpfFile() throws IOException {

        InputStream inputStream = null;

        try {
            inputStream = this.getUpfInputStream();
            if (inputStream == null) {
                throw new IOException("cannot create InputStream.");
            }

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = null;
            if (documentFactory != null) {
                documentBuilder = documentFactory.newDocumentBuilder();
            }

            Document document = null;
            if (documentBuilder != null) {
                document = documentBuilder.parse(inputStream);
            }

            Node rootNode = null;
            if (document != null) {
                rootNode = document.getDocumentElement();
            }

            if (rootNode == null) {
                throw new IOException("cannot create DocumentElement.");
            }

            Node firstNode = null;
            if (rootNode.hasChildNodes()) {
                NodeList nodeList = rootNode.getChildNodes();
                if (nodeList != null) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            firstNode = node;
                            break;
                        }
                    }
                }
            }

            if (firstNode == null) {
                throw new IOException("DocumentElement does not have ChildNodes.");
            }

            String firstName = firstNode.getNodeName();
            if (!"UPF".equals(firstName)) {
                this.data.setUpfVersion(PseudoData.UPF_VERSION_1);
                this.parseUpf(false, rootNode);

            } else {
                this.data.setUpfVersion(PseudoData.UPF_VERSION_2);
                this.parseUpf(true, firstNode);
            }

        } catch (IOException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }
    }

    private InputStream getUpfInputStream() throws IOException {
        InputStream inputStream = null;

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(this.upfFile));

            StringBuilder strBuilder = new StringBuilder("<root>");

            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.replace('&', '#');
                strBuilder.append(line);
                strBuilder.append(System.lineSeparator());
            }

            strBuilder.append("</root>");

            String str = strBuilder.toString();
            if (str != null) {
                byte[] strBytes = str.getBytes();
                if (strBytes != null && strBytes.length > 0) {
                    inputStream = new ByteArrayInputStream(strBytes);
                }
            }

        } catch (FileNotFoundException e1) {
            throw e1;

        } catch (IOException e2) {
            throw e2;

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }

        return inputStream;
    }

    private void parseUpf(boolean isUPFv2, Node rootNode) {
        if (rootNode == null || !rootNode.hasChildNodes()) {
            throw new IllegalArgumentException("rootNode is incorrect.");
        }

        NodeList nodeList = rootNode.getChildNodes();
        if (nodeList == null) {
            return;
        }

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node == null) {
                continue;
            }

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String name = node.getNodeName();
                if ("PP_INFO".equals(name)) {
                    if (!isUPFv2) {
                        this.parseUpfV1PPInfo(node);
                    } else {
                        this.parseUpfV2PPInfo(node);
                    }

                } else if ("PP_HEADER".equals(name)) {
                    if (!isUPFv2) {
                        this.parseUpfV1PPHeader(node);
                    } else {
                        this.parseUpfV2PPHeader(node);
                    }
                }
            }
        }
    }

    private void parseUpfV1PPInfo(Node node) {
        if (node == null) {
            return;
        }

        // NOP
    }

    private void parseUpfV2PPInfo(Node node) {
        if (node == null) {
            return;
        }

        // NOP
    }

    private void parseUpfV1PPHeader(Node node) {
        if (node == null) {
            return;
        }

        String content = node.getTextContent();
        if (content == null || content.isEmpty()) {
            return;
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new StringReader(content));

            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    break;
                }
            }

            line = reader.readLine();
            if (line != null) {
                String[] subLines = line.trim().split("[\\s]+");
                if (subLines.length > 0) {
                    this.data.setElement(subLines[0]);
                }
            }

            line = reader.readLine();
            if (line != null) {
                String[] subLines = line.trim().split("[\\s]+");
                if (subLines.length > 0) {
                    this.data.setPseudoType(subLines[0]);
                    int pseudoType = this.data.getPseudoType();

                    if (pseudoType == PseudoData.PSEUDO_TYPE_NC) {
                        this.data.setUltrasoft(false);
                        this.data.setPaw(false);
                        this.data.setCoulomb(false);

                    } else if (pseudoType == PseudoData.PSEUDO_TYPE_US) {
                        this.data.setUltrasoft(true);
                        this.data.setPaw(false);
                        this.data.setCoulomb(false);

                    } else if (pseudoType == PseudoData.PSEUDO_TYPE_PAW) {
                        this.data.setUltrasoft(true);
                        this.data.setPaw(true);
                        this.data.setCoulomb(false);

                    } else if (pseudoType == PseudoData.PSEUDO_TYPE_COULOMB) {
                        this.data.setUltrasoft(false);
                        this.data.setPaw(false);
                        this.data.setCoulomb(true);
                    }
                }
            }

            line = reader.readLine();
            if (line != null) {
                String[] subLines = line.trim().split("[\\s]+");
                if (subLines.length > 0) {
                    QEValue value = QEValueBase.getInstance("x", subLines[0]);
                    if (value != null) {
                        this.data.setCoreCorrection(value.getLogicalValue());
                    }
                }
            }

            line = reader.readLine();
            if (line != null) {
                String str = line.substring(0, Math.min(20, line.length()));
                this.data.setFunctional(str);
            }

            line = reader.readLine();
            if (line != null) {
                String[] subLines = line.trim().split("[\\s]+");
                if (subLines.length > 0) {
                    QEValue value = QEValueBase.getInstance("x", subLines[0]);
                    if (value != null) {
                        this.data.setZValence(value.getRealValue());
                    }
                }
            }

            line = reader.readLine();

            line = reader.readLine();
            if (line != null) {
                String[] subLines = line.trim().split("[\\s]+");
                if (subLines.length > 0) {
                    QEValue value = QEValueBase.getInstance("x", subLines[0]);
                    if (value != null) {
                        this.data.setWfcCutoff(value.getRealValue());
                    }
                }

                if (subLines.length > 1) {
                    QEValue value = QEValueBase.getInstance("x", subLines[1]);
                    if (value != null) {
                        this.data.setRhoCutoff(value.getRealValue());
                    }
                }
            }

            line = reader.readLine();
            if (line != null) {
                String[] subLines = line.trim().split("[\\s]+");
                if (subLines.length > 0) {
                    QEValue value = QEValueBase.getInstance("x", subLines[0]);
                    if (value != null) {
                        this.data.setLMax(value.getIntegerValue());
                    }
                }
            }

            line = reader.readLine();
            if (line != null) {
                String[] subLines = line.trim().split("[\\s]+");
                if (subLines.length > 0) {
                    QEValue value = QEValueBase.getInstance("x", subLines[0]);
                    if (value != null) {
                        this.data.setNumberOfWfc(value.getIntegerValue());
                    }
                }

                if (subLines.length > 1) {
                    QEValue value = QEValueBase.getInstance("x", subLines[1]);
                    if (value != null) {
                        this.data.setNumberOfProj(value.getIntegerValue());
                    }
                }
            }

        } catch (IOException e1) {
            e1.printStackTrace();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    private void parseUpfV2PPHeader(Node node) {
        if (node == null || !node.hasAttributes()) {
            return;
        }

        Node attNode = null;
        NamedNodeMap attributes = node.getAttributes();

        attNode = attributes.getNamedItem("generated");
        if (attNode != null) {
            this.data.setGenerated(attNode.getNodeValue());
        }

        attNode = attributes.getNamedItem("author");
        if (attNode != null) {
            this.data.setAuthor(attNode.getNodeValue());
        }

        attNode = attributes.getNamedItem("date");
        if (attNode != null) {
            this.data.setDate(attNode.getNodeValue());
        }

        attNode = attributes.getNamedItem("comment");
        if (attNode != null) {
            this.data.setComment(attNode.getNodeValue());
        }

        attNode = attributes.getNamedItem("element");
        if (attNode != null) {
            String element = attNode.getNodeValue();
            this.data.setElement(element == null ? element : element.trim());
        }

        attNode = attributes.getNamedItem("pseudo_type");
        if (attNode != null) {
            this.data.setPseudoType(attNode.getNodeValue());
        }

        attNode = attributes.getNamedItem("relativistic");
        if (attNode != null) {
            this.data.setRelativistic(attNode.getNodeValue());
        }

        attNode = attributes.getNamedItem("is_ultrasoft");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setUltrasoft(value.getLogicalValue());
            }
        }

        attNode = attributes.getNamedItem("is_paw");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setPaw(value.getLogicalValue());
            }
        }

        attNode = attributes.getNamedItem("is_coulomb");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setCoulomb(value.getLogicalValue());
            }
        }

        attNode = attributes.getNamedItem("has_so");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setWithSo(value.getLogicalValue());
            }
        }

        attNode = attributes.getNamedItem("has_wfc");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setWithWfc(value.getLogicalValue());
            }
        }

        attNode = attributes.getNamedItem("has_gipaw");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setWithGipaw(value.getLogicalValue());
            }
        }

        attNode = attributes.getNamedItem("paw_as_gipaw");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setPawAsGipaw(value.getLogicalValue());
            }
        }

        attNode = attributes.getNamedItem("core_correction");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setCoreCorrection(value.getLogicalValue());
            }
        }

        attNode = attributes.getNamedItem("functional");
        if (attNode != null) {
            this.data.setFunctional(attNode.getNodeValue());
        }

        attNode = attributes.getNamedItem("z_valence");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setZValence(value.getRealValue());
            }
        }

        attNode = attributes.getNamedItem("total_psenergy");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setTotalPsenergy(value.getRealValue());
            }
        }

        attNode = attributes.getNamedItem("wfc_cutoff");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setWfcCutoff(value.getRealValue());
            }
        }

        attNode = attributes.getNamedItem("rho_cutoff");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setRhoCutoff(value.getRealValue());
            }
        }

        attNode = attributes.getNamedItem("l_max");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setLMax(value.getIntegerValue());
            }
        }

        attNode = attributes.getNamedItem("l_max_rho");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setLMaxRho(value.getIntegerValue());
            }
        }

        attNode = attributes.getNamedItem("l_local");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setLLocal(value.getIntegerValue());
            }
        }

        attNode = attributes.getNamedItem("mesh_size");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setMeshSize(value.getIntegerValue());
            }
        }

        attNode = attributes.getNamedItem("number_of_wfc");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setNumberOfWfc(value.getIntegerValue());
            }
        }

        attNode = attributes.getNamedItem("number_of_proj");
        if (attNode != null) {
            QEValue value = QEValueBase.getInstance("x", attNode.getNodeValue());
            if (value != null) {
                this.data.setNumberOfProj(value.getIntegerValue());
            }
        }
    }
}
