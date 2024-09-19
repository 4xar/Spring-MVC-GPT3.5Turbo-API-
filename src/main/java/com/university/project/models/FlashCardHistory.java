package com.university.project.models;

import jakarta.persistence.*;

import java.util.List;
@Entity
public class FlashCardHistory {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFlashcardName() {
        return FlashcardName;
    }

    public void setFlashcardName(String flashcardName) {
        FlashcardName = flashcardName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String FlashcardName;
    private int score;
}
