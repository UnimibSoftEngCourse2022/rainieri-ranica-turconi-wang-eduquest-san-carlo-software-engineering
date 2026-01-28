class AddQuestion extends HTMLElement {
    constructor() {
        super();
        this.currentType = null; 
    }

    connectedCallback() {
        this.renderSelectionScreen();
    }

    closeComponent() {
        this.dispatchEvent(new CustomEvent("operation-completed", {
            bubbles: true,
            composed: true
        }));
    }

    renderSelectionScreen() {
        this.innerHTML = `
        <div class="card shadow-sm" style="max-width: 700px; margin: 2rem auto;">
            <div class="card-header bg-white text-center">
                <h1 class="h4 mt-2">Select Question Type</h1>
            </div>
            <div class="card-body text-center p-5">
                <div class="d-grid gap-3 col-md-8 mx-auto">
                    <button class="btn btn-outline-primary btn-lg p-3" id="btn-open">
                        Open Question
                    </button>
                    
                    <div class="input-group">
                        <button class="btn btn-outline-warning btn-lg flex-grow-1 p-3" id="btn-multi">
                            Multiple Choice / True-False
                        </button>
                        <input type="number" class="form-control" id="option-count" value="4" min="2" max="10" style="max-width: 80px; text-align: center;" title="Number of options">
                    </div>
                    <small class="text-muted">Enter 2 in the number box to create a True/False question</small>
                </div>
            </div>
            <div class="card-footer bg-white text-end">
                 <button class="btn btn-secondary" id="cancel-selection">Cancel</button>
            </div>
        </div>
        `;

        this.querySelector('#btn-open').addEventListener('click', () => this.renderForm('OPEN'));
        
        this.querySelector('#btn-multi').addEventListener('click', () => {
            const count = this.querySelector('#option-count').value;
            this.renderForm('MULTIPLE_CHOICE', parseInt(count));
        });

        this.querySelector('#cancel-selection').addEventListener('click', () => this.closeComponent());
    }

    renderForm(type, optionCount = 0) {
        this.currentType = type;
        
        let html = `
        <div class="card shadow-sm" style="max-width: 700px; margin: 2rem auto;">
            <div class="card-header bg-white d-flex justify-content-between align-items-center">
                <h1 class="h4 mt-2 mb-0">Add Question</h1>
                <button class="btn btn-sm btn-outline-secondary" id="back-selection">Change Type</button>
            </div>
            <div class="card-body">
                
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Topic</label>
                        <input type="text" class="form-control" id="question-topic" placeholder="Ex: Math, History...">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Difficulty</label>
                        <select class="form-select" id="question-difficulty">
                            <option value="EASY">Easy</option>
                            <option value="MEDIUM">Medium</option>
                            <option value="HARD">Hard</option>
                        </select>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">Question Text</label>
                    <textarea class="form-control" id="question-text" rows="3" placeholder="Type your question here..."></textarea>
                </div>
        `;

        if (type === 'OPEN') {
            html += `<div class="alert alert-info">This question requires a free text answer.</div>`;
        } 
        
        else if (type === 'MULTIPLE_CHOICE') {
            html += `<label class="form-label fw-bold">Options</label>`;
            
            for (let i = 0; i < optionCount; i++) {
                const letter = String.fromCharCode(65 + i); 
                const placeholder = (optionCount === 2 && i === 0) ? "Ex: True" : 
                                    (optionCount === 2 && i === 1) ? "Ex: False" : 
                                    `Option ${letter}`;

                html += `
                <div class="input-group mb-2">
                    <span class="input-group-text fw-bold">${letter}</span>
                    <input type="text" class="form-control option-input" data-letter="${letter}" placeholder="${placeholder}">
                </div>`;
            }

            html += `
            <div class="mb-3 mt-3">
                <label class="form-label fw-bold">Correct Answer</label>
                <select class="form-select" id="correct-answer">
            `;
            for (let i = 0; i < optionCount; i++) {
                const letter = String.fromCharCode(65 + i);
                html += `<option value="${letter}">Option ${letter}</option>`;
            }
            html += `</select></div>`;
        }

        html += `
            </div>
            <div class="card-footer d-flex justify-content-end gap-2 bg-white">
                <button type="button" class="btn btn-secondary" id="cancel-btn">Cancel</button>
                <button type="button" class="btn btn-primary" id="save-btn">Save Question</button>
            </div>
        </div>
        `;

        this.innerHTML = html;

        this.querySelector('#back-selection').addEventListener('click', () => this.renderSelectionScreen());
        this.querySelector('#save-btn').addEventListener('click', () => this.saveQuestion(type));
        this.querySelector('#cancel-btn').addEventListener('click', () => this.closeComponent());
    }

    async saveQuestion(type) {
        const questionText = this.querySelector('#question-text').value;
        const topic = this.querySelector('#question-topic').value;
        const difficulty = this.querySelector('#question-difficulty').value;

        if (!questionText.trim() || !topic.trim()) {
            alert("Please enter the question text and topic!");
            return;
        }

        let questionDTO = {
            text: questionText,
            topic: topic,
            difficulty: difficulty,
            questionType: type,
            validAnswersOpenQuestion: [], 
            closedQuestionOptions: []
        };

        if (type === 'MULTIPLE_CHOICE') {
            const inputs = this.querySelectorAll('.option-input');
            const selectedCorrectLetter = this.querySelector('#correct-answer').value;
            
            let optionsList = [];
            let allFilled = true;

            inputs.forEach(input => {
                if(!input.value.trim()) allFilled = false;
                
                const currentLetter = input.dataset.letter;
                const isThisTrue = (currentLetter === selectedCorrectLetter);

                optionsList.push({
                    text: input.value,
                    isTrue: isThisTrue
                });
            });

            if (!allFilled) {
                alert("Please fill in all answer options!");
                return;
            }

            questionDTO.closedQuestionOptions = optionsList;
        }

        const jwt = localStorage.getItem("token");
        const API_URL = "http://localhost:8080/api/question";

        if (!jwt) {
            alert("Error: You are not logged in!");
            window.location.href = "../login/index.html";
            return;
        }

        try {
            const response = await fetch(API_URL, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + jwt
                },
                body: JSON.stringify(questionDTO)
            });

            if (response.ok) {
                alert("Question saved successfully!");
                this.closeComponent();
            } else {
                const errorText = await response.text();
                console.error("Server error:", errorText);
                alert("Error saving question: " + response.status);
            }
        } catch (error) {
            console.error("Connection error:", error);
            alert("Unable to contact the server.");
        }
    }
}

customElements.define('add-question', AddQuestion);