package it.bicocca.eduquest.domain.quiz;

// 1. AGGIUNGI QUESTI IMPORT (Se vedi 'javax', cancellali e metti questi)
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// 2. AGGIUNGI QUESTA SCRITTA SOPRA LA CLASSE
@Entity
public class ClosedQuestionOption {

    // 3. ASSICURATI CHE L'ID ABBIA QUESTE DUE RIGHE SOPRA
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ... lascia il resto del codice (getter, setter, altri campi) così com'è ...
}