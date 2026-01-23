package it.bicocca.eduquest.domain.answers;

import jakarta.persistence.Entity;

@Entity
public class OpenAnswer extends Answer {

    private String text;

    public OpenAnswer() {}

    public OpenAnswer(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}