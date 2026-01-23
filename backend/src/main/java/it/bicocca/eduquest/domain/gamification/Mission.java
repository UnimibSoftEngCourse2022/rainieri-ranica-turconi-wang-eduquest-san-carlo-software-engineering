package it.bicocca.eduquest.domain.gamification;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) //Create separate tables for each mission type
public abstract class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String title;
    protected String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "badge_id", referencedColumnName = "id")
    protected Badge badge;

    protected Mission() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Badge getBadge() {
		return badge;
	}

	public void setBadge(Badge badge) {
		this.badge = badge;
	}

}