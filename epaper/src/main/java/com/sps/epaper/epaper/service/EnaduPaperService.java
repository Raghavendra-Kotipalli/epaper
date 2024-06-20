package com.sps.epaper.epaper.service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class EnaduPaperService {
    private final RestTemplate restTemplate;

    public EnaduPaperService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public byte[] generatePdfFromImages(byte[][] images) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        for (byte[] imageBytes : images) {
            ImageData imageData = ImageDataFactory.create(imageBytes);
            Image image = new Image(imageData);
            document.add(image);
        }

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getImageInBytes(String url) throws IOException {
        try {
            byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
            if (imageBytes != null) {
                return imageBytes;
            } else {
                throw new IOException("Failed to download image from " + url);
            }
        }catch (IOException e) {
            System.out.println("Failed to download image from " + url);
        }
        return null;
    }

    public void downloadImage(String url, String destinationFile) throws IOException {
        try {
            byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
            if (imageBytes != null) {
                try (InputStream in = new ByteArrayInputStream(imageBytes);
                     FileOutputStream out = new FileOutputStream(destinationFile)) {
                    IOUtils.copy(in, out);
                }
            } else {
                throw new IOException("Failed to download image from " + url);
            }
        }catch (IOException e) {
           System.out.println("Failed to download image from " + url);
        }
    }

    public List<String> extractLinks(String htmlString) {
        List<String> links = new ArrayList<>();
        org.jsoup.nodes.Document document = Jsoup.parse(htmlString);
        Elements linkElements = document.select("owl-item");

        for (Element linkElement : linkElements) {
            links.add(linkElement.attr("highres"));
        }
        return links;
    }

    public String fetchHtmlContent(String url) {
        extractLinks(restTemplate.getForObject(url, String.class));
        return restTemplate.getForObject(url, String.class);
    }
}
