package com.university.project.controllers;

import com.university.project.Service.OpenAIService;
import com.university.project.models.Quiz;
import com.university.project.repo.QuizRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class OpenAIController {

    private final OpenAIService openAIService;

    @Autowired
    public OpenAIController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @Autowired
    QuizRepo quizRepo;

    // Passes question to OpenAI
    @PostMapping("/ask")
    public Long askOpenAI(@RequestBody String topic) {
        List<Quiz> q = openAIService.getResponse(topic);

        if (!q.isEmpty()) {
            return q.get(q.size() - 1).getId();
        } else {
            return null;
        }
    }

}

