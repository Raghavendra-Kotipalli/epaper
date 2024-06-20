package com.sps.epaper.epaper.controller;

import com.sps.epaper.epaper.service.EnaduPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(value ="/enaduPaper")
public class EnaduPaper {

    @Autowired
    public EnaduPaperService paperService;

    @GetMapping("/welcome")
    public String helloMessage() {
        System.out.println("Hello!");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);
        System.out.println("Formatted date: " + formattedDate);
        return "Hello Satti!";
    }

    @PostMapping("/generatePdf")
    public ResponseEntity<byte[]> generatePdf(@RequestParam("images") MultipartFile[] images) throws IOException {
        byte[][] imageBytes = new byte[images.length][];
        for (int i = 0; i < images.length; i++) {
            imageBytes[i] = images[i].getBytes();
        }

        byte[] pdfBytes = paperService.generatePdfFromImages(imageBytes);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", formattedDate+"_enadu_epaper.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }


    @PostMapping("/epaperByLinks")
    public ResponseEntity<byte[]> generatePdfByLinks(@RequestParam("links") String links) throws IOException {
        String urls[] = links.split(",");
        byte[][] imageBytes = new byte[urls.length][];
        for (int i = 0; i < urls.length; i++) {
            imageBytes[i] = paperService.getImageInBytes(urls[i].trim());
        }

        byte[] pdfBytes = paperService.generatePdfFromImages(imageBytes);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", formattedDate+"_enadu_epaper.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/download-image")
    public String downloadImage() {
        String imageUrl = "https://d66zsp32hue2v.cloudfront.net/Eenadu/2024/06/20/CAN/5_22/dbe5755f_22.jpg";
        String destinationFile = "C:\\Users\\Raghavendra.kotipall\\Desktop\\Epaper\\TodayNews\\a.jpg";

        try {
            paperService.downloadImage(imageUrl, destinationFile);
            return "Image downloaded successfully!";
        } catch (IOException e) {
            return "Failed to download image: " + e.getMessage();
        }
    }

    @GetMapping("/fetch-html")
    public String fetchHtml(@RequestParam String url) {
        return paperService.fetchHtmlContent(url);
    }
}
