package com.university.project.controllers;

import com.university.project.DTO.History;
import com.university.project.models.FlashCardHistory;
import com.university.project.models.Flashcard;
import com.university.project.models.Quiz;
import com.university.project.models.User;
import com.university.project.repo.FlashcardRepo;
import com.university.project.repo.HistoryRepo;
import com.university.project.repo.QuizRepo;
import com.university.project.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizRepo quizRepo;

    @Autowired
    FlashcardRepo flashcardRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    HistoryRepo historyRepo;

    // Gets the user
    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    // Quiz data for the quiz to take place
    @GetMapping("")
    public String Quiz(@RequestParam Long id, @RequestParam(required = false, defaultValue = "-1") int currentFlashcardIndex, Model model) {
        boolean isEnd = false;
        Long u = userRepo.findByUsername(getCurrentUser()).getId();
        if (quizRepo.findByUserIdAndId(u, id).isPresent()) {

            Optional<Quiz> quizOptional = quizRepo.findById(id);
            if (quizOptional.isPresent()) {
                Quiz quiz = quizOptional.get();
                model.addAttribute("quiz", quiz);
                // All flashcards for a quiz
                List<Flashcard> flashcards = flashcardRepo.findByQuizId(id);
//                for (Flashcard a : flashcards) {
////                    System.out.println(a.getQuestion());
//                }
                model.addAttribute("flashcards", flashcards);

                if (currentFlashcardIndex + 1 < flashcards.size()) {

                    model.addAttribute("currentFlashcardIndex", currentFlashcardIndex + 1);
                } else {
                    // If the current flashcard index is at the end, display the submit button
                    model.addAttribute("currentFlashcardIndex", currentFlashcardIndex);
                    isEnd = true;
                }
            }
        }
        model.addAttribute("isEnd", isEnd);

        return "quiz";
    }
    // Navigate to history with necessary information
    @GetMapping("/history")
    public String History(Model model) {
        Long u = userRepo.findByUsername(getCurrentUser()).getId();
        List<FlashCardHistory> history = historyRepo.findAllByUserId(u);

        model.addAttribute("history", history);

        return "quizhistory";
    }
    // Delete quiz from history
    // NOT IN USE
    @DeleteMapping("/history/delete/{id}")
    public ResponseEntity<String> deleteHistory(@PathVariable Long id) {
        historyRepo.deleteById(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    // Save quiz for the quiz history
    @PostMapping("/saveQuizData")
    public ResponseEntity<String> saveQuizData(@RequestBody History historyInfo) {

        FlashCardHistory f = new FlashCardHistory();
        User u = userRepo.findByUsername(getCurrentUser());
        f.setUser(u);
        f.setScore(historyInfo.getScore());

        String historyName = historyInfo.getName();
        // Check if the name already exists
        boolean nameExists = false;
        for (FlashCardHistory history : historyRepo.findAll()) {
            if (history.getFlashcardName().equals(historyName)) {
                nameExists = true;
                break;
            }
        }

        // If the name already exists, append "#" followed by a number
        if (nameExists) {
            int index = historyName.lastIndexOf("#");
            if (index != -1) {
                try {
                    int number = Integer.parseInt(historyName.substring(index + 1)) + 1;
                    historyName = historyName.substring(0, index) + "#" + number;
                    f.setFlashcardName(historyName);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else {
                historyName += "#1";
                f.setFlashcardName(historyName);
            }
        }
        else {
            f.setFlashcardName(historyName);
        }
        historyRepo.save(f);
        return new ResponseEntity<>("Quiz data saved successfully", HttpStatus.OK);
    }
    // Delete a quiz
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable Long id) {
        quizRepo.deleteById(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }
   // Presents quiz data to be able to navigate
    @GetMapping("/option")
    public String quizOption(@RequestParam long id, Model model) {
        Optional<Quiz> Question = quizRepo.findById(id);

        if (Question.isPresent()){
            model.addAttribute("Question", Question.get().getName());
        }

        model.addAttribute("id", id);

        return "option";
    }
    // Gives all data of quiz & flashcards for the revision
    @GetMapping("/revision")
    public String quizRevision(Model model, @RequestParam Long id) {
        Long u = userRepo.findByUsername(getCurrentUser()).getId();
        if (quizRepo.findByUserIdAndId(u, id).isPresent()) {

            Optional<Quiz> quizOptional = quizRepo.findById(id);
            if (quizOptional.isPresent()) {
                Quiz quiz = quizOptional.get();
                model.addAttribute("quiz", quiz);

                List<Flashcard> flashcards = flashcardRepo.findByQuizId(id);

                model.addAttribute("flashcards", flashcards);
                // Number between 0-10
                int randomNumber = (int) Math.floor(Math.random() * 11);

                if (randomNumber < flashcards.size()) {

                    model.addAttribute("Index", randomNumber);
                } else {
                    model.addAttribute("Index", randomNumber - 1);
                }

            }
        }
        return "revision";
    }

}
