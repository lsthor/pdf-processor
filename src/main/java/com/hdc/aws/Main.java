package com.hdc.aws;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        PDFProcessor processor = new PDFProcessor();
        processor.loadAndProcessPDF("/Users/thor/Downloads/pdf-sample.pdf", "/Users/thor/Downloads/pdf-sample-output-1.pdf");
    }
}
