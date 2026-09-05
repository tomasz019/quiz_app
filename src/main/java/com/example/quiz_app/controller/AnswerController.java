package com.example.quiz_app.controller;

import com.example.quiz_app.service.AnswerService;
import com.example.quiz_app.service.QuestionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/quizzes/{quizId}/questions/{questionId}/answers")
public class AnswerController {

    private final AnswerService answerService;
    private final QuestionService questionService;

    public AnswerController(AnswerService answerService, QuestionService questionService) {
        this.answerService = answerService;
        this.questionService = questionService;
    }

    @GetMapping
    public String form(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserDetails user,
            Model model) {
        model.addAttribute("question",
                questionService.findQuestion(questionId, quizId, user.getUsername()));
        model.addAttribute("answers",
                answerService.findAnswers(questionId, quizId, user.getUsername()));
        return "answers_add";
    }

    @PostMapping
    public String create(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @RequestParam("answerTexts") List<String> answerTexts,
            @RequestParam(value = "correctIndexes", required = false) List<Integer> correctIndexes,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes) {
        try {
            answerService.createAnswers(
                    questionId, quizId, answerTexts, correctIndexes, user.getUsername());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/quizzes/" + quizId + "/questions/" + questionId + "/answers";
    }

    @GetMapping("/{answerId}/edit")
    public String editForm(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @PathVariable Long answerId,
            @AuthenticationPrincipal UserDetails user,
            Model model) {
        model.addAttribute("question",
                questionService.findQuestion(questionId, quizId, user.getUsername()));
        model.addAttribute("answers",
                answerService.findAnswers(questionId, quizId, user.getUsername()));
        model.addAttribute("editAnswer",
                answerService.findAnswer(answerId, questionId, quizId, user.getUsername()));
        return "answers_add";
    }

    @PostMapping("/{answerId}")
    public String update(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @PathVariable Long answerId,
            @RequestParam String text,
            @RequestParam(defaultValue = "false") boolean isCorrect,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes) {
        try {
            answerService.updateAnswer(
                    answerId, questionId, quizId, text, isCorrect, user.getUsername());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/quizzes/" + quizId + "/questions/" + questionId + "/answers";
    }

    @PostMapping("/{answerId}/delete")
    public String delete(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @PathVariable Long answerId,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes) {
        try {
            answerService.deleteAnswer(answerId, questionId, quizId, user.getUsername());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/quizzes/" + quizId + "/questions/" + questionId + "/answers";
    }
}
