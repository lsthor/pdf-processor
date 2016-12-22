package com.hdc.aws;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

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

//            addText(page, cs, DISCLAIMER);
            placeWatermark(doc, page, cs);

            cs.close();
            doc.save(outputStream);
        } while (pages.hasNext());
        return doc;
    }

    private void addText(PDPage page, PDPageContentStream cs, String text) throws IOException {
        cs.setGraphicsStateParameters(initiateGraphicsState(page, 0.8f));
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 14);
        cs.newLineAtOffset(0, 14);
        cs.showText(text);
        cs.endText();
    }

    private void placeWatermark(PDDocument doc, PDPage page, PDPageContentStream cs) throws IOException {
        cs.setGraphicsStateParameters(initiateGraphicsState(page, 0.2f));
        PDImageXObject ximage = PDImageXObject.createFromFileByContent(new File(Main.class.getClassLoader().getResource("watermark.png").getFile()), doc);
        PDRectangle rec = page.getMediaBox();
        placeWatermarkAtTheMiddle(cs, ximage, rec);
    }

    private void placeWatermarkAtTheMiddle(PDPageContentStream cs, PDImageXObject ximage, PDRectangle rec) throws IOException {
        float scale = rec.getWidth() * 0.25f / ximage.getWidth();
        cs.drawImage(ximage, rec.getUpperRightX() - (rec.getUpperRightX() / 2) - (ximage.getWidth() * scale / 2), rec.getUpperRightY() - (rec.getUpperRightY() / 2) - (ximage.getHeight() * scale / 2), ximage.getWidth() * scale, ximage.getHeight() * scale);
    }

    private void placeWatermarkAtTheBottomLeft(PDPageContentStream cs, PDImageXObject ximage, PDRectangle rec) throws IOException {
        cs.drawImage(ximage, 0, 0, ximage.getWidth() / 2, ximage.getHeight() / 2);
    }

    private void placeWatermarkAtTheBottomRight(PDPageContentStream cs, PDImageXObject ximage, PDRectangle rec) throws IOException {
        cs.drawImage(ximage, rec.getWidth() - (ximage.getWidth() / 2), 0, ximage.getWidth() / 2, ximage.getHeight() / 2);
    }

    private void placeWatermarkAtTheTopLeft(PDPageContentStream cs, PDImageXObject ximage, PDRectangle rec) throws IOException {
        cs.drawImage(ximage, 0, rec.getHeight() - (ximage.getHeight() / 2), ximage.getWidth() / 2, ximage.getHeight() / 2);
    }

    private void placeWatermarkAtTheTopRight(PDPageContentStream cs, PDImageXObject ximage, PDRectangle rec) throws IOException {
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
