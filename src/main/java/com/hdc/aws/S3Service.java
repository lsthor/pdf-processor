package com.hdc.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class S3Service {
    private static final String WATERMARK_METADATA = "watermark";
    private static final String BACKUP_FOLDER_NAME = "upload-backup/";

    public void putWatermarkOnPDF(String bucketName, String key) throws IOException {
        AmazonS3 s3Client = getAmazonS3Client();

        S3Object obj = s3Client.getObject(new GetObjectRequest(bucketName, key));
        boolean doNotHaveWatermarkMetadata = "true".equals(obj.getObjectMetadata().getUserMetaDataOf(WATERMARK_METADATA)) == false;
        if(doNotHaveWatermarkMetadata) {
            makeABackupCopy(s3Client, bucketName, key);

            PDDocument doc = PDDocument.load(obj.getObjectContent());
            doc = new PDFProcessor().processPDF(doc);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            doc.save(outputStream);

            ObjectMetadata metadata = obj.getObjectMetadata();
            metadata.setContentLength(outputStream.size());
            metadata.addUserMetadata(WATERMARK_METADATA, "true");

            PutObjectRequest putRequest = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(outputStream.toByteArray()), metadata);
            putRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(putRequest);

            doc.close();
        }
    }


    private void makeABackupCopy(AmazonS3 s3Client, String bucketName, String key) {
        s3Client.copyObject(new CopyObjectRequest(bucketName, key, bucketName, BACKUP_FOLDER_NAME + key));
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
}
