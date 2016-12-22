package com.hdc.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.hdc.aws.request.APIRequest;

import java.io.IOException;

public class DirectAPIHandler implements RequestHandler<APIRequest, Object> {
    @Override
    public Object handleRequest(APIRequest request, Context context) {
        try {
            new S3Service().putWatermarkOnPDF(request.getBucket(), request.getKey());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return new Object();
    }
}
