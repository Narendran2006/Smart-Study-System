package com.example.springapp.service;
import com.example.springapp.model.QuestionProbability;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;


@Service
public class AnalyzerService {

    public List<QuestionProbability> analyzeFile(MultipartFile file) throws Exception {

        String extractedText;

        // 1️⃣ Convert PDF / TXT → text
        if (file.getOriginalFilename().endsWith(".pdf")) {
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();
            extractedText = stripper.getText(document);
            document.close();
        } else {
            extractedText = new String(file.getBytes());
        }

        // 2️⃣ Split text into questions
        String[] questions = extractedText.split("\\?|\\n");

        // 3️⃣ Count frequency
        Map<String, Integer> frequencyMap = new HashMap<>();

        for (String q : questions) {
            q = q.trim().toLowerCase();
            if (q.length() > 10) {   // avoid noise
                frequencyMap.put(q, frequencyMap.getOrDefault(q, 0) + 1);
            }
        }

        // 4️⃣ Calculate probability
        int total = frequencyMap.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();

        List<QuestionProbability> result = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            double probability = (entry.getValue() * 100.0) / total;
            result.add(new QuestionProbability(entry.getKey(), probability));
        }

        return result;
    }
}
