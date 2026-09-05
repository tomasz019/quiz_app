package com.example.quiz_app.repository;

import com.example.quiz_app.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByOwnerEmailOrderByCreatedAtDesc(String email);

    Optional<Quiz> findByIdAndOwnerEmail(Long id, String email);
}
