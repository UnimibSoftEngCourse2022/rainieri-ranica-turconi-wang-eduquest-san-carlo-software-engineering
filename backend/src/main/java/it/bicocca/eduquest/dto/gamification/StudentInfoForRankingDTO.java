package it.bicocca.eduquest.dto.gamification;

public class StudentInfoForRankingDTO {
	private long id;
	private String name;
	private String surname;
	private double value;

	public StudentInfoForRankingDTO(long id, String name, String surname, double value) {
		this.name = name;
		this.surname = surname;
		this.value = value;
	}

	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}
	
	public double getValue() {
		return value;
	}
}
