package com.hdc.aws;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

import java.io.*;
import java.util.Iterator;

public class PDFProcessor{

    public static final String DISCLAIMER = "This is the sample document and we are adding content to it.";

    public void loadAndProcessPDF(String inputFilename, String outputFilename) throws IOException {
        PDDocument doc = PDDocument.load(new File(inputFilename));
        doc = processPDF(doc);

        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        doc.save(outputStream2);

        FileUtils.writeByteArrayToFile(new File(outputFilename), outputStream2.toByteArray());

        doc.close();
    }


    public PDDocument processPDF(PDDocument doc) throws IOException {
        doc.setAllSecurityToBeRemoved(true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Iterator<PDPage> pages = doc.getDocumentCatalog().getPages().iterator();
        do {
            PDPage page = pages.next();
            PDPageContentStream cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);

            placeWatermark(doc, page, cs,new File(Main.class.getClassLoader().getResource("watermark.png").getFile()), 0.2f, 0.35f, Placement.MIDDLE);
            placeWatermark(doc, page, cs, new File(Main.class.getClassLoader().getResource("text-black.png").getFile()), 0.8f, 1f, Placement.BOTTOM_LEFT);

            cs.close();
            doc.save(outputStream);
        } while (pages.hasNext());
        return doc;
    }

    private void placeWatermark(PDDocument doc, PDPage page, PDPageContentStream cs, File image, float alphaLevel, float imageScaleToPage, Placement placement) throws IOException {
        cs.setGraphicsStateParameters(initiateGraphicsState(page, alphaLevel));
        PDImageXObject ximage = PDImageXObject.createFromFileByContent(image, doc);
        PDRectangle rec = page.getMediaBox();
        switch(placement) {
            case MIDDLE:
                placeImageAtTheMiddle(cs, ximage, rec, imageScaleToPage);
                break;
            case BOTTOM_LEFT:
                placeImageAtTheBottomLeft(cs, ximage, rec, imageScaleToPage);
                break;
        }
    }

    private void placeImageAtTheMiddle(PDPageContentStream cs, PDImageXObject ximage, PDRectangle rec, float imageScaleToPage) throws IOException {
        float scale = rec.getWidth() / ximage.getWidth() * imageScaleToPage;
        cs.drawImage(ximage, rec.getUpperRightX() - (rec.getUpperRightX() / 2) - (ximage.getWidth() * scale / 2), rec.getUpperRightY() - (rec.getUpperRightY() / 2) - (ximage.getHeight() * scale / 2), ximage.getWidth() * scale, ximage.getHeight() * scale);
    }

    private void placeImageAtTheBottomLeft(PDPageContentStream cs, PDImageXObject ximage, PDRectangle rec, float imageScaleToPage) throws IOException {
        float scale = rec.getWidth() / ximage.getWidth() * imageScaleToPage;
        cs.drawImage(ximage, 0, 0, ximage.getWidth() * scale, ximage.getHeight() * scale);
    }

    private void placeImageAtTheBottomRight(PDPageContentStream cs, PDImageXObject ximage, PDRectangle rec) throws IOException {
        cs.drawImage(ximage, rec.getWidth() - (ximage.getWidth() / 2), 0, ximage.getWidth() / 2, ximage.getHeight() / 2);
    }

    private void placeImageAtTheTopLeft(PDPageContentStream cs, PDImageXObject ximage, PDRectangle rec) throws IOException {
        cs.drawImage(ximage, 0, rec.getHeight() - (ximage.getHeight() / 2), ximage.getWidth() / 2, ximage.getHeight() / 2);
    }

    private void placeImageAtTheTopRight(PDPageContentStream cs, PDImageXObject ximage, PDRectangle rec) throws IOException {
        cs.drawImage(ximage, rec.getWidth() - (ximage.getWidth() / 2), rec.getHeight() - (ximage.getHeight() / 2), ximage.getWidth() / 2, ximage.getHeight() / 2);
    }

    private PDExtendedGraphicsState initiateGraphicsState(PDPage page, float alphaLevel) {
        PDResources resources = page.getResources();
        PDExtendedGraphicsState extendedGraphicsState = new PDExtendedGraphicsState();
        extendedGraphicsState.setNonStrokingAlphaConstant(alphaLevel);
        PDExtendedGraphicsState graphicsState = resources.getExtGState(COSName.TRANSPARENCY);
        if (graphicsState == null) {
            graphicsState = extendedGraphicsState;
        }
        resources.add(graphicsState);
        return graphicsState;
    }


}
