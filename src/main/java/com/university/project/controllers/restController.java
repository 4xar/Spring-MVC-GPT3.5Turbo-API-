package com.university.project.controllers;

import com.university.project.DTO.QuizInfo;
import com.university.project.models.Quiz;
import com.university.project.repo.QuizRepo;
import com.university.project.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
public class restController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    QuizRepo quizRepo;
    // Gets the user
    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
    //  Latest 3 quizzes
    @GetMapping("/latestQuizzes")
    public List<QuizInfo> getLatestQuizzes() {
        Long id = userRepo.findByUsername(getCurrentUser()).getId();

        List<Quiz> latestQuizzes = quizRepo.findLatestQuizzesForUser(id,3);
        List<QuizInfo> quizInfos = new ArrayList<>();

        for (Quiz quiz : latestQuizzes) {
            QuizInfo quizInfo = new QuizInfo(quiz.getId(), quiz.getName());
            quizInfos.add(quizInfo);
        }

        return quizInfos;
    }

//    //  Find specific quiz for user id
//    @GetMapping("/quizData")
//    public ResponseEntity<Quiz> getQuizData(@RequestParam Long id) {
//        Long userId = userRepo.findByUsername(getCurrentUser()).getId();
//        Optional<Quiz> q = quizRepo.findByUserIdAndId(userId, id);
//        if (q.isPresent()){
//            Quiz quiz = q.get();
//            return ResponseEntity.ok(quiz);
//        } else return ResponseEntity.notFound().build();
//    }

    // All quizzes for a user
    @GetMapping("/allQuizzes")
    public List<Object[]> allQuizzes(){
        Long userId = userRepo.findByUsername(getCurrentUser()).getId();
        return quizRepo.findAllQuizInfoByUserId(userId);
    }



}
