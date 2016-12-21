package com.hdc.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDInlineImage;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
//        AmazonS3 s3Client = new AmazonS3Client(new AWSCredentialsProvider() {
//            @Override
//            public AWSCredentials getCredentials() {
//                return new BasicAWSCredentials("AKIAJNM5CZKFCYNJRM4Q", "9G2HrhIFFFk77PIfuNS8KobLzsEmvS6L7pPd1iSk");
//            }
//
//            @Override
//            public void refresh() {
//            }
//        });
//        S3Object obj = s3Client.getObject(new GetObjectRequest("ehalal-uploads", "test/pdf-sample.pdf"));
//        PDDocument doc = PDDocument.load(obj.getObjectContent());
//        doc.getDocumentCatalog().getPages().forEach(page -> {
//            try {
//                PDPageContentStream cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
//                PDResources resources = page.getResources();
//                PDExtendedGraphicsState extendedGraphicsState = new PDExtendedGraphicsState();
//                // Set the transparency/opacity
//                extendedGraphicsState.setNonStrokingAlphaConstant(0.2f);
//                PDExtendedGraphicsState graphicsState = resources.getExtGState(COSName.TRANSPARENCY);
//                if (graphicsState == null) {
//                    graphicsState = extendedGraphicsState;
//                }
//                resources.add(graphicsState);
//
//                cs.setGraphicsStateParameters(graphicsState);
//
//                PDImageXObject ximage = PDImageXObject.createFromFileByContent(new File(Main.class.getClassLoader().getResource("watermark.png").getFile()), doc);
////                cs.drawImage(ximage, 100, 100, ximage.getWidth() / 2, ximage.getHeight() / 2);
//                PDRectangle rec = page.getMediaBox();
////System.out.println(rec.getHeight() + " " + rec.getUpperRightY());
//                cs.drawImage(ximage, rec.getUpperRightX() - (rec.getUpperRightX() / 2), rec.getUpperRightY() - (rec.getUpperRightY() / 2), ximage.getWidth() / 2, ximage.getHeight() / 2);
////                cs.drawImage(ximage, rec.getUpperRightX() - 80, rec.getUpperRightY() - 80, ximage.getWidth() / 2, ximage.getHeight() / 2);
//                cs.drawImage(ximage, rec.getWidth() - (ximage.getWidth() / 2), rec.getHeight() - (ximage.getHeight() / 2), ximage.getWidth() / 2, ximage.getHeight() / 2);
//                cs.drawImage(ximage, rec.getWidth() - (ximage.getWidth() / 2), 0, ximage.getWidth() / 2, ximage.getHeight() / 2);
//                cs.drawImage(ximage, 0, rec.getHeight() - (ximage.getHeight() / 2), ximage.getWidth() / 2, ximage.getHeight() / 2);
//                cs.drawImage(ximage, 0, 0, ximage.getWidth() / 2, ximage.getHeight() / 2);
////                cs.drawImage(ximage, rec.getLowerLeftX(), rec.getLowerLeftX(), ximage.getWidth() / 2, ximage.getHeight() / 2);
//                cs.close();
//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                doc.save(outputStream);
//                ObjectMetadata metadata = obj.getObjectMetadata();
//                metadata.setContentLength(outputStream.size());
//                s3Client.putObject(new PutObjectRequest("ehalal-uploads", "test/pdf-sample.pdf", new ByteArrayInputStream(outputStream.toByteArray()), metadata));
//                doc.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
        PDFProcessor processor = new PDFProcessor();
        processor.loadAndProcessPDF("/Users/thor/Downloads/pdf-sample.pdf", "/Users/thor/Downloads/pdf-sample-output.pdf");
//        File tmpPDF = new File("/Users/thor/Downloads" + System.getProperty("file.separator") + "tmp.pdf");

//        doc.save(tmpPDF);
//        pages.iterator().forEachRemaining(page -> {
//            PDPageContentStream contentStream = null;
//            try {
//                contentStream = new PDPageContentStream(doc, page, true, true, true);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            contentStream.drawString(stampString);
//        });
    }
}
