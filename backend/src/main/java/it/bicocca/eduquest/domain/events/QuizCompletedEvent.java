package it.bicocca.eduquest.domain.events;

import it.bicocca.eduquest.domain.answers.*;
import org.springframework.context.ApplicationEvent;

public class QuizCompletedEvent extends ApplicationEvent {
	private final QuizAttempt attempt;

    public QuizCompletedEvent(Object source, QuizAttempt attempt) {
        super(source);
        this.attempt = attempt;
    }

    public QuizAttempt getAttempt() {
        return attempt;
    }
}
