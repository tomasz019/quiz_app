package com.example.quiz_app.service;

import com.example.quiz_app.model.Quiz;
import com.example.quiz_app.model.User;
import com.example.quiz_app.repository.QuizRepository;
import com.example.quiz_app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Quiz> findOwnedQuizzes(String ownerEmail) {
        return quizRepository.findAllByOwnerEmailOrderByCreatedAtDesc(ownerEmail);
    }

    @Transactional(readOnly = true)
    public Quiz findOwnedQuiz(Long quizId, String ownerEmail) {
        return quizRepository.findByIdAndOwnerEmail(quizId, ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Quiz nie istnieje lub nie należy do tego użytkownika."));
    }

    public Quiz createQuiz(String title, String ownerEmail) {
        User owner = findUser(ownerEmail);
        return quizRepository.save(new Quiz(validateTitle(title), owner));
    }

    public Quiz updateQuiz(Long quizId, String title, String ownerEmail) {
        Quiz quiz = findOwnedQuiz(quizId, ownerEmail);
        quiz.setTitle(validateTitle(title));
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long quizId, String ownerEmail) {
        Quiz quiz = findOwnedQuiz(quizId, ownerEmail);
        quizRepository.delete(quiz);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono właściciela quizu."));
    }

    private String validateTitle(String title) {
        if (title == null || title.isBlank() || title.length() > 255) {
            throw new IllegalArgumentException("Tytuł quizu jest wymagany i może mieć maksymalnie 255 znaków.");
        }
        return title.trim();
    }
}
