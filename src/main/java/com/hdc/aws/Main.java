package com.hdc.aws;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        PDFProcessor processor = new PDFProcessor();
        processor.loadAndProcessPDF("/Users/thor/Downloads/test92-1470976808894.pdf", "/Users/thor/Downloads/pdf-sample-output.pdf");
    }
}
