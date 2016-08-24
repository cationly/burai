/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.graphic.svg;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

public final class SVGLibrary {

    private SVGLibrary() {
        // NOP
    }

    public static enum SVGData {
        ANATOM(SVGItemAnAtom.WIDTH, SVGItemAnAtom.HEIGHT, SVGItemAnAtom.CONTENT),
        ATOMS(SVGItemAtoms.WIDTH, SVGItemAtoms.HEIGHT, SVGItemAtoms.CONTENT),
        ARROW_UP(SVGItemArrowLeft.WIDTH, SVGItemArrowLeft.HEIGHT, 90.0, SVGItemArrowLeft.CONTENT),
        ARROW_DOWN(SVGItemArrowLeft.WIDTH, SVGItemArrowLeft.HEIGHT, -90.0, SVGItemArrowLeft.CONTENT),
        ARROW_LEFT(SVGItemArrowLeft.WIDTH, SVGItemArrowLeft.HEIGHT, 0.0, SVGItemArrowLeft.CONTENT),
        ARROW_RIGHT(SVGItemArrowLeft.WIDTH, SVGItemArrowLeft.HEIGHT, 180.0, SVGItemArrowLeft.CONTENT),
        ARROW_ROUND(SVGItemArrowRound.WIDTH, SVGItemArrowRound.HEIGHT, SVGItemArrowRound.CONTENT),
        CAMERA(SVGItemCamera.WIDTH, SVGItemCamera.HEIGHT, SVGItemCamera.CONTENT),
        CHECK(SVGItemCheck.WIDTH, SVGItemCheck.HEIGHT, SVGItemCheck.CONTENT),
        CLOSE(SVGItemClose.WIDTH, SVGItemClose.HEIGHT, SVGItemClose.CONTENT),
        CONTROL(SVGItemControl.WIDTH, SVGItemControl.HEIGHT, SVGItemControl.CONTENT),
        CROSS(SVGItemPlus.WIDTH, SVGItemPlus.HEIGHT, 45.0, SVGItemPlus.CONTENT),
        CRYSTAL(SVGItemCrystal.WIDTH, SVGItemCrystal.HEIGHT, SVGItemCrystal.CONTENT),
        DOTS(SVGItemDots.WIDTH, SVGItemDots.HEIGHT, SVGItemDots.CONTENT),
        DOWNLOAD(SVGItemDownload.WIDTH, SVGItemDownload.HEIGHT, SVGItemDownload.CONTENT),
        EARTH(SVGItemEarth.WIDTH, SVGItemEarth.HEIGHT, SVGItemEarth.CONTENT),
        ERROR(SVGItemError.WIDTH, SVGItemError.HEIGHT, SVGItemError.CONTENT),
        EXPORT(SVGItemExport.WIDTH, SVGItemExport.HEIGHT, SVGItemExport.CONTENT),
        FILE(SVGItemFile.WIDTH, SVGItemFile.HEIGHT, SVGItemFile.CONTENT),
        FOLDER(SVGItemFolder.WIDTH, SVGItemFolder.HEIGHT, SVGItemFolder.CONTENT),
        HEART(SVGItemHeart.WIDTH, SVGItemHeart.HEIGHT, SVGItemHeart.CONTENT),
        HELP(SVGItemHeart.WIDTH, SVGItemHeart.HEIGHT, SVGItemHeart.CONTENT),
        HOME(SVGItemHome.WIDTH, SVGItemHome.HEIGHT, SVGItemHome.CONTENT),
        HTML(SVGItemHtml.WIDTH, SVGItemHtml.WIDTH, SVGItemHtml.CONTENT),
        INFO(SVGItemInfo.WIDTH, SVGItemInfo.HEIGHT, SVGItemInfo.CONTENT),
        INPUTFILE(SVGItemInputFile.WIDTH, SVGItemInputFile.HEIGHT, SVGItemInputFile.CONTENT),
        LIST(SVGItemList.WIDTH, SVGItemList.HEIGHT, SVGItemList.CONTENT),
        MAXIMIZE(SVGItemMaximize.WIDTH, SVGItemMaximize.HEIGHT, SVGItemMaximize.CONTENT),
        MENU(SVGItemMenu.WIDTH, SVGItemMenu.HEIGHT, SVGItemMenu.CONTENT),
        MINIMIZE(SVGItemMinimize.WIDTH, SVGItemMinimize.HEIGHT, SVGItemMinimize.CONTENT),
        MOVIE(SVGItemMovie.WIDTH, SVGItemMovie.HEIGHT, SVGItemMovie.CONTENT),
        PLUS(SVGItemPlus.WIDTH, SVGItemPlus.HEIGHT, SVGItemPlus.CONTENT),
        RESULT(SVGItemResult.WIDTH, SVGItemResult.HEIGHT, SVGItemResult.CONTENT),
        RUN(SVGItemRun.WIDTH, SVGItemRun.HEIGHT, SVGItemRun.CONTENT),
        SAVE(SVGItemSave.WIDTH, SVGItemSave.HEIGHT, SVGItemSave.CONTENT),
        SEARCH(SVGItemSearch.WIDTH, SVGItemSearch.HEIGHT, SVGItemSearch.CONTENT),
        STOP(SVGItemStop.WIDTH, SVGItemStop.HEIGHT, SVGItemStop.CONTENT),
        TILES(SVGItemTiles.WIDTH, SVGItemTiles.HEIGHT, SVGItemTiles.CONTENT),
        TRIANGLE_RIGHT(SVGItemTriangle.WIDTH, SVGItemTriangle.HEIGHT, SVGItemTriangle.CONTENT),
        TRIANGLE_UPWARD(SVGItemTriangle.WIDTH, SVGItemTriangle.HEIGHT, 90.0, SVGItemTriangle.CONTENT),
        VECTOR_UP(SVGItemVectorRight.WIDTH, SVGItemVectorRight.HEIGHT, -90.0, SVGItemVectorRight.CONTENT),
        VECTOR_DOWN(SVGItemVectorRight.WIDTH, SVGItemVectorRight.HEIGHT, 90.0, SVGItemVectorRight.CONTENT),
        VECTOR_LEFT(SVGItemVectorRight.WIDTH, SVGItemVectorRight.HEIGHT, 180.0, SVGItemVectorRight.CONTENT),
        VECTOR_RIGHT(SVGItemVectorRight.WIDTH, SVGItemVectorRight.HEIGHT, 0.0, SVGItemVectorRight.CONTENT);

