package com.example.springapp.service;

import com.example.springapp.model.QuestionProbability;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class AnalyzerService {

    public List<QuestionProbability> analyzeFiles(MultipartFile[] files) throws Exception {

        Map<String, Integer> frequencyMap = new HashMap<>();

        for (MultipartFile file : files) {

            if (file == null || file.isEmpty()) continue;

            String name = file.getOriginalFilename().toLowerCase();
            String text;

            // 1️⃣ Extract text
            if (name.endsWith(".pdf")) {
                try (PDDocument doc = PDDocument.load(file.getInputStream())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    text = stripper.getText(doc);
                }
            } else if (name.endsWith(".txt")) {
                text = new String(file.getBytes());
            } else {
                continue;
            }

            // 2️⃣ Normalize text aggressively
            text = text
                    .replace("\r", " ")
                    .replace("\n", " ")
                    .replaceAll("\\s+", " ");

            // 3️⃣ Split by question marks
            String[] questions = text.split("\\?");

            for (String q : questions) {
                String question = q.trim();

                if (question.isEmpty()) continue;

                // 4️⃣ Normalize key (CRITICAL)
                String key = question
                        .toLowerCase()
                        .replaceAll("[^a-z0-9 ]", "")
                        .replaceAll("\\s+", " ")
                        .trim();

                if (key.isEmpty()) continue;

                frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
            }
        }

        // 5️⃣ Probability calculation
        int max = frequencyMap.values().stream()
                .max(Integer::compare)
                .orElse(1);

        List<QuestionProbability> result = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            double probability = (entry.getValue() * 100.0) / max;
            result.add(new QuestionProbability(entry.getKey() + "?", probability));
        }

        // 6️⃣ Sort by probability (optional but useful)
        result.sort(
                Comparator.comparingDouble(QuestionProbability::getProbability)
                        .reversed()
        );

        return result;
    }
}
