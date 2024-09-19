package com.university.project.controllers;

import com.university.project.models.Flashcard;
import com.university.project.models.Quiz;
import com.university.project.models.User;
import com.university.project.repo.FlashcardRepo;
import com.university.project.repo.QuizRepo;
import com.university.project.repo.UserRepo;
import jakarta.persistence.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.PasswordView;
import javax.swing.text.html.StyleSheet;
import java.util.List;
import java.util.Optional;

@Controller
public class mainController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    QuizRepo quizRepo;

    @Autowired
    FlashcardRepo flashcardRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String homePage() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "register";
    }
    // Registers the user and encodes the pass for the database
    @PostMapping("/process_register")
    public String processRegister(User user, Model model, RedirectAttributes redirectAttributes) {

        User existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser != null) {
            model.addAttribute("usernameError", "Username already exists. Please choose a different one.");
            return "redirect:register";
        } else {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setRole("USER");
            userRepo.save(user);
            redirectAttributes.addFlashAttribute("registrationSuccess", true);
            return "redirect:/login";
        }

    }

    @GetMapping("/dashboard")
    public String dashboard() {

        return "dashboard";
    }
    // Presents all quiz question and answers
    @GetMapping("/quizsummary")
    public String quizSummary(@RequestParam Long id, Model model) {
        Optional<Quiz> quizOptional = quizRepo.findById(id);
        if (quizOptional.isPresent()) {
            Quiz quiz = quizOptional.get();
            model.addAttribute("quiz", quiz);

            List<Flashcard> flashcards = flashcardRepo.findByQuizId(id);
            model.addAttribute("flashcards", flashcards);
        } else {
            System.out.println("Quiz not found for ID: " + id);
        }
        return "quizsummary";
    }
}
