package com.example.springapp.controller;
import com.example.springapp.model.QuestionProbability;
import com.example.springapp.service.AnalyzerService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyzerController {

    private final AnalyzerService analyzerService;

    public AnalyzerController(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @PostMapping("/analyze")
    public List<QuestionProbability> analyze(
            @RequestParam("file") MultipartFile file) throws Exception {

        return analyzerService.analyzeFile(file);
    }
}
