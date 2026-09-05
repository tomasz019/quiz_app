package com.example.quiz_app.service;

import com.example.quiz_app.model.Answer;
import com.example.quiz_app.model.Question;
import com.example.quiz_app.repository.AnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionService questionService;

    public AnswerService(
            AnswerRepository answerRepository,
            QuestionService questionService) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
    }

    @Transactional(readOnly = true)
    public List<Answer> findAnswers(Long questionId, Long quizId, String ownerEmail) {
        questionService.findQuestion(questionId, quizId, ownerEmail);
        return answerRepository.findAllByQuestionIdAndQuestionQuizOwnerEmailOrderByIdAsc(
                questionId, ownerEmail);
    }

    @Transactional
    public List<Answer> createAnswers(
            Long questionId,
            Long quizId,
            List<String> texts,
            List<Integer> correctIndexes,
            String ownerEmail) {
        Question question = questionService.findQuestion(questionId, quizId, ownerEmail);
        if (texts == null || texts.isEmpty()) {
            throw new IllegalArgumentException("Dodaj co najmniej jedną odpowiedź.");
        }

        List<Answer> answers = new ArrayList<>();
        for (int index = 0; index < texts.size(); index++) {
            answers.add(new Answer(
                    question,
                    validateText(texts.get(index)),
                    correctIndexes != null && correctIndexes.contains(index)));
        }
        return answerRepository.saveAll(answers);
    }

    @Transactional
    public Answer updateAnswer(
            Long answerId,
            Long questionId,
            Long quizId,
            String text,
            boolean isCorrect,
            String ownerEmail) {
        Answer answer = findAnswer(answerId, questionId, quizId, ownerEmail);
        answer.setText(validateText(text));
        answer.setCorrect(isCorrect);
        return answerRepository.save(answer);
    }

    @Transactional
    public void deleteAnswer(
            Long answerId, Long questionId, Long quizId, String ownerEmail) {
        answerRepository.delete(findAnswer(answerId, questionId, quizId, ownerEmail));
    }

    @Transactional(readOnly = true)
    public Answer findAnswer(
            Long answerId, Long questionId, Long quizId, String ownerEmail) {
        questionService.findQuestion(questionId, quizId, ownerEmail);
        return answerRepository.findByIdAndQuestionIdAndQuestionQuizOwnerEmail(
                        answerId, questionId, ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Odpowiedź nie istnieje lub nie należy do tego pytania."));
    }

    private String validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Treść odpowiedzi jest wymagana.");
        }
        return text.trim();
    }
}
