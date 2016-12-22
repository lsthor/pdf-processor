package com.hdc.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

public class S3NotificationHandler implements RequestStreamHandler, RequestHandler<Object, Object> {
    private static final String BUCKET_NAME = "ehalal-uploads";

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
        new S3Service().putWatermarkOnPDF(BUCKET_NAME, key);
        output.write("OK".getBytes());
    }


}
