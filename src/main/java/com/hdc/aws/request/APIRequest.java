package com.hdc.aws.request;

public class APIRequest {
    private String bucket;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBucket() {

        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
