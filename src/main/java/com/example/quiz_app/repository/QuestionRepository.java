package com.example.quiz_app.repository;

import com.example.quiz_app.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findAllByQuizIdAndQuizOwnerEmailOrderByIdAsc(Long quizId, String ownerEmail);

    Optional<Question> findByIdAndQuizIdAndQuizOwnerEmail(
            Long questionId,
            Long quizId,
            String ownerEmail);
}
