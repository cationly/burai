/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.icon;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.pseudo.PseudoLibrary;
import burai.pseudo.PseudoPotential;

public class QEFXUPFIcon extends QEFXIconBase<String> {

    private static final String FIGURE_CLASS = "icon-upf";
    private static final double FIGURE_FONT_SIZE1 = 0.08;
    private static final double FIGURE_FONT_SIZE2 = 0.16;

    public QEFXUPFIcon(String filePath) {
        super(filePath);
    }

    @Override
    public void detach() {
        this.content = null;
    }

    @Override
    public Node getFigure(double size) {
        if (size <= 0.0) {
            return null;
        }

        double scaledSize = size * ICON_SCALE;
        double insetsSize = 0.5 * (size - scaledSize);
        StackPane stackPane = new StackPane();

        Node figure = SVGLibrary.getGraphic(SVGData.ANATOM, scaledSize, null, FIGURE_CLASS);
        StackPane.setAlignment(figure, Pos.CENTER);
        stackPane.getChildren().add(figure);

        Label label1 = new Label();
        label1.getStyleClass().add(FIGURE_CLASS);
        label1.setStyle("-fx-font-size: " + (size * FIGURE_FONT_SIZE1));
        String title = this.createFileBaseText();
        if (title != null) {
            label1.setText(title);
        }

        Label label2 = new Label();
        label2.getStyleClass().add(FIGURE_CLASS);
        label2.setStyle("-fx-font-size: " + (size * FIGURE_FONT_SIZE2));
        label2.setText(".UPF");

        BorderPane borderPane = new BorderPane();
        BorderPane.setAlignment(label1, Pos.BOTTOM_LEFT);
        BorderPane.setAlignment(label2, Pos.TOP_CENTER);
        borderPane.setTop(label1);
        borderPane.setCenter(label2);

        StackPane.setAlignment(borderPane, Pos.CENTER);
        stackPane.getChildren().add(borderPane);

        Group group = new Group(stackPane);
        BorderPane.setMargin(group, new Insets(insetsSize));
        return group;
    }

    private String createFileBaseText() {
        if (this.content == null || this.content.trim().isEmpty()) {
            return null;
        }

        File file = new File(this.content);
        String name = file.getName();
        name = name == null ? null : name.trim();
        if (name == null || name.isEmpty()) {
            return null;
        }

        if (name.toLowerCase().endsWith(".upf")) {
            return name.substring(0, name.length() - 4);
        }

        return name;
    }

    @Override
    protected String initCaption() {
        File file = new File(this.content);
        String name = file.getName();
        if (name == null || name.trim().isEmpty()) {
            name = this.content;
        }

        return name;
    }

    @Override
    protected String initSubCaption() {
        String caption = this.getFileDetail(this.content);
        String propCaption = this.getPropertiesCaption();
        if (propCaption != null) {
            caption = (caption == null ? "" : (caption + System.lineSeparator())) + propCaption;
        }

        return caption;
    }

    public String getPropertiesCaption() {
        return this.getPropertiesCaption(true);
    }

    public String getPropertiesCaption(boolean wideLine) {
        PseudoPotential pseudoPot = PseudoLibrary.getInstance().peekPseudoPotential(this.content);
        if (pseudoPot == null) {
            pseudoPot = new PseudoPotential(this.content);
        }

        String caption = null;
        String lineSep = wideLine ? ",  " : System.lineSeparator();

        String pseudoType = pseudoPot.getData().getPseudoTypeName();
        if (pseudoPot.getData().hasPseudoType() && pseudoType != null && !pseudoType.isEmpty()) {
            caption = "P.P. Type : " + pseudoType;
        }

        String xcFunc = pseudoPot.getData().getFunctionalName();
        if (pseudoPot.getData().hasFunctional() && xcFunc != null && !xcFunc.isEmpty()) {
            caption = (caption == null ? "" : (caption + lineSep))
                    + "XC Functional : " + xcFunc;
        }

        double ecutWfc = pseudoPot.getData().getWfcCutoff();
        if (ecutWfc > 0.0) {
            caption = (caption == null ? "" : (caption + System.lineSeparator()))
                    + "Cutoff of W.F. :" + String.format("%9.3f", ecutWfc) + "Ry";
        }

        double ecutRho = pseudoPot.getData().getRhoCutoff();
        if (ecutRho > 0.0) {
            caption = (caption == null ? "" : (caption + lineSep))
                    + "Cutoff of Charge :" + String.format("%9.3f", ecutRho) + "Ry";
        }

        String relativ = pseudoPot.getData().getRelativisticName();
        if (pseudoPot.getData().hasRelativistic() && relativ != null && !relativ.isEmpty()) {
            caption = (caption == null ? "" : (caption + System.lineSeparator()))
                    + "Relativistic : " + relativ;
        }

        boolean hasNcc = pseudoPot.getData().isCoreCorrection();
        if (hasNcc) {
            caption = (caption == null ? "" : (caption + lineSep))
                    + "Nonlinear Core Correction is used.";
        }

        return caption;
    }
}
