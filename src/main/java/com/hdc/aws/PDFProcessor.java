package com.hdc.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;
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

public class PDFProcessor implements RequestStreamHandler, RequestHandler<Object, Object> {
    private static final String WATERMARK_METADATA = "watermark";
    private static final String BUCKET_NAME = "ehalal-uploads";
    private static final String BACKUP_FOLDER_NAME = "upload-backup/";

    public void loadAndProcessPDF(String inputFilename, String outputFilename) throws IOException {
        PDDocument doc = PDDocument.load(new File(inputFilename));

        doc = processPDF(doc);

        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        doc.save(outputStream2);

        FileUtils.writeByteArrayToFile(new File(outputFilename), outputStream2.toByteArray());

        doc.close();
    }

    @Override
    public Object handleRequest(Object input, Context context) {
        return null;
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();

        Map<String, Object> myMap = gson.fromJson(IOUtils.toString(input, "UTF-8"), type);
        JXPathContext testContext = JXPathContext.newContext(myMap);
        String key = String.valueOf(testContext.getValue("//Records[1]/s3/object/key"));
        AmazonS3 s3Client = getAmazonS3Client();

        S3Object obj = s3Client.getObject(new GetObjectRequest(BUCKET_NAME, key));
        boolean doNotHaveWatermarkMetadata = "true".equals(obj.getObjectMetadata().getUserMetaDataOf(WATERMARK_METADATA)) == false;
        if(doNotHaveWatermarkMetadata) {
            makeABackupCopy(key, s3Client);

            PDDocument doc = PDDocument.load(obj.getObjectContent());
            doc = processPDF(doc);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            doc.save(outputStream);

            ObjectMetadata metadata = obj.getObjectMetadata();
            metadata.setContentLength(outputStream.size());
            metadata.addUserMetadata(WATERMARK_METADATA, "true");
            s3Client.putObject(new PutObjectRequest(BUCKET_NAME, key, new ByteArrayInputStream(outputStream.toByteArray()), metadata));

            doc.close();
        }
        output.write("OK".getBytes());
    }

    private void makeABackupCopy(String key, AmazonS3 s3Client) {
        s3Client.copyObject(new CopyObjectRequest(BUCKET_NAME, key, BUCKET_NAME, BACKUP_FOLDER_NAME + key));
    }

    private PDDocument processPDF(PDDocument doc) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Iterator<PDPage> pages = doc.getDocumentCatalog().getPages().iterator();
        do {
            PDPage page = pages.next();
            PDPageContentStream cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);

            addText(page, cs, "This is the sample document and we are adding content to it.");
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
        cs.drawImage(ximage, rec.getUpperRightX() - (rec.getUpperRightX() / 2) - (ximage.getWidth() * 2 / 2), rec.getUpperRightY() - (rec.getUpperRightY() / 2) - (ximage.getHeight() * 2 / 2), ximage.getWidth() * 2, ximage.getHeight() * 2);
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

    private AmazonS3Client getAmazonS3Client() {
        String accessKey = System.getProperty("accessKey");
        String secretKey = System.getProperty("secretKey");
        if(accessKey != null && secretKey != null) {
            return new AmazonS3Client(new AWSCredentialsProvider() {
                @Override
                public AWSCredentials getCredentials() {
                    return new BasicAWSCredentials(accessKey, secretKey);
                }

                @Override
                public void refresh() {
                }
            });
        } else {
            return new AmazonS3Client();
        }
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
