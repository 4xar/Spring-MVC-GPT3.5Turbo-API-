package com.university.project.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;
@Entity
@JsonIgnoreProperties("quiz")
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    @OneToMany(mappedBy = "flashcard", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MultipleChoiceOption> options;
    private String correctAnswer;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<MultipleChoiceOption> getOptions() {
        return options;
    }

    public void setOptions(List<MultipleChoiceOption> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}
