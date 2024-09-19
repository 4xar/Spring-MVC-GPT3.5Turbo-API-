package com.university.project.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.project.models.*;
import com.university.project.repo.FlashcardRepo;
import com.university.project.repo.MCArepo;
import com.university.project.repo.QuizRepo;
import com.university.project.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@Transactional
@Service
public class OpenAIService {
    @Autowired
    FlashcardRepo flashcardRepo;
    @Autowired
    QuizRepo quizRepo;
    @Autowired
    MCArepo mcarepo;
    @Autowired
    UserRepo userRepo;

    @Value("${openai.key}")
    private String apiKey;

    private final String OPENAI_API_URL = "https://api.openai.com/v1/completions";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private String topic;

    public OpenAIService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
    // AI Config / Response
    public List<Quiz> getResponse(String topic) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        JSONObject t = new JSONObject(topic);

        this.topic = t.getString("topic");

        // Construct request body
        OpenAIRequest request = new OpenAIRequest();
        request.setModel("gpt-3.5-turbo-instruct");
        request.setPrompt("Generate 10 multiple-choice questions about " + topic + " with their answers");
        request.setMax_tokens(860);

        HttpEntity<String> entity = new HttpEntity<>(toJsonString(request), headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                OPENAI_API_URL, HttpMethod.POST, entity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return parseResponse(responseEntity.getBody());
        } else {
            return Collections.emptyList();
        }
    }

    private String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }
    // Add data to the quiz table
    private List<Quiz> parseResponse(String responseBody) {
        List<Quiz> quizzes = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            JsonNode choicesNode = rootNode.get("choices");

            if (choicesNode != null && choicesNode.isArray()) {
                for (JsonNode choiceNode : choicesNode) {
                    // Capture the response
                    String text = choiceNode.get("text").asText();
                    // Splits for each question
                    String[] questions = text.split("\\n\\n");

                    Quiz quiz = new Quiz();
                    quiz.setName(topic);

                    List<Flashcard> flashcards = new ArrayList<>();
                    for (String question : questions) {
                        Flashcard flashcard = parseQuestion(question);
                        if (flashcard != null && flashcard.getCorrectAnswer() != null) {
                            flashcard.setQuiz(quiz);
                            flashcards.add(flashcard);
                        }
                    }
                    if (flashcards.get(0).getCorrectAnswer() != null){
                    quiz.setFlashcards(flashcards);
                    quiz.setUser(userRepo.findByUsername(getCurrentUser()));

                    quizzes.add(quiz);
                    quizRepo.save(quiz);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return quizzes;
    }

    // Format the flashcard and add to the database
    private Flashcard parseQuestion(String question) {
        try {
            Flashcard flashcard = new Flashcard();
            // Get each line
            String[] lines = question.split("\\n");
            flashcard.setQuestion(lines[0].trim());

            List<MultipleChoiceOption> options = new ArrayList<>();
            //  Add correct values to objects
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.startsWith("Answer:") || line.startsWith("Answer: ")) {
                    String answerLine = line.substring("Answer:".length()).trim();
                    if (!answerLine.isEmpty()) {
                        String a = Character.toString(answerLine.charAt(0)).toUpperCase();
                        flashcard.setCorrectAnswer(a);
                    }
                } else {
                    MultipleChoiceOption option = new MultipleChoiceOption();
                    option.setOptionLetter(line.substring(0, 1).toUpperCase());
                    option.setOptionText(line.length() > 3 ? line.substring(3).trim() : "");

                    option.setFlashcard(flashcard);
                    options.add(option);
                }
            }
            flashcard.setOptions(options);
            flashcardRepo.save(flashcard);
            return flashcard;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
