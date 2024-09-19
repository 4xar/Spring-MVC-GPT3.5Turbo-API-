package com.university.project.models;

import jakarta.persistence.*;

import java.util.List;
@Entity
public class MultipleChoiceOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String optionLetter;
    private String optionText;

    @ManyToOne
    @JoinColumn(name = "flashcard_id")
    private Flashcard flashcard;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOptionLetter() {
        return optionLetter;
    }

    public void setOptionLetter(String optionLetter) {
        this.optionLetter = optionLetter;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Flashcard getFlashcard() {
        return flashcard;
    }

    public void setFlashcard(Flashcard flashcard) {
        this.flashcard = flashcard;
    }

}