        private double width;
        private double height;
        private double angle;
        private String content;

        private SVGData(double width, double height, String content) {
            this(width, height, 0.0, content);
        }

        private SVGData(double width, double height, double angle, String content) {
            if (width <= 0.0) {
                throw new IllegalArgumentException("width is not positive.");
            }

            if (height <= 0.0) {
                throw new IllegalArgumentException("height is not positive.");
            }

            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("content is empty.");
            }

            this.width = width;
            this.height = height;
            this.angle = angle;
            this.content = content;
        }
    }

    public static Node getGraphic(SVGData svgData, double size) {
        return getGraphic(svgData, size, null);
    }

    public static Node getGraphic(SVGData svgData, double size, String style) {
        return getGraphic(svgData, size, style, null);
    }

    public static Node getGraphic(SVGData svgData, double size, String style, String styleClass) {
        if (svgData == null) {
            throw new IllegalArgumentException("svgData is null.");
        }

        if (size <= 0.0) {
            throw new IllegalArgumentException("size is not positive.");
        }

        SVGPath svgPath = new SVGPath();

        if (styleClass != null && (!styleClass.isEmpty())) {
            svgPath.getStyleClass().add(styleClass);
        }

        if (style != null && (!style.isEmpty())) {
            svgPath.setStyle(style);
        }

        svgPath.setContent(svgData.content);
        svgPath.getTransforms().add(new Scale(size / svgData.width, size / svgData.height, 0.0, 0.0));
        if (svgData.angle != 0.0) {
            svgPath.getTransforms().add(new Rotate(svgData.angle, 0.5 * svgData.width, 0.5 * svgData.height));
        }

        Pane pane = new Pane(svgPath);
        pane.setPrefSize(size, size);
        Group group = new Group(pane);
        return group;
    }
}
