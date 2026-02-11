package it.bicocca.eduquest.dto.gamification;

import java.time.LocalDate;

public class BadgeDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate obtainedDate;

    public BadgeDTO(Long id, String name, String description, LocalDate obtainedDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.obtainedDate = obtainedDate;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getObtainedDate() { return obtainedDate; }
}
