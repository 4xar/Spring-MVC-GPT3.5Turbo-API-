package com.university.project.repo;

import com.university.project.models.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlashcardRepo extends JpaRepository<Flashcard,Long> {
    List<Flashcard> findByQuizId(Long quizId);
}
