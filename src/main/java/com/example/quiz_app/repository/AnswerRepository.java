package com.example.quiz_app.repository;

import com.example.quiz_app.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAllByQuestionIdAndQuestionQuizOwnerEmailOrderByIdAsc(
            Long questionId, String ownerEmail);

    Optional<Answer> findByIdAndQuestionIdAndQuestionQuizOwnerEmail(
            Long answerId, Long questionId, String ownerEmail);
}
