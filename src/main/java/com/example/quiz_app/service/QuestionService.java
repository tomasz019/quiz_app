package com.example.quiz_app.service;

import com.example.quiz_app.model.Question;
import com.example.quiz_app.model.Quiz;
import com.example.quiz_app.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizService quizService;

    public QuestionService(QuestionRepository questionRepository, QuizService quizService) {
        this.questionRepository = questionRepository;
        this.quizService = quizService;
    }

    @Transactional(readOnly = true)
    public List<Question> findQuestionsForQuiz(Long quizId, String ownerEmail) {
        // if quiz does not exist or does not belong to the user, this will throw an exception
        quizService.findOwnedQuiz(quizId, ownerEmail);
        return questionRepository.findAllByQuizIdAndQuizOwnerEmailOrderByIdAsc(quizId, ownerEmail);
    }

    @Transactional(readOnly = true)
    public Question findQuestion(Long questionId, Long quizId, String ownerEmail) {
        quizService.findOwnedQuiz(quizId, ownerEmail);
        return questionRepository.findByIdAndQuizIdAndQuizOwnerEmail(questionId, quizId, ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pytanie nie istnieje lub nie należy do tego quizu."));
    }

    public Question createQuestion(
            Long quizId,
            String content,
            Integer timeLimit,
            String ownerEmail) {
        Quiz quiz = quizService.findOwnedQuiz(quizId, ownerEmail);
        return questionRepository.save(new Question(
                quiz,
                validateContent(content),
                validateTimeLimit(timeLimit)));
    }

    public Question updateQuestion(
            Long questionId,
            Long quizId,
            String content,
            Integer timeLimit,
            String ownerEmail) {
        Question question = findQuestion(questionId, quizId, ownerEmail);
        question.setContent(validateContent(content));
        question.setTimeLimit(validateTimeLimit(timeLimit));
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long questionId, Long quizId, String ownerEmail) {
        questionRepository.delete(findQuestion(questionId, quizId, ownerEmail));
    }

    private String validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Treść pytania jest wymagana.");
        }
        return content.trim();
    }

    private Integer validateTimeLimit(Integer timeLimit) {
        if (timeLimit == null || timeLimit <= 0) {
            throw new IllegalArgumentException("Limit czasu musi być większy od 0 sekund.");
        }
        return timeLimit;
    }
}
